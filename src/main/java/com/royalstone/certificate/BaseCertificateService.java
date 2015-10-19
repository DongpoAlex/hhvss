/**
 * 
 */
package com.royalstone.certificate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.certificate.dao.CertificateCategoryDAO;
import com.royalstone.certificate.dao.CertificateRelationDAO;
import com.royalstone.certificate.dao.CertificateTypeDAO;
import com.royalstone.certificate.dao.VenderCategoryRelationDAO;
import com.royalstone.security.Permission;
import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;

/**
 * @author BaiJian
 * 
 */
public class BaseCertificateService {
	final private HttpServletRequest	request;
	final private Token 				token;
	final private Connection			conn;

	/**
	 * @param request
	 * @param conn
	 */
	public BaseCertificateService(HttpServletRequest request, Connection conn,Token token) {
		super();
		this.request = request;
		this.conn = conn;
		this.token = token;
	}

	public Element getCTList() throws SQLException, IOException {
		CertificateTypeDAO dao = new CertificateTypeDAO(conn);

		return dao.getList();
	}

	public Element getCTList(int flag) throws SQLException, IOException {
		CertificateTypeDAO dao = new CertificateTypeDAO(conn);

		return dao.getList(flag);
	}

	public Element getCCList() throws SQLException {
		CertificateCategoryDAO dao = new CertificateCategoryDAO(conn);

		return dao.getList();
	}

	public Element getCTCList() throws SQLException {
		String by = this.request.getParameter("by");
		int id = Integer.parseInt(this.request.getParameter("id"));
		CertificateRelationDAO dao = new CertificateRelationDAO(conn);
		Element elm = null;
		if ("ccid".equals(by)) {
			elm = dao.getListByCcid(id);
		} else {
			elm = dao.getListByCtid(id);
		}
		return elm;
	}

	public Element getCTCByCCID() throws SQLException {
		int ccid = Integer.parseInt(request.getParameter("ccid"));
		CertificateRelationDAO dao = new CertificateRelationDAO(conn);
		return dao.getCTCByCCID(ccid);
	}

	public void addCT() throws SQLException, NamingException, PermissionException, UnsupportedEncodingException {
		token.checkPermission(8000011, Permission.INSERT);
		String ctname = new String(request.getParameter("ctname").getBytes("ISO8859-1"),"UTF-8");
		String note = new String(request.getParameter("note").getBytes("ISO8859-1"),"UTF-8");
		int flag = Integer.parseInt(request.getParameter("flag"));
		int yearFlag = Integer.parseInt(request.getParameter("yearFlag"));
		CertificateTypeDAO dao = new CertificateTypeDAO(conn);
		int appFlag = Integer.parseInt(request.getParameter("appFlag"));
		int whFlag= Integer.parseInt(request.getParameter("whFlag"));
		dao.insert(ctname, note, flag, yearFlag,appFlag,whFlag);
	}

	public void addCC() throws SQLException, NamingException, PermissionException, UnsupportedEncodingException {
		token.checkPermission(8000011, Permission.INSERT);
		String ccname = new String(request.getParameter("ccname").getBytes("ISO8859-1"),"UTF-8");
		String note = new String(request.getParameter("note").getBytes("ISO8859-1"),"UTF-8");
		CertificateCategoryDAO dao = new CertificateCategoryDAO(conn);
		dao.insert(ccname, note);
	}

	public void updateCT() throws SQLException, NamingException, PermissionException, UnsupportedEncodingException {
		token.checkPermission(8000011, Permission.EDIT);
		int ctid = Integer.parseInt(request.getParameter("ctid"));
		String ctname =  new String(request.getParameter("ctname").getBytes("ISO8859-1"),"UTF-8");
		String note = new String(request.getParameter("note").getBytes("ISO8859-1"),"UTF-8");
		int flag = Integer.parseInt(request.getParameter("flag"));
		int yearFlag = Integer.parseInt(request.getParameter("yearFlag"));
		int appFlag = Integer.parseInt(request.getParameter("appFlag"));
		int whFlag = Integer.parseInt(request.getParameter("whFlag"));
		CertificateTypeDAO dao = new CertificateTypeDAO(conn);
		dao.update(ctname, note, flag, yearFlag,appFlag,whFlag,ctid);
	}

	public void updateCC() throws SQLException, NamingException, PermissionException, UnsupportedEncodingException {
		token.checkPermission(8000011, Permission.EDIT);
		int ccid = Integer.parseInt(request.getParameter("ccid"));
		String ccname = new String(request.getParameter("ccname").getBytes("ISO8859-1"),"UTF-8");
		String note = new String(request.getParameter("note").getBytes("ISO8859-1"),"UTF-8");
		CertificateCategoryDAO dao = new CertificateCategoryDAO(conn);
		dao.update(ccname, note, ccid);
	}

	/**
	 * 閫氶敓鏂ゆ嫹鍝侀敓鏂ゆ嫹缁撮敓鏂ゆ嫹 鍝侀敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻緝锟�
	 * 
	 * @throws SQLException
	 * @throws PermissionException 
	 * @throws NamingException 
	 */
	public void insertCTCByCCID() throws SQLException, NamingException, PermissionException {
		token.checkPermission(8000011, Permission.INSERT);
		int ccid = Integer.parseInt(request.getParameter("ccid"));
		int ctid = Integer.parseInt(request.getParameter("ctid"));
		int flag = Integer.parseInt(request.getParameter("flag"));

		CertificateRelationDAO dao = new CertificateRelationDAO(conn);
		try {
			conn.setAutoCommit(false);
			dao.del(ctid, ccid);
			dao.insert(ctid, ccid, flag, "");
			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
		}
		finally {
			conn.setAutoCommit(true);
		}

	}

	/**
	 * 鍒犻敓鏂ゆ嫹鏌愰敓鏂ゆ嫹鍝侀敓鏂ゆ嫹閿熻緝锟�
	 * 
	 * @throws SQLException
	 * @throws PermissionException 
	 * @throws NamingException 
	 */
	public void delCTCByCCID() throws SQLException, NamingException, PermissionException {
		token.checkPermission(8000011, Permission.DELETE);
		int ccid = Integer.parseInt(request.getParameter("ccid"));
		int ctid = Integer.parseInt(request.getParameter("ctid"));

		CertificateRelationDAO dao = new CertificateRelationDAO(conn);
		dao.del(ctid, ccid);

	}

	/**
	 * 閿熸枻鎷烽敓閾扮櫢鎷峰簲閿熸枻鎷疯瘉閿熸枻鎷烽敓鏂ゆ嫹閿熼叺鐧告嫹绯�
	 * 
	 * @param elm
	 * @throws SQLException
	 * @throws PermissionException 
	 * @throws NamingException 
	 */
	public void updateVenderRelation(Element elm) throws SQLException, NamingException, PermissionException {
		token.checkPermission(8000011, Permission.EDIT);
		VenderCategoryRelationDAO dao = new VenderCategoryRelationDAO(conn);
		Element elmHead = elm.getChild("head");
		String venderid = elmHead.getChildText("venderid");
		int ccid = Integer.parseInt(elmHead.getChildText("ccid"));

		Element elmDataSet = elm.getChild("dataset");
		List list = elmDataSet.getChildren("row");

		try {
			conn.setAutoCommit(false);
			// 寰敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
			dao.delete(venderid, ccid);
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Element tempElm = (Element) iterator.next();
				int ctid = Integer.parseInt(tempElm.getChildText("ctid"));
				dao.insert(venderid, ctid, ccid);
			}

			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	public void updateVenderRelationByCC(Element elm) throws SQLException, NamingException, PermissionException {
		token.checkPermission(8000011, Permission.EDIT);
		VenderCategoryRelationDAO dao = new VenderCategoryRelationDAO(conn);
		Element elmHead = elm.getChild("head");
		String venderid = elmHead.getChildText("venderid");

		Element elmDataSet = elm.getChild("dataset");
		List list = elmDataSet.getChildren("row");

		try {
			conn.setAutoCommit(false);
			dao.delete(venderid);
			// 寰敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Element tempElm = (Element) iterator.next();
				int ccid = Integer.parseInt(tempElm.getChildText("ccid"));
				dao.insert(venderid, ccid);
			}

			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	public Element getVRCCList(String venderid) throws SQLException {
		VenderCategoryRelationDAO dao = new VenderCategoryRelationDAO(conn);

		return dao.getCCList(venderid);
	}

	public Element getVenderRelation() throws SQLException {
		return new VenderCategoryRelationDAO(conn).getList(request.getParameterMap());
	}

	public Element getVendersByCCID() throws SQLException {
		int ccid = Integer.parseInt(request.getParameter("ccid"));
		VenderCategoryRelationDAO dao = new VenderCategoryRelationDAO(conn);

		return dao.getVendersByCCID(ccid);
	}
}
