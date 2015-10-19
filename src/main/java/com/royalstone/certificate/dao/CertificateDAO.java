/**
 * 
 */
package com.royalstone.certificate.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.certificate.bean.Certificate;
import com.royalstone.certificate.bean.CertificateItem;
import com.royalstone.certificate.bean.Image;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author BaiJian 证照子系统 数据库操作
 */
public class CertificateDAO {

	final private Connection	conn;

	/**
	 * @param conn
	 */
	public CertificateDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	public void addHead(Certificate c) {
		String sql = "insert into certificate(sheetid,type,venderid,venderType,contact,ccid,flag,checker,checktime,note,venderTypeName) values(?,?,?,?,?,?,?,?,?,?,?)";
		SqlUtil.executePS(conn, sql, c.getSheetid(), c.getType(), c.getVenderid(), c.getVenderType(), c.getContact(), c
				.getCategoryid(), c.getFlag(), c.getChecker(), c.getCheckTime(), c.getNote(), c.getVenderTypeName());
	}

	/**
	 * 单独修改供应商类型名称
	 * 
	 * @throws SQLException
	 */
	public void updateVenderTypeName(Certificate c) {
		String sql = " update certificate set venderTypeName=? where sheetid=?";
		SqlUtil.executePS(conn, sql, c.getVenderTypeName(), c.getSheetid());
	}

	public void addItem(CertificateItem i) {
		String sql = "insert into certificateitem(sheetid,certificateID,certificateName,ctid,expirydate,goodsname,barcodeid,note,seqno,flag,yeardate,approvalnum,papprovalnum,editor,edittime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
		SqlUtil.executePS(conn, sql, i.getSheetid(), i.getCertificateID(), i.getCertificateName(), i.getCtid(), i
				.getExpiryDate(), i.getGoodsName(), i.getBarcodeid(), i.getNote(), i.getSeqno(), i.getFlag(), i
				.getYearDate(), i.getApprovalnum(), i.getPapprovalnum(), i.getEditor());
	}

	/**
	 * 添加新的图片
	 * 
	 * @param i
	 * @return
	 * @throws SQLException
	 * @throws SQLException
	 */
	public int addImg(Image... i) throws SQLException {
		String sql = "insert into certificateitemimg values(?,?,?,?)";
		int rows = 0;
		ArrayList<Object[]> paramsList = new ArrayList<Object[]>();
		for (int j = 0; j < i.length; j++) {
			Object[] obj = new Object[4];
			obj[0] = i[j].getSheetid();
			obj[1] = i[j].getSeqno();
			obj[2] = i[j].getImgseqno();
			obj[3] = i[j].getImgFileName();
			paramsList.add(obj);
		}
		
		rows = SqlUtil.executeBatchPS(conn, sql, paramsList).length;
		return rows;
	}

	/**
	 * 删除一个图片
	 * 
	 * @param i
	 * @return
	 * @throws SQLException
	 */
	public int delImg(Image i) {
		String sql = "delete from certificateitemimg where sheetid=? and seqno=? and imgseqno=?";
		return SqlUtil.executePS(conn, sql, i.getSheetid(), i.getSeqno(), i.getImgseqno());
	}

	public String getImgFileName(Image i) {
		String sql = " select imgfile from certificateitemimg " + " where sheetid=? and seqno=? and imgseqno=? ";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, i.getSheetid(), i.getSeqno(), i.getImgseqno());
		String fileName = "";
		if (list.size() > 0) {
			fileName = list.get(0);
		}
		return fileName;
	}

	public List<String> getSheetImgList(String sheetid) {
		String sql = " select imgfile from certificateitemimg where sheetid=? ";
		return SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
	}

	public List<String> getItemImgList(String sheetid, int seqno) {
		String sql = " select imgfile from certificateitemimg where sheetid=? and seqno=? ";
		return SqlUtil.queryPS4SingleColumn(conn, sql, sheetid, seqno);
	}

	public Element getImg(Image i) {
		String sql = " select sheetid,seqno,imgseqno,imgfile from certificateitemimg "
				+ " where sheetid=? and seqno=? and imgseqno=? ";
		return SqlUtil.getRowSetElement(conn, sql, new Object[] { i.getSheetid(), i.getSeqno(), i.getImgseqno() },
				"img");
	}

	public Element getImgList(String sheetid, int seqno) {
		String sql = " select sheetid,seqno,imgseqno,imgfile from certificateitemimg "
				+ " where sheetid=? and seqno=? ";
		return SqlUtil.getRowSetElement(conn, sql, new Object[] { sheetid, seqno }, "img");
	}

	/**
	 * 通过证照编码获取图片信息
	 * @param cid
	 *            证照编码
	 * @param ctid
	 *            证照类型
	 * @param venderid
	 * @return
	 */
	public Image[] getImgByCid(String venderid,String certificateid) {
		String sql = "select c.imgseqno,MAX(c.imgfile) from certificate a,certificateitem b,certificateitemimg c "
				+ " where a.sheetid=b.sheetid and b.sheetid=c.sheetid and b.seqno=c.seqno "
				+ " and a.venderid=? and b.certificateid=? group by c.imgseqno "
				+ " order by c.imgseqno";
		List<String[]> list = SqlUtil.queryPS4Column(conn, sql, venderid,certificateid);
		Image[] imgs = new Image[list.size()];
		for (int i = 0; i < imgs.length; i++) {
			String[] ss = list.get(i);
			imgs[i] = new Image(null,0,Integer.parseInt(ss[0]),ss[1] );
		}

		return imgs;
	}

	/**
	 * 更新图片
	 * 
	 * @param i
	 * @return
	 * @throws SQLException
	 */
	public int updateImg(Image i) {
		String sql = "update certificateitemimg set imgFile=? where sheetid=? and seqno=? and imgseqno=?";

		return SqlUtil.executePS(conn, sql, i.getImgFileName(), i.getSheetid(), i.getSeqno(), i.getImgseqno());
	}

	/**
	 * 不修改flag审核列，在审核方法里单独修改
	 * 
	 * @param c
	 * @throws SQLException
	 */
	public void updateHead(Certificate c) {
		String sql = "update certificate set type=?,venderid=?,venderType=?,contact=?,categoryid=?,note=? where sheetid=?";

		SqlUtil.executePS(conn, sql, c.getType(), c.getVenderid(), c.getVenderType(), c.getContact(),
				c.getCategoryid(), c.getNote(), c.getSheetid());
	}

	public int delSheet(Certificate c) {
		String sql = "delete from certificateitem where sheetid=?";
		String sql2 = "delete from certificateitemimg where sheetid=?";
		String sql3 = "delete from certificate where sheetid=?";

		int rows = SqlUtil.executePS(conn, sql, c.getSheetid());
		rows += SqlUtil.executePS(conn, sql2, c.getSheetid());
		rows += SqlUtil.executePS(conn, sql3, c.getSheetid());

		return rows;
	}

	public int delSheetItem(CertificateItem c) {
		String sql = "delete from certificateitem where sheetid=? and seqno=? ";
		String sql2 = "delete from certificateitemimg where sheetid=? and seqno=? ";
		int rows = SqlUtil.executePS(conn, sql, c.getSheetid(), c.getSeqno());
		rows += SqlUtil.executePS(conn, sql2, c.getSheetid(), c.getSeqno());
		return rows;
	}

	/**
	 * 删除表体
	 * 
	 * @param i
	 * @return
	 * @throws SQLException
	 */
	public int delItem(CertificateItem i) {
		String sql = "delete from certificateitem where sheetid=? and seqno=?";
		return SqlUtil.executePS(conn, sql, i.getSheetid(), i.getSeqno());
	}

	public String getVenderid(String sheetid){
		String sql = "select venderid from certificate where sheetid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		return list.size()>0?list.get(0):null;
	}
	
	public Element getHead(String sheetid) {
		String sql = " select c.sheetid,c.type,c.venderid,c.venderType,c.venderTypeName,"
				+ " ext.contact,c.ccid,c.flag,c.checker,c.checktime,c.submittime,"
				+ " c.note,v.vendername,ext.contacttel telno,v.address,ca.ccname  from certificate c "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join venderext ext on ext.venderid = c.venderid " + " where c.sheetid=? ";
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "head");
	}

	public Element getItem(String sheetid) {
		String sql = " select i.sheetid,i.seqno,i.flag,i.certificateID,i.certificatename,"
				+ " i.ctid,i.expirydate,i.yeardate,decode(b.type,3,goods.goodsname,4,i.goodsname) goodsname,i.barcodeid, "
				+ " i.note,ct.ctname,ct.yearflag,ct.whflag,g.goodsid,i.approvalnum,i.papprovalnum,ct.appflag,i.editor,i.edittime,i.checker,i.checktime "
				+ " from certificateitem i join certificate b on i.sheetid=b.sheetid "
				+ " left join certificateType ct on (ct.ctid=i.ctid) "
				+ " left join gpackage g on g.barcode=i.barcodeid "
				+ " left join goods on goods.goodsid=g.goodsid " 
				+ " where i.sheetid=? order by i.seqno";
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "body");
	}

	public Element getDetail(String sheetid, int seqno) {
		String sql = " select c.sheetid,c.type,c.venderid,c.venderType,c.venderTypeName, "
				+ " ext.contact,c.ccid,c.flag headflag,i.editor,i.edittime,i.checker,i.checktime, "
				+ " v.vendername,ext.contacttel telno,v.address,ca.ccname, "
				+ " i.seqno,i.flag,i.certificateID,i.certificateName, "
				+ " i.ctid,i.EXPIRYDATE,i.yeardate,decode(c.type,3,goods.goodsname,4,i.goodsname) goodsname,g.goodsid,"
				+ " i.barcodeid,i.note,ct.ctname,ct.yearflag,ct.whflag,ct.appflag,ctc.flag ctcflag,i.approvalnum,i.papprovalnum "
				+ " from certificateitem i " 
				+ " join certificate c on c.sheetid=i.sheetid "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join certificateType ct on (ct.ctid=i.ctid)  "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " left join certificateRelation ctc on (ctc.ctid=i.ctid and ctc.ccid=c.ccid)  "
				+ " left join gpackage g on g.barcode=i.barcodeid "
				+ " left join goods on goods.goodsid=g.goodsid " 
				+ " where i.sheetid=? and i.seqno=?";
		return SqlUtil.getRowSetElement(conn, sql, new Object[] { sheetid, seqno }, "detail");
	}

	public boolean editItem(CertificateItem i, int oldFlag) {
		String sql = "update certificateitem set flag=?, edittime=sysdate , editor=? where sheetid=? and seqno=? and flag=?";
		return SqlUtil.executePS(conn, sql, i.getFlag(), i.getChecker(), i.getSheetid(), i.getSeqno(), oldFlag) > 0 ? true
				: false;
	}

	public boolean checkHead(Certificate c, int oldFlag) {
		String sql = " update certificate set flag=?, checktime=sysdate , checker=?, submittime=? where sheetid=? and flag=?";
		return SqlUtil.executePS(conn, sql, c.getFlag(), c.getChecker(),c.getSubmitTime(), c.getSheetid(),  oldFlag) > 0 ? true : false;
	}

	public boolean checkHead(Certificate c) {
		String sql = " update certificate set flag=?, checktime=sysdate , checker=? where sheetid=?";
		return SqlUtil.executePS(conn, sql, c.getFlag(), c.getChecker(), c.getSheetid()) > 0 ? true : false;
	}

	public boolean checkItem(CertificateItem i, int oldFlag) {
		String sql = "update certificateitem set flag=?, checktime=sysdate , checker=? , note=? where sheetid=? and seqno=? and flag=?";
		return SqlUtil.executePS(conn, sql, i.getFlag(), i.getChecker(), i.getNote(), i.getSheetid(), i.getSeqno(),oldFlag) > 0 ? true : false;
	}

	public boolean checkItem(CertificateItem i) {
		String sql = "update certificateitem set flag=?, checktime=sysdate , checker=? , note=? where sheetid=? and seqno=? ";
		return SqlUtil.executePS(conn, sql, i.getFlag(), i.getChecker(), i.getNote(), i.getSheetid(), i.getSeqno()) > 0 ? true : false;
	}

	public int checkAllItem(String sheetid, String checker, Date checktime, int newFlag, int oldFlag, String note) {
		String sql = "update certificateitem set flag=?, checker=? , checktime=? , note=? where sheetid=? and flag=? ";
		return SqlUtil.executePS(conn, sql, newFlag, checker, checktime,note, sheetid, oldFlag);
	}

	public int checkAllItem(String sheetid, String checker, Date checktime, int newFlag, String note) {
		String sql = "update certificateitem set flag=?, checker=? , checktime=? , note=? where sheetid=? ";
		return SqlUtil.executePS(conn, sql, newFlag, checker,checktime, note, sheetid);
	}

	public boolean checkAllUnflagItem(String sheetid, String checker, Date checktime, int newFlag, int oldFlag, String note) {
		String sql = "update certificateitem set flag=?, checker=? , checktime=? , note=? where sheetid=? and flag<>? ";
		return SqlUtil.executePS(conn, sql, newFlag, checker,checktime, note, sheetid, oldFlag) > 0 ? true : false;
	}

	/**
	 * 检查是否全部审核，有未审核单据则返回false
	 * 
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 */
	public boolean isAllItemPass(String sheetid) throws SQLException {
		String sql = "select Count(*) from certificateitem where sheetid=? and flag<>100";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		return Integer.parseInt(list.get(0)) == 0 ? true : false;
	}

	/**
	 * 检查单据是否含有已审核。没有已审核的证照据则返回false
	 * 
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 */
	public boolean isSheetChecked(String sheetid) throws SQLException {
		String sql = "select Count(*) from certificateitem where sheetid=? and flag=100";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		return Integer.parseInt(list.get(0)) == 0 ? false : true;
	}
	public boolean isSheetChecked(String sheetid, int seqno) {
		String sql = "select Count(*) from certificateitem where sheetid=? and seqno=? and flag=100";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid, seqno);
		return Integer.parseInt(list.get(0)) == 0 ? false : true;
	}

	private int getCount(String sql) throws SQLException {
		List<String> list = SqlUtil.querySQL4SingleColumn(conn, sql);
		return Integer.parseInt(list.get(0));
	}

	/**
	 * 证照列表搜索
	 * 
	 * @param map
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public Element searchList(Map<?, ?> map) throws SQLException, InvalidDataException {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = " select * from (select distinct c.sheetid,c.type,c.venderid,c.venderType,c.venderTypeName,"
				+ " ext.contact,c.ccid,c.flag,c.checker,c.checktime,c.submittime,"
				+ " c.note,v.vendername,ext.contacttel telno,v.address,ca.ccname " 
				+ " from certificate c "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " left join certificateitem i on (i.sheetid=c.sheetid)  WHERE " + filter.toString()
				+ ") where rownum<=3000 ";

		String sql_count = " select count(*) from (select distinct c.sheetid,c.type,c.venderid,c.venderType,c.venderTypeName,"
				+ " ext.contact,c.ccid,c.flag,c.checker,c.checktime,c.submittime,"
				+ " c.note,v.vendername,ext.contacttel telno,v.address,ca.ccname from certificate c "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " left join certificateitem i on (i.sheetid=c.sheetid) where " + filter.toString() + " ) ";

		int count = getCount(sql_count);

		Element elm = SqlUtil.getRowSetElement(conn, sql, "list");
		elm.addContent(new Element("totalCount").addContent(count + ""));
		return elm;

	}

	/**
	 * 检查新品、旧品证照是否重复
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */

	public Element searchcheckLIst(Map<?, ?> map) throws InvalidDataException, SQLException {
		Filter filter = cookCheckitemlistFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");
		String sql = " select c.sheetid,i.expirydate,i.barcodeid,i.papprovalnum,i.seqno from certificateitem  i "
				+ " join certificate c on c.sheetid=i.sheetid where " + filter.toString();
		Element elm = SqlUtil.getRowSetElement(conn, sql, "checkitemlist");
		return elm;

	}

	/**
	 * 检查品类证照是否可以新建
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public Element getplcount(Map<?, ?> map) throws InvalidDataException, SQLException {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");
		String sql = "  select count(distinct c.sheetid) count " 
				+ " from certificate c "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " WHERE "
				+ filter.toString()
				+ " and c.flag <>'100' ";

		ResultSet rs = SqlUtil.querySQL(conn, sql);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("getplcount", "row");

		rs.close();
		return elm;
	}

	/**
	 * 检查证照提交时图片是否均已上传
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */

	public Element searchcheckimage(Map<?, ?> map) throws InvalidDataException, SQLException {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = "  select count(*) count from (select distinct sheetid,seqno from certificateitem c where "
				+ filter.toString()
				+ " and exists(select distinct sheetid,seqno from certificateitemimg B where c.sheetid=B.sheetid and c.seqno=B.seqno) "
				+ " group by sheetid,seqno) a having count(*)=(select count(*) from certificateitem c where "
				+ filter.toString() + " )";

		ResultSet rs = SqlUtil.querySQL(conn, sql);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("checkimageList", "row");

		rs.close();
		return elm;
	}

	/**
	 * 证照明细列表
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public Element searchDetailList(Map<?, ?> map) throws InvalidDataException, SQLException {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = " select c.sheetid,c.type,c.venderid,c.venderType,c.venderTypeName, "
				+ " ext.contact,c.ccid,i.editor,i.edittime,i.checker,i.checktime,c.submittime,v.vendername,ext.contacttel telno,v.address,ca.ccname, "
				+ " i.seqno,i.flag,i.certificateid,i.certificateName cname, "
				+ " i.ctid,i.expirydate,i.yeardate,decode(c.type,3,goods.goodsname,4,i.goodsname) goodsname,"
				+ " i.barcodeid,i.note,ct.ctname,ct.yearflag,ct.whflag,g.goodsid,i.approvalnum,i.papprovalnum "
				+ " from certificateitem i join certificate c on c.sheetid=i.sheetid "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join certificateType ct on (ct.ctid=i.ctid)  "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " left join gpackage g on g.barcode=i.barcodeid " 
				+ " left join goods on goods.goodsid=g.goodsid " 
				+ " where rownum<=5000 and " + filter.toString();

		String sql_count = " select count(*) " + " from certificateitem i join certificate c on c.sheetid=i.sheetid "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join certificateType ct on (ct.ctid=i.ctid)  "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " left join gpackage g on g.barcode=i.barcodeid " 
				+ " left join goods on goods.goodsid=g.goodsid " 
				+ " where " + filter.toString();

		int count = getCount(sql_count);

		ResultSet rs = SqlUtil.querySQL(conn, sql);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("detail", "row");

		elm.addContent(new Element("totalCount").addContent(count + ""));

		rs.close();
		return elm;
	}

	/**
	 * 证照明细列表excel
	 * 
	 * @param file
	 * @param map
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void makeDetailExcel(File file, Map<?, ?> map) throws InvalidDataException, SQLException, IOException {
		String[] title = { "状态", "单号", "序号", "证照类型", "供应商类型", "类型名称", "供应商ID", "供应商名称", "供应商电话", "联系人", "种类", "品类",
				"证照编码", "证照名称", "有效截止日", "年审日", "商品条码", "商品编码", "商品名称", "批文号", "产品批次号", "审核人", "审核时间", "备注" };
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = " select  case  when i.flag=0 then '"
				+ SqlUtil.toLocal("未提交")
				+ "' when i.flag=1 then '"
				+ SqlUtil.toLocal("待审核")
				+ "' when i.flag=100 then '"
				+ SqlUtil.toLocal("审核通过")
				+ "' when i.flag=-1 then '"
				+ SqlUtil.toLocal("审核返回")
				+ "' when i.flag=-10 then '"
				+ SqlUtil.toLocal("年审预警")
				+ "' when i.flag=-11 then '"
				+ SqlUtil.toLocal("过期预警")
				+ "' when i.flag=-100 then '"
				+ SqlUtil.toLocal("已过期")
				+ "' end as flag,"
				+ " c.sheetid,i.seqno,"
				+ " case when c.type=1 then '"
				+ SqlUtil.toLocal("基本证照")
				+ "' when c.type=2 then '"
				+ SqlUtil.toLocal("品类证照")
				+ "' when c.type=3 then '"
				+ SqlUtil.toLocal("旧品证照")
				+ "' when c.type=4 then '"
				+ SqlUtil.toLocal("新品证照")
				+ "' end as type,"
				+ " case when c.vendertype=0 then '"
				+ SqlUtil.toLocal("生成型")
				+ "' when c.vendertype=1 then '"
				+ SqlUtil.toLocal("代理或贸易型")
				+ "' end as vendertype,c.venderTypeName,v.venderid,v.vendername,ext.contacttel telno,"
				+ " ext.contact,ct.ctname,ca.ccname,i.certificateid,i.certificateName,i.expirydate,"
				+ " case when ct.yearflag=1 then i.yeardate end as yeardate,"
				+ "i.barcodeid,g.goodsid,decode(c.type,3,goods.goodsname,4,i.goodsname) goodsname,i.approvalnum,i.papprovalnum, i.checker,i.checktime,i.note "
				+ " from certificateitem i join certificate c on c.sheetid=i.sheetid "
				+ " join vender v on (v.venderid=c.venderid) "
				+ " left join certificateCategory ca on (ca.ccid = c.ccid) "
				+ " left join certificateType ct on (ct.ctid=i.ctid) "
				+ " left join venderext ext on ext.venderid = c.venderid "
				+ " left join gpackage g on g.barcode=i.barcodeid "
				+ " left join goods on goods.goodsid=g.goodsid " 
				+ " where rownum<=8000 and " + filter.toString();

		// System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(file, rs, title, "证照信息明细");
		rs.close();
		pstmt.close();
	}

	private Filter cookFilter(Map<?, ?> map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String[] ss2 = (String[]) map.get("isLike");

			if (ss2 != null && ss2.length > 0 && ss2[0] != null && ss2[0].length() > 0 && ss2[0].equals("true")) {
				filter.add("c.venderid like '%" + ss[0].replaceAll("'", "''") + "%'");
			} else {
				filter.add("c.venderid=" + ValueAdapter.toString4String(ss[0]));
			}
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("c.sheetid IN (" + val_sheetid.toString4String() + ") ");

			return filter;
		}

		ss = (String[]) map.get("regionid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_regionid = new Values(ss);
			filter.add("v.vendertype IN (" + val_regionid.toString4String() + ") ");
		}

		ss = (String[]) map.get("goodsname");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String[] ss2 = (String[]) map.get("isLike");

			if (ss2 != null && ss2.length > 0 && ss2[0] != null && ss2[0].length() > 0 && ss2[0].equals("true")) {
				filter.add("i.goodsname like '%" + ss[0].replaceAll("'", "''") + "%'");
			} else {
				filter.add("i.goodsname=" + ValueAdapter.toString4String(ss[0]));
			}
		}

		ss = (String[]) map.get("type");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_shopid = new Values(ss);
			filter.add("c.type IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) map.get("ctype");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_shopid = new Values(ss);
			filter.add("i.ctid IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) map.get("venderType");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_shopid = new Values(ss);
			filter.add("c.venderType IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) map.get("venderTypeName");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_shopid = new Values(ss);
			filter.add("c.venderTypeName IN (" + SqlUtil.toLocal(val_shopid.toString4String()) + ") ");
		}

		ss = (String[]) map.get("contact");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_shopid = new Values(ss);
			filter.add("c.contact IN (" + SqlUtil.toLocal(val_shopid.toString4String()) + ") ");
		}

		ss = (String[]) map.get("ccid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("c.ccid IN (" + val_status.toString4String() + ") ");
		}

		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("c.flag IN (" + val_status.toString4String() + ") ");
		}
		// 对于空表体的单据，查询部分特殊处理
		ss = (String[]) map.get("itemflag");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			// 对于待处理的单据定义
			if (ss[0].equals("uncheck")) {
				filter.add("i.flag IN (-1,-10,-11,-100,0) OR i.flag is NULL");
			} else if (ss[0].equals("0")) {
				filter.add("i.flag=0 OR i.flag is NULL");
			} else if (ss[0].equals("valid")) {
				filter.add("i.flag IN (100,-10,-11) ");
			} else {
				Values val_flag = new Values(ss);
				filter.add("i.flag IN (" + val_flag.toString4String() + ") ");
			}
		}
		ss = (String[]) map.get("certificateID");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("i.certificateid IN (" + val_status.toString4String() + ") ");
		}
		ss = (String[]) map.get("barcodeid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("i.barcodeid IN (" + Values.toString4in(ss) + ") ");
		}
		ss = (String[]) map.get("goodsid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("g.goodsid IN (" + val_status.toString4String() + ") ");
		}

		ss = (String[]) map.get("checker");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("c.checker IN (" + SqlUtil.toLocal(val_status.toString4String()) + ") ");
		}

		ss = (String[]) map.get("checktime");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String date = ss[0];
			filter.add("trunc(c.checktime) = " + ValueAdapter.std2mdy(date));
		}
		ss = (String[]) map.get("papprovalnum");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("i.papprovalnum IN (" + SqlUtil.toLocal(val_status.toString4String()) + ") ");
		}

		ss = (String[]) map.get("expiryDate");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String date = ss[0];
			filter.add("trunc(i.expirydate) = " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("checktime_min");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String date = ss[0];
			filter.add("trunc(c.checktime) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("checktime_max");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String date = ss[0];
			filter.add("trunc(c.checktime) <= " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}
	
	
	private Filter cookCheckitemlistFilter(Map<?, ?> map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("c.venderid=" + ValueAdapter.toString4String(ss[0]));
		}

		ss = (String[]) map.get("certificateID");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("i.certificateid IN (" + val_status.toString4String() + ") ");
		}else{
			filter.add("i.certificateid is null");
		}
		
		ss = (String[]) map.get("barcodeid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("i.barcodeid IN (" + Values.toString4in(ss) + ") ");
		}else{
			filter.add("i.barcodeid is null");
		}
		
		ss = (String[]) map.get("papprovalnum");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("i.papprovalnum IN (" + SqlUtil.toLocal(val_status.toString4String()) + ") ");
		}else{
			filter.add("i.papprovalnum is null");
		}

		ss = (String[]) map.get("expiryDate");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			String date = ss[0];
			filter.add("trunc(i.expirydate) = " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}

	/**
	 * 检查表头表体数据状态一致
	 */
	public void checkItemFlag(String sheetid) {
		String sql1 = "update certificateitem a set flag=1 where flag=0 and sheetid =  (select sheetid from certificate        b where b.flag=1 and b.sheetid=? )";
		String sql2 = "update certificate a         set flag=1 where flag=0 and sheetid IN (select sheetid from certificateitem b where b.flag=1 and b.sheetid=?  )";
		System.out.println(SqlUtil.executePS(conn, sql1, sheetid));
		System.out.println(SqlUtil.executePS(conn, sql2, sheetid));
	}

	public Element getCheckedVenderList(Map<?, ?> map) {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = "select d.venderid,e.vendername from (select c.venderid,c.type,i.flag,count(*) count from certificate c "
				+ " join certificateitem i on c.sheetid=i.sheetid "
				+ " where "
				+ filter.toString()
				+ " group by c.venderid, i.flag,c.type) d "
				+ " JOIN vender e on e.venderid=d.venderid "
				+ " group by d.venderid,e.vendername having count(d.venderid)=" + ((String[]) map.get("type")).length;
		return SqlUtil.getRowSetElement(conn, sql, "list");
	}

	public void getCheckedVenderListExcel(File file, Map<?, ?> map) throws SQLException, IOException {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = "select d.venderid,e.vendername from (select c.venderid,c.type,i.flag,count(*) count from certificate c "
				+ " join certificateitem i on c.sheetid=i.sheetid "
				+ " where "
				+ filter.toString()
				+ " group by c.venderid, i.flag,c.type) d "
				+ " JOIN vender e on e.venderid=d.venderid "
				+ " group by d.venderid,e.vendername having count(d.venderid)=" + ((String[]) map.get("type")).length;
		String[] title = { "供应商编码", "供应商名称" };
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(file, rs, title, "证照信息明细");
		SqlUtil.close(rs);
	}

	public Element getWarnVenderCertificateList(Map<?, ?> map) {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = "select c.venderid,v.vendername,"
				+ " decode(i.flag,-10,'年审预警',-11,'有效期预警',-100,'已过期') flag,count(*) count "
				+ " from certificate c join certificateitem i on c.sheetid=i.sheetid JOIN vender v on v.venderid=c.venderid "
				+ " where " + filter.toString() + " group by c.venderid,v.vendername,i.flag ";
		return SqlUtil.getRowSetElement(conn, sql, "list");
	}

	public void getWarnVenderCertificateListExcel(File file, Map<?, ?> map) throws SQLException, IOException {
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		String sql = "select c.venderid,v.vendername,"
				+ " decode(i.flag,-10,'年审预警',-11,'有效期预警',-100,'已过期') flag,count(*) count "
				+ " from certificate c join certificateitem i on c.sheetid=i.sheetid JOIN vender v on v.venderid=c.venderid "
				+ " where " + filter.toString() + " group by c.venderid,v.vendername,i.flag ";

		String[] title = { "供应商编码", "供应商名称", "状态", "数目" };
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(file, rs, title, "证照信息明细");
		SqlUtil.close(rs);
	}

	public void addUploadTask(CertificateItem i) {
		String sql = "insert into certasklist(taskid,sheetid,seq,flag,tries,checker,checktime,exporttime,note) "
				+ " select certaskid.nextval,ta.sheetid,tb.seqno,0,0,?,sysdate,null,'' "
				+ " from certificate ta,certificateitem tb,cert_export_vender tc "
				+ " where ta.sheetid=tb.sheetid and ta.venderid=tc.venderid and tb.ctid=tc.ctid "
				+ " and tb.sheetid=? and tb.seqno=? ";

		SqlUtil.executePS(conn, sql, new Object[] { i.getChecker(), i.getSheetid(), i.getSeqno() });
	}

	public void addUploadTask(String sheetid, String checker) {
		String sql = "insert into certasklist(taskid,sheetid,seq,flag,tries,checker,checktime,exporttime,note) "
				+ " select certaskid.nextval,ta.sheetid,tb.seqno,0,0,?,sysdate,null,'' "
				+ " from certificate ta,certificateitem tb,cert_export_vender tc "
				+ " where ta.sheetid=tb.sheetid and ta.venderid=tc.venderid and tb.ctid=tc.ctid "
				+ " and tb.sheetid=? ";

		SqlUtil.executePS(conn, sql, new Object[] { checker, sheetid });
	}

	
}
