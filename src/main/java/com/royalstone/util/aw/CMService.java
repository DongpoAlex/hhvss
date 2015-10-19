package com.royalstone.util.aw;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUnit;

public class CMService {
	final private Connection	conn;
	final private Token			token;
	final private String		cmid;
	final private int			sid;

	public CMService(Connection conn, Token token, String cmid) {
		super();
		this.conn = conn;
		this.token = token;
		this.cmid = cmid;
		if(cmid!=null && cmid.indexOf("30203")==0){
			sid=0;
		}else{
			sid = token.site.getSid();
		}
	}

	public String makeCMID(int moduleid) throws SQLException {
		ColModelDAO dao = new ColModelDAO(conn);
		return dao.makeCMID(moduleid);

	}

	public Element preview() throws InvalidDataException, NamingException, SQLException {

		// 获取sql语句
		String smid = ColModelLoader.getInstance().getCM(sid, cmid).getSmid();
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(sid, smid);

		String sql_where = " rownum<=100 ";

		Element elm = null;

		elm = XColModelAdapter.getAwReportRowSetElement(conn, sid, sqlUnit.toString(sql_where), cmid,
				1, 100);

		return elm;
	}

	public Element initHtml() throws InvalidDataException, NamingException, SQLException {
		Element elm = null;
		elm = XColModelAdapter.getAwReportDef(conn, sid, cmid);

		return elm;
	}

	public Element getCMInfo() throws InvalidDataException, NamingException, SQLException {
		Element elm = null;
		elm = XColModelAdapter.getAwReportDef(conn, sid, cmid);
		return elm;
	}

	public void updateCM(Document doc) throws NamingException, SQLException, InvalidDataException {
		try {
			Element elmRoot = doc.getRootElement();
			Element elmHead = elmRoot.getChild("head");
			Element elmRowSet = elmRoot.getChild("rowset");
			String title = elmHead.getChildText("title");
			String footer = elmHead.getChildText("footer");
			String note = elmHead.getChildText("note");
			String xslprint = elmHead.getChildText("xslprint");
			String xslview = elmHead.getChildText("xslview");
			String temp = elmHead.getChildText("moduleid");
			long moduleid = Long.parseLong(temp);
			temp = elmHead.getChildText("smid");
			if (temp.length() == 0)
				temp = "0";
			String smid = temp;
			temp = elmHead.getChildText("smidhead");
			if (temp.length() == 0)
				temp = "0";
			String smidhead = temp;
			temp = elmHead.getChildText("smidbody");
			if (temp.length() == 0)
				temp = "0";
			String smidbody = temp;

			// 没有使用clone，直接修改对象，如果数据库操作失败，可能会有问题
			// 如果cmid不存在，返回默认cm
			ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);

			// cm没有则为首次定义的cm
			if (cm == null) {
				cm = new ColModel();
				cm.setCmid(cmid);
				cm.setCmDetailList(new ArrayList());
				cm.setModuleid(moduleid);
				cm.setSearchList(new ArrayList());
			}

			// 得到的cm和操作的cmid不同，则表明是新增cm
			if (cmid != cm.getCmid()) {
				// 新cm，则使用新的cm
				cm = cm.clone();
				cm.setCmid(cmid);
			}

			cm.setSmid(smid);
			cm.setHsmid(smidhead);
			cm.setBsmid(smidbody);
			cm.setTitle(title);
			cm.setFooter(footer);
			cm.setNote(note);
			cm.setXslPrint(xslprint);
			cm.setXslView(xslview);

			List<ColModelDetail> listDetail = cm.getCmDetailList();
			listDetail.clear();
			List listRow = elmRowSet.getChildren();
			for (Iterator it = listRow.iterator(); it.hasNext();) {
				Element elm = (Element) it.next();
				ColModelDetail detail = new ColModelDetail();
				String seqno = elm.getChildTextTrim("seqno");
				String field = elm.getChildTextTrim("field");
				String name = elm.getChildTextTrim("name");
				String vtype = elm.getChildTextTrim("vtype");
				String width = elm.getChildTextTrim("width");
				String sum = elm.getChildTextTrim("sum");
				String css = elm.getChildTextTrim("css");
				String render = elm.getChildTextTrim("render");
				String dnote = elm.getChildTextTrim("note");
				dnote = dnote == null ? "" : dnote;
				detail.setCmid(cmid);
				detail.setSeqno(Integer.valueOf(seqno));
				detail.setField(field);
				detail.setName(name);
				detail.setVtype(vtype);
				detail.setWidth(width);
				detail.setSum(Integer.valueOf(sum));
				detail.setCss(css);
				detail.setRender(render);
				detail.setNote(dnote);
				listDetail.add(detail);
			}

			// 对list进行排序
			CMDetailComparatorBySeqno comparator = new CMDetailComparatorBySeqno();
			Collections.sort(listDetail, comparator);

			conn.setAutoCommit(false);
			ColModelDAO dao = new ColModelDAO(conn);
			dao.delHead(cm.getCmid());
			dao.addHead(cm);
			dao.delAllCMDetail(cm.getCmid());
			dao.addCMDetail(cm.getCmDetailList());

			ColModelLoader.getInstance().addCM(sid, cm);

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}

	}
}
