/**
 * 
 */
package com.royalstone.certificate;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.certificate.bean.Certificate;
import com.royalstone.certificate.bean.CertificateItem;
import com.royalstone.certificate.bean.Config;
import com.royalstone.certificate.bean.Image;
import com.royalstone.certificate.dao.CertificateDAO;
import com.royalstone.certificate.dao.TaskDao;
import com.royalstone.certificate.util.FileHandle;
import com.royalstone.common.Sheetid;
import com.royalstone.security.Permission;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.vss.vender.VenderExt;

/**
 * @author BaiJian
 */
public class CertificateService {
	final private HttpServletRequest request;

	final private Connection conn;

	final private Token token;

	Permission perm;

	/**
	 * 将请求参数转为证照表头
	 * 
	 * @return
	 */
	public Certificate cookHead() {
		Certificate c = new Certificate();
		String tmpInt = null;
		c.setSheetid(request.getParameter("sheetid"));
		c.setVenderid(request.getParameter("venderid"));

		tmpInt = request.getParameter("type");
		if (tmpInt == null || tmpInt.length() == 0) {
			c.setType(-1);
		} else {
			c.setType(Integer.parseInt(tmpInt));
		}

		c.setContact(request.getParameter("contact"));

		tmpInt = request.getParameter("ccid");
		if (tmpInt == null || tmpInt.length() == 0) {
			c.setCategoryid(-1);
		} else {
			c.setCategoryid(Integer.parseInt(tmpInt));
		}

		tmpInt = request.getParameter("flag");
		if (tmpInt == null || tmpInt.length() == 0) {
			c.setFlag(0);
		} else {
			c.setFlag(Integer.parseInt(tmpInt));
		}

		c.setChecker(request.getParameter("checker"));
		c.setNote(request.getParameter("note"));

		tmpInt = request.getParameter("venderType");
		if (tmpInt == null || tmpInt.length() == 0) {
			c.setVenderType(-1);
		} else {
			c.setVenderType(Integer.parseInt(tmpInt));
		}

		String tmpStr = request.getParameter("venderTypeName");
		tmpStr = tmpStr == null ? "" : tmpStr;
		c.setVenderTypeName(tmpStr);
		return c;
	}

	/**
	 * 将前台表体参数转为证照表体
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InvalidDataException
	 */
	public CertificateItem cookItem() throws ParseException,
			InvalidDataException {
		String tmpInt = null;
		CertificateItem i = new CertificateItem();
		i.setSheetid(request.getParameter("sheetid"));
		i.setCertificateID(request.getParameter("certificateID"));
		i.setCertificateName(request.getParameter("certificateName"));

		tmpInt = request.getParameter("ctid");
		if (tmpInt == null || tmpInt.length() == 0) {
			i.setCtid(-1);
		} else {
			i.setCtid(Integer.parseInt(tmpInt));
		}

		tmpInt = request.getParameter("seqno");
		if (tmpInt == null || tmpInt.length() == 0) {
			i.setSeqno(-1);
		} else {
			i.setSeqno(Integer.parseInt(tmpInt));
		}

		tmpInt = request.getParameter("flag");
		if (tmpInt == null || tmpInt.length() == 0) {
			i.setFlag(0);
		} else {
			i.setFlag(Integer.parseInt(tmpInt));
		}

		i.setExpiryDate(request.getParameter("expiryDate"));

		i.setYearDate(request.getParameter("yearDate"));

		// i.setWhDate(request.getParameter("whDate"));

		i.setEditor(request.getParameter("editor"));

		i.setGoodsName(request.getParameter("goodsName"));

		i.setBarcodeid(request.getParameter("barcodeid"));

		i.setNote(request.getParameter("note"));

		i.setApprovalnum(request.getParameter("approvalnum"));

		i.setPapprovalnum(request.getParameter("papprovalnum"));

		return i;
	}

	/**
	 * 将前台证照图片参数转为图片对象
	 * 
	 * @return
	 */
	public Image cookImage() {
		String tmpInt = null;
		Image i = new Image();
		i.setSheetid(request.getParameter("sheetid"));
		i.setImgFileName(request.getParameter("filename"));
		tmpInt = request.getParameter("seqno");
		if (tmpInt == null || tmpInt.length() == 0) {
			i.setSeqno(-1);
		} else {
			i.setSeqno(Integer.parseInt(tmpInt));
		}

		tmpInt = request.getParameter("imgseqno");
		if (tmpInt == null || tmpInt.length() == 0) {
			i.setImgseqno(-1);
		} else {
			i.setImgseqno(Integer.parseInt(tmpInt));
		}

		return i;
	}

	/**
	 * 插入一张图片
	 * 
	 * @throws SQLException
	 */
	public void addImage() throws SQLException {
		Image i = cookImage();
		CertificateDAO dao = new CertificateDAO(conn);
		dao.addImg(i);
	}

	/**
	 * 修改图片
	 * 
	 * @throws SQLException
	 */
	public void editImage() throws SQLException {
		Image i = cookImage();
		CertificateDAO dao = new CertificateDAO(conn);
		dao.updateImg(i);
	}

	/**
	 * 删除图片
	 * 
	 * @throws SQLException
	 */
	public void delImage() throws SQLException {
		Image i = cookImage();
		CertificateDAO dao = new CertificateDAO(conn);
		dao.delImg(i);
	}

	/**
	 * 获取图片
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Element getImage() throws SQLException {
		Image i = cookImage();
		CertificateDAO dao = new CertificateDAO(conn);
		return dao.getImg(i);
	}

	/**
	 * 获取指定单据图片列表
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Element getImageList() throws SQLException {
		String sheetid = request.getParameter("sheetid");
		String tmpInt = request.getParameter("seqno");
		int seqno = -1;
		if (tmpInt == null || tmpInt.length() == 0) {
		} else {
			seqno = Integer.parseInt(tmpInt);
		}
		return getImgList(sheetid, seqno);
	}

	private Element getImgList(String sheetid, int seqno) {
		CertificateDAO dao = new CertificateDAO(conn);
		return dao.getImgList(sheetid, seqno);
	}

	/**
	 * 增加一张单据头
	 * 
	 * @return
	 * @throws Exception
	 */
	public String addHead() throws Exception {
		Certificate c = cookHead();
		String sheetid = "";
		// 添加
		sheetid = Sheetid.getSheetid(conn, 8001, "");
		c.setSheetid(sheetid);
		c.setVenderid(token.getBusinessid());
		c.setFlag(0);

		try {
			conn.setAutoCommit(false);
			addHead(c);
			conn.commit();
			return sheetid;
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 修改供应商类型名称
	 * 
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public void updateVenderTypeName() throws InvalidDataException,
			SQLException {
		Certificate c = cookHead();
		if (c.getSheetid() == null)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");

		updateVenderTypeName(c);
	}

	/**
	 * 修改单据表头
	 * 
	 * @throws ParseException
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public void editHead() throws ParseException, InvalidDataException,
			SQLException {
		Certificate c = cookHead();

		if (c.getSheetid() == null)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");

		c.setType(1);
		c.setVenderid(token.getBusinessid());
		editHead(c);
	}

	/**
	 * 删除一个单据
	 * 
	 * @throws Exception
	 */
	public void delSheet() throws Exception {
		token.checkPermission(8000003, Permission.DELETE);
		Certificate c = cookHead();
		if (c.getSheetid() == null)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");
		if (isSheetChecked(c.getSheetid())) {
			throw new InvalidDataException("该单据含有有效的证照（已审核证照），暂时无法删除！");
		}
		try {
			conn.setAutoCommit(false);
			// 同时删除图片 ，以sheetid开头的文件。
			String venderid = token.getBusinessid();
			if (!token.isVender) {
				venderid = getVenderid(c.getSheetid());
			}
			delSheetImgs(c.getSheetid(), venderid);
			delSheet(c);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 供应商删除证照
	 * 
	 * @throws Exception
	 */
	public void venderDelSheet() throws Exception {
		token.checkPermission(8000002, Permission.DELETE);
		Certificate c = cookHead();
		if (c.getSheetid() == null)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");
		if (isSheetChecked(c.getSheetid())) {
			throw new InvalidDataException("该单据含有有效的证照（已审核证照），暂时无法删除！");
		}
		try {
			conn.setAutoCommit(false);
			// 同时删除图片 ，以sheetid开头的文件。
			delSheetImgs(c.getSheetid(), token.getBusinessid());
			delSheet(c);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 供应商删除证照商品明细
	 * 
	 * @throws Exception
	 */
	public void venderDelItem() throws Exception {
		token.checkPermission(8000002, Permission.DELETE);
		CertificateItem c = cookItem();
		if (c.getSheetid() == null)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");
		if (isSheetChecked(c.getSheetid(), c.getSeqno())) {
			throw new InvalidDataException("已审核证照，暂时无法删除！");
		}
		try {
			conn.setAutoCommit(false);
			// 同时删除图片
			delItemImgs(c.getSheetid(), c.getSeqno(), token.getBusinessid());

			delSheetItem(c);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 供应商删除证照商品
	 * 
	 * @throws Exception
	 * @throws InvalidDataException
	 */
	public void delSheetItem() throws Exception {
		token.checkPermission(8000003, Permission.DELETE);
		CertificateItem c = cookItem();
		if (c.getSheetid() == null)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");
		if (isSheetChecked(c.getSheetid())) {
			throw new InvalidDataException("该单据含有有效的证照（已审核证照），暂时无法删除！");
		}
		try {
			conn.setAutoCommit(false);
			String venderid = token.getBusinessid();
			if (!token.isVender) {
				venderid = getVenderid(c.getSheetid());
			}
			// 同时删除图片 ，以sheetid开头的文件。
			delItemImgs(c.getSheetid(), c.getSeqno(), venderid);
			delSheetItem(c);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 编辑表体
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public Element editItem() throws Exception {
		CertificateItem i = cookItem();

		Date expiryDate = i.getExpiryDate();
		Date yearDate = i.getYearDate();

		if (yearDate != null && expiryDate != null
				&& yearDate.after(expiryDate)) {
			throw new InvalidDataException("年审日期大于有效期，请修正");
		}
		if (yearDate != null && yearDate.before(new java.util.Date())) {
			throw new InvalidDataException("年审日期为下次年审的日期，不能小于等于今天，请修正");
		}
		if (i.getSheetid() == null || i.getSheetid().length() == 0)
			throw new InvalidDataException("sheetid 为空，无法定位到需要修改的数据");
		try {
			conn.setAutoCommit(false);
			editItem(i);

			CertificateDAO dao = new CertificateDAO(conn);
			// 检查是否已经有图片
			Element elm = getImgList(i.getSheetid(), i.getSeqno());
			if (!elm.hasChildren()) {
				// 如果未录入图片，且相同证照号已有图片，则自动设置图片。
				Image[] imgs = dao.getImgByCid(token.getBusinessid(),
						i.getCertificateID());
				for (int j = 0; j < imgs.length; j++) {
					Image image = imgs[j];
					image.setSheetid(i.getSheetid());
					image.setSeqno(i.getSeqno());
				}
				dao.addImg(imgs);
			}
			conn.commit();
			return getImgList(i.getSheetid(), i.getSeqno());
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	private void addHead(Certificate c) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		dao.addHead(c);
	}

	private void updateVenderTypeName(Certificate c) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		dao.updateVenderTypeName(c);
	}

	private void editHead(Certificate c) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		dao.updateHead(c);
	}

	private void editItem(CertificateItem i) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		// 先删除，再插入，外部事物
		dao.delItem(i);

		i.setFlag(0);

		dao.addItem(i);
	}

	private void delSheet(Certificate c) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		dao.delSheet(c);
	}

	private void delSheetItem(CertificateItem c) throws Exception {
		CertificateDAO dao = new CertificateDAO(conn);
		dao.delSheetItem(c);
	}

	/**
	 * 新增或覆盖图片
	 * 
	 * @throws SQLException
	 */
	public void editImg(Image i) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		try {
			conn.setAutoCommit(false);
			dao.delImg(i);
			dao.addImg(i);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
		} finally {
			conn.setAutoCommit(true);
		}

	}

	/**
	 * 供应商提交单据
	 * 
	 * @throws Exception
	 */
	public void venderSubmit() throws Exception {
		String sheetid = request.getParameter("sheetid");
		if (sheetid == null || sheetid.length() == 0) {
			throw new InvalidDataException("必须提供证照sheetid");
		}
		CertificateDAO dao = new CertificateDAO(conn);
		Element elmHead = dao.getHead(sheetid);
		Element elmItem = dao.getItem(sheetid);

		String type = elmHead.getChild("row").getChildText("type");
		List<Element> rowsList = elmItem.getChildren("row");
		int itemRows = rowsList.size();
		if (itemRows == 0) {
			throw new InvalidDataException(sheetid + "尚未录入证照，不能提交");
		}

		// 对于type=2的证照，检查表体是否已录入全部必录类型证照
		if ("2".equals(type)) {
			String ccid = elmHead.getChild("row").getChildText("ccid");
			SelCertificateCategoryType ctc = new SelCertificateCategoryType(
					token, ccid, "", "");
			HashSet<String> set = ctc.getHashSet(conn);
			HashSet<String> hasSet = new HashSet<String>();
			for (Iterator<Element> i = rowsList.iterator(); i.hasNext();) {
				Element elm = i.next();
				String ctid = elm.getChildText("ctid");
				hasSet.add(ctid);
			}
			set.removeAll(hasSet);

			if (set.size() != 0) {
				throw new InvalidDataException(sheetid + "尚有" + set.size()
						+ "份必录证照未录入，请补全后提交");
			}
		}
		if ("1".equals(type)) {
			SelCertificateType ct = new SelCertificateType(token);
			HashSet<String> set = ct.getHashSet(conn);
			HashSet<String> hasSet = new HashSet<String>();
			for (Iterator<Element> i = rowsList.iterator(); i.hasNext();) {
				Element elm = i.next();
				String ctid = elm.getChildText("ctid");
				hasSet.add(ctid);
			}
			set.removeAll(hasSet);

			if (set.size() != 0) {
				throw new InvalidDataException(sheetid + "尚有" + set.size()
						+ "份必录证照未录入，请补全后提交");
			}
		}

		try {
			conn.setAutoCommit(false);
			// 更新表体信息为1
			postItem(sheetid);
			// 更新表头为1
			if (!checkHead(1, 0)) {
				if (!checkHead(1, 99)) {
					throw new InvalidDataException("无法更新" + sheetid + "表头状态为1");
				}
			}
			dao.checkItemFlag(sheetid);
			conn.commit();

		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 提交表体，根据状态依次提交
	 * 
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 */
	private int postItem(String sheetid) throws SQLException {
		int rows = checkAllItem(sheetid, 1, 0, "新建提交");
		rows += checkAllItem(sheetid, 1, -1, "审核返回重新提交");
		rows += checkAllItem(sheetid, 1, -10, "过期预警更新提交");
		rows += checkAllItem(sheetid, 1, -11, "到期预警更新提交");
		rows += checkAllItem(sheetid, 1, -100, "过期更新提交");
		return rows;
	}

	/**
	 * 审核表头通过，意味着表体全部falg=100，表头flag=100
	 * 
	 * @throws Exception
	 */
	public void checkHeadOK() throws Exception {
		token.checkPermission(8000003, Permission.CHECK);

		String sheetid = request.getParameter("sheetid");
		if (sheetid == null || sheetid.length() == 0) {
			throw new InvalidDataException("必须提供证照sheetid");
		}
		try {
			conn.setAutoCommit(false);
			if (!checkHead(100)) {
				throw new InvalidDataException("无法更新" + sheetid + "表头状态为100");
			}

			if (checkAllItem(sheetid, 100, "审核通过") == 0) {
				throw new InvalidDataException("无法更新" + sheetid + "表体状态为100");
			}

			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 审核表体单条记录通过
	 * 
	 * @throws Exception
	 */
	public void checkItemOK() throws Exception {
		token.checkPermission(8000003, Permission.CHECK);
		perm = token.getPermission(8000003);
		try {
			conn.setAutoCommit(false);
			if (perm.include(Permission.CONFIRM)) {
				if (!checkItem(100)) {
					throw new InvalidDataException("无法更新表体状态为100");
				}
			} else {
				if (!checkItem(2)) {
					throw new InvalidDataException("无法更新表体状态为2");
				}
			}

			// 检查表体是否全部审核通过
			if (isAllItemPass()) {
				// 表体全部审核通过后，直接更新表头
				if (perm.include(Permission.CONFIRM)) {
					if (!checkHead(100)) {
						throw new InvalidDataException("无法更新表头状态为100");
					}
				} else {
					if (!checkHead(2)) {
						throw new InvalidDataException("无法更新表头状态为2");
					}
				}

			} else {
				checkHead(99);
			}
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 拒绝审核单条表体信息
	 * 
	 * @throws Exception
	 */
	public void checkItemNO() throws Exception {
		token.checkPermission(8000003, Permission.CHECK);
		try {
			conn.setAutoCommit(false);

			if (!checkItem(-1)) {
				throw new InvalidDataException("无法更新表体状态为-1");
			}
			// 作废表体同时，表头状态为部分审核
			checkHead(99, 1);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	private boolean checkItem(int newFlag) throws ParseException, SQLException,
			InvalidDataException {
		CertificateDAO dao = new CertificateDAO(conn);
		CertificateItem i = cookItem();
		i.setFlag(newFlag);
		i.setChecker(token.getBusinessid());
		String note = token.getBusinessid() + "审核";
		if (i.getNote() != null && i.getNote().length() > 0) {
			note = i.getNote();
		}
		if (newFlag == 2) {
			// 插入任务
			dao.addUploadTask(i);
			note += "一审核通过";
		} else if (newFlag == 100) {
			// 插入任务
			dao.addUploadTask(i);
			note += "二审核通过";
		} else if (newFlag == 1) {
			note += "提交";
			i.setChecker("");// 供应商提交操作不更新审核人
		}
		i.setNote(note);
		return dao.checkItem(i);
	}

	/**
	 * 审核全部表体为newFalg
	 * 
	 * @param sheetid
	 * @throws SQLException
	 */
	private int checkAllItem(String sheetid, int newFlag, int oldFlag,
			String note) throws SQLException {
		String checker = "";
		Date checktime = null;
		CertificateDAO dao = new CertificateDAO(conn);
		note = token.getBusinessid() + note;
		if (newFlag != 1) {
			checker = token.getBusinessid();
			checktime = new Date(new java.util.Date().getTime());
		}

		if (newFlag == 100) {
			dao.addUploadTask(sheetid, checker);
		}

		return dao.checkAllItem(sheetid, checker, checktime, newFlag, oldFlag,
				note);
	}

	private int checkAllItem(String sheetid, int newFlag, String note)
			throws SQLException {
		String checker = "";
		Date checktime = null;
		CertificateDAO dao = new CertificateDAO(conn);
		note += token.getBusinessid();
		if (newFlag != 1) {
			checker = token.getBusinessid();
			checktime = new Date(new java.util.Date().getTime());
		}

		// 上传证照
		if (newFlag == 100) {
			dao.addUploadTask(sheetid, checker);
		}

		return dao.checkAllItem(sheetid, checker, checktime, newFlag, note);
	}

	/**
	 * 审核表头 供应商提交提交
	 * 
	 * @param flag
	 * @throws SQLException
	 */
	private boolean checkHead(int flag, int oldFlag) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		Certificate c = cookHead();
		if (flag == 100) {
			c.setNote(token.getBusinessid() + "，审核通过");
		} else if (flag == 1) {
			// 如果是供应商提交状态，则修改提交日期列
			c.setSubmitTime(new Date(System.currentTimeMillis()));
		}
		c.setFlag(flag);
		c.setChecker(token.getBusinessid());
		return dao.checkHead(c, oldFlag);
	}

	private boolean checkHead(int flag) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		Certificate c = cookHead();
		if (flag == 100) {
			c.setNote(token.getBusinessid() + "，二审核通过");
		}else if(flag == 2){
			c.setNote(token.getBusinessid() + "，一审核通过");
		}
		c.setFlag(flag);
		c.setChecker(token.getBusinessid());
		return dao.checkHead(c);
	}

	/**
	 * 检查表体是否都审核通过
	 * 
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private boolean isAllItemPass() throws ParseException, SQLException,
			InvalidDataException {
		CertificateDAO dao = new CertificateDAO(conn);
		CertificateItem i = cookItem();
		String sheetid = i.getSheetid();

		return dao.isAllItemPass(sheetid);
	}

	/**
	 * 搜索供应商证照列表
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws NamingException
	 */
	public Element searchVenderList() throws SQLException,
			InvalidDataException, NamingException {
		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<>(
				this.request.getParameterMap());
		if (token.isVender) {
			String[] arr_venderid = new String[1];
			arr_venderid[0] = token.getBusinessid();
			map.put("venderid", arr_venderid);
		}
		return dao.searchList(map);
	}

	public Element searchcheckitemList() throws SQLException,
			InvalidDataException, NamingException {

		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());
		if (token.isVender) {
			String[] arr_venderid = new String[1];
			arr_venderid[0] = token.getBusinessid();
			map.put("venderid", arr_venderid);
		}

		return dao.searchcheckLIst(map);
	}

	public Element getplcount() throws SQLException, InvalidDataException,
			NamingException {

		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());
		if (token.isVender) {
			String[] arr_venderid = new String[1];
			arr_venderid[0] = token.getBusinessid();
			map.put("venderid", arr_venderid);
		}
		return dao.getplcount(map);
	}

	public Element searchcheckimage() throws InvalidDataException, SQLException {

		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());

		return dao.searchcheckimage(map);
	}

	/**
	 * 供应商搜索证照明细
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws NamingException
	 */
	public Element searchVenderDetailList() throws SQLException,
			InvalidDataException, NamingException {
		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());
		if (token.isVender) {
			String[] arr_venderid = new String[1];
			arr_venderid[0] = token.getBusinessid();
			map.put("venderid", arr_venderid);
		}
		return dao.searchDetailList(map);
	}

	/**
	 * 审核人员搜索证照
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public Element searchCheckerList() throws SQLException,
			InvalidDataException {
		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());
		return dao.searchList(map);
	}

	/**
	 * 返回证照明细
	 * 
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public Element getDetailList() throws InvalidDataException, SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		String sheetid = this.request.getParameter("sheetid");
		if (sheetid == null || sheetid.length() == 0) {
			throw new InvalidDataException("必须提供证照sheetid");
		}
		Element elmHead = dao.getHead(sheetid);
		Element elmItem = dao.getItem(sheetid);
		Element elmDetail = new Element("detail");
		elmDetail.addContent(elmHead);
		elmDetail.addContent(elmItem);
		return elmDetail;
	}

	public Element getDetail() throws InvalidDataException, SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		String sheetid = this.request.getParameter("sheetid");
		if (sheetid == null || sheetid.length() == 0) {
			throw new InvalidDataException("必须提供证照sheetid");
		}
		String seqno = this.request.getParameter("seqno");
		if (seqno == null || seqno.length() == 0) {
			throw new InvalidDataException("必须提供证照seqno");
		} else {
			int i = Integer.parseInt(seqno);
			return dao.getDetail(sheetid, i);
		}
	}

	/**
	 * 取得供应商的扩展信息
	 * 
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public Element getVenderExt() throws SQLException, NamingException {
		VenderExt ext = new VenderExt();
		String venderid = "";
		if (token.isVender) {
			venderid = token.getBusinessid();
		} else {
			venderid = request.getParameter("venderid");
		}
		return ext.getVenderExt(conn, venderid);
	}

	private String getVenderid(String sheetid) {
		CertificateDAO dao = new CertificateDAO(conn);
		return dao.getVenderid(sheetid);
	}

	public void setVenderExt(Element elmVender) throws Exception {
		VenderExt ext = new VenderExt();
		String venderid = "";
		if (token.isVender) {
			venderid = token.getBusinessid();
		} else {
			venderid = request.getParameter("venderid");
		}
		try {
			conn.setAutoCommit(false);
			ext.delRow(conn, venderid);
			ext.insertNewRow(conn, elmVender, venderid);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	protected void moveFile(String fileName) throws InvalidDataException {
		// 检查临时文件
		String filePatch = Config.getInstance(token).getImgPatch() + fileName;
		// System.out.println(filePatch);
		if (!FileHandle.getState(filePatch)) {
			throw new InvalidDataException("没有上传图片！");
		}

		// 检查正式目录
		String newPatch = Config.getInstance(token).getImgPatch()
				+ token.getBusinessid();
		if (!FileHandle.createFolder(newPatch)) {
			throw new InvalidDataException("无法创建用户目录！" + newPatch);
		}
		String newFilePatch = newPatch + "/" + fileName;
		// 复制文件
		if (!FileHandle.copyFile(filePatch, newFilePatch)) {
			throw new InvalidDataException("无法复制文件到指定目录");
		}
		// 清理
		FileHandle.delFile(filePatch);

	}

	/**
	 * 检查单据是否含有已审核。没有已审核的证照据则返回false
	 * 
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 */
	private boolean isSheetChecked(String sheetid) throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		return dao.isSheetChecked(sheetid);
	}

	/**
	 * 检查单据具体一行明细是否含有已审核。没有已审核的证照据则返回false
	 * 
	 * @param sheetid
	 * @param i
	 * @return
	 * @throws SQLException
	 */
	private boolean isSheetChecked(String sheetid, int seqno)
			throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		return dao.isSheetChecked(sheetid, seqno);
	}

	/**
	 * @param request
	 * @param token
	 */
	public CertificateService(HttpServletRequest request, Connection conn,
			Token token) {
		super();
		this.request = request;
		this.conn = conn;
		this.token = token;
	}

	/**
	 * 供应商证照完成情况
	 * 
	 * @param venderid
	 * @return
	 * @throws SQLException
	 */
	public Element getTask(String venderid) throws SQLException {
		TaskDao dao = new TaskDao(conn, venderid);

		return dao.getTaskList();
	}

	/**
	 * 供应商证照列表
	 * 
	 * @param venderid
	 * @return
	 * @throws SQLException
	 */
	public Element getVenderCertificateList(String venderid)
			throws SQLException {
		TaskDao dao = new TaskDao(conn, venderid);

		return dao.getVenderCertificateList();
	}

	private void delSheetImgs(String sheetid, String venderid)
			throws SQLException {
		CertificateDAO dao = new CertificateDAO(conn);
		List<String> list = dao.getSheetImgList(sheetid);
		for (String fileName : list) {
			delImg(fileName, venderid);
		}
	}

	private void delItemImgs(String sheetid, int seqno, String venderid) {
		CertificateDAO dao = new CertificateDAO(conn);
		List<String> list = dao.getItemImgList(sheetid, seqno);
		for (String fileName : list) {
			delImg(fileName, venderid);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 *            文件名，非绝对路径
	 */
	private void delImg(String fileName, String venderid) {
		String filePatch = Config.getInstance(token).getImgPatch() + venderid
				+ "/" + fileName;
		boolean res = FileHandle.delFile(filePatch);
		System.out.println(res == true ? "删除图片:" + filePatch : "删除图片失败:"
				+ filePatch);
	}

	/**
	 * 获取已有通过证照供应商清单
	 * 
	 * @return
	 */
	public Element getCheckedVenderList() {
		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());
		if (token.isVender) {
			String[] arr_venderid = new String[1];
			arr_venderid[0] = token.getBusinessid();
			map.put("venderid", arr_venderid);
		}

		return dao.getCheckedVenderList(map);
	}

	/**
	 * 供应商获取预警状态证照清单
	 * 
	 * @return
	 */
	public Element getWarnVenderCertificateList() {
		CertificateDAO dao = new CertificateDAO(conn);
		Map<String, String[]> map = new HashMap<String, String[]>(
				this.request.getParameterMap());
		if (token.isVender) {
			String[] arr_venderid = new String[1];
			arr_venderid[0] = token.getBusinessid();
			map.put("venderid", arr_venderid);
		}

		return dao.getWarnVenderCertificateList(map);
	}

}
