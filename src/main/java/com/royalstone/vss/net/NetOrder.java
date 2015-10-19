package com.royalstone.vss.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;
import com.royalstone.workbook.Workbook;

public class NetOrder {
	final private HttpServletRequest	request;
	final private Connection			conn;
	final private Token					token;
	final int							site;
	String								username;
	Date								inputdate;

	SimpleDateFormat					df			= new SimpleDateFormat("yyyy-MM-dd");
	String								currenttime	= df.format(new Date());

	public NetOrder(HttpServletRequest request, Connection conn, Token token) throws ParseException {
		super();
		this.request = request;
		this.conn = conn;
		this.token = token;
		site = token.site.getSid();
		username = token.username;
		inputdate = df.parse(currenttime);
	}

	/**
	 * 取消预约
	 * @return
	 * @throws SQLException
	 */
	public void cancelOrder() throws SQLException {
		String order_serial = request.getParameter("order_serial");
		String dccode = request.getParameter("dccode");
		String request_date = request.getParameter("request_date");
		// 获取单据信息
		HashMap<String, String> map = getSheetInfo(order_serial, dccode);
		if (map == null)
			throw new DAOException("无法获取单据信息：" + order_serial);
		String logistics = map.get("logistics");
		String starttime = map.get("start_time");
		String endtime = map.get("end_time");
		int sumsku = Integer.parseInt(map.get("skunum"));
		int sumpkg = Integer.parseInt(map.get("temp1"));
		try {
			conn.setAutoCommit(false);
			// 预约控制表
			delSheetNetOrderTimeST(true, request_date,dccode, logistics, starttime, endtime, sumsku, sumpkg,sumpkg);

			// 修改预约主表的状态信息
			String sql = " update netorderhead set flag='N',updt_name=?,updt_date=sysdate where order_serial=? and dccode=? ";
			SqlUtil.executePS(conn, sql, username, order_serial, dccode);
			// 删除订单预约状态表信息
			String sql_orderstatus = " delete from netorderstatus  where sheetid in (select po_no from netorderdetail where order_serial=? and dccode=? ) "
					+ " and logistics = (select logistics from netorderhead where order_serial=? ) ";
			SqlUtil.executePS(conn, sql_orderstatus, order_serial, dccode, order_serial);
			// 记录订单预约取消信息
			String sql_orderlog = " insert into netorderlog(order_serial,dccode,po_no,po_type,rgst_date,rgst_name)"
					+ " select order_serial,dccode,po_no,'1',sysdate,? from netorderdetail where  order_serial=? and dccode=? ";
			SqlUtil.executePS(conn, sql_orderlog, username, order_serial, dccode);

			toITF(order_serial, dccode, 2);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new DAOException(e.getMessage());
		}
	}

	/**
	 * 获取单据信息
	 * @param order_serial
	 * @param dccode
	 * @return
	 */
	private HashMap<String, String> getSheetInfo(String order_serial, String dccode) {
		String sql = "select a.dccode,a.logistics,a.start_time,a.end_time,a.temp1,sum(nvl(b.skunum,0)) skunum,sum(nvl(b.pkgnum,0)) pkgnum "
				+ " from netorderhead a,netorderdetail b where a.order_serial=b.order_serial(+) and a.order_serial=? and a.dccode=? "
				+ " group by a.dccode,a.logistics,a.start_time,a.end_time,a.temp1 ";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, order_serial, dccode);
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	private int getSheetPkg(String order_serial, String dccode) {
		String sql = "select  a.temp1 from netorderhead a where  a.order_serial=? and a.dccode=? ";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, order_serial, dccode);
		if (list.size() == 0) {
			return 0;
		} else {
			String temp = list.get(0).get("temp1");
			return  Integer.parseInt(temp);
		}
	}

	/**
	 * 判断赠品订单和非赠品订单不能同时预约
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SQLException
	 */
	public Element checkPo() throws IOException, JDOMException, SQLException {

		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}
		String dataList = "0";
		for (Object object : elm_row) {
			Element elm = (Element) object;
			String sheetid = elm.getChildTextTrim("sheetid");
			dataList = dataList + ",'" + sheetid + "'";
		}

		String sql = " select sheetid,paytypeid from purchase where sheetid in (" + dataList.replace("0,", "") + ")";
		Element elm_sel = new Element("select");
		Element elm_opt = new Element("checkpo");
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String sheetid = rs.getString("sheetid");
			String paytypeid = rs.getString("paytypeid");
			elm_opt = new Element("checkpo");
			elm_opt.setAttribute("sheetid", sheetid);
			elm_opt.setAttribute("paytypeid", paytypeid);
			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();

		return elm_sel;
	}

	/**
	 * 此方法把查询结果输出到一个EXCEL兼容的XML文件中（供应商网上预约明细）
	 * 
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public void cookExcelFile(Map<String,String[]> map, File file) throws SQLException, InvalidDataException, IOException,
			RowsExceededException, WriteException {
		String[] title = { "预约流水号", "预约送货日期", "开始时间", "结束时间", "订单号", "供应商编码", "供应商名称", "商品条码", "商品名称", "箱数", "实际预约箱数","物流模式",
				"楼层", "VSS预约时间" };

		String logistics = "";
		if (request.getParameter("logistics") != null) {
			logistics = request.getParameter("logistics");
		}
		String sql = "";
		if (logistics.equals("1")) {
			sql = " select a.order_serial,to_char(a.request_date,'YYYY-MM-DD') request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1,name4code(a.logistics,'logistics') logistics,Substr(a.order_serial,13,13) as floor,to_char(a.rgst_date,'YYYY-MM-DD HH24:MI:SS') rgst_date "
					+ " from netorderhead a join netorderdetail b on a.order_serial=b.order_serial and a.dccode=b.dccode "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on ((b.po_no=d.sheetid and d.venderid=a.supplier_no and a.logistics=d.logistics)) join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(map).toString();
		} else if (logistics.equals("2")) {
			sql = " select a.order_serial,to_char(a.request_date,'YYYY-MM-DD') request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1,name4code(a.logistics,'logistics') logistics,Substr(a.order_serial,13,13) as floor,to_char(a.rgst_date,'YYYY-MM-DD HH24:MI:SS') rgst_date "
					+ " from netorderhead a join netorderdetail b on a.order_serial=b.order_serial and a.dccode=b.dccode "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on (b.po_no=d.refsheetid and d.venderid=a.supplier_no and a.logistics=d.logistics) join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(map).toString();
		} else {
			sql = " select a.order_serial,to_char(a.request_date,'YYYY-MM-DD') request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1,name4code(a.logistics,'logistics') logistics,Substr(a.order_serial,13,13) as floor,to_char(a.rgst_date,'YYYY-MM-DD HH24:MI:SS') rgst_date "
					+ " from netorderhead a join netorderdetail b on a.order_serial=b.order_serial and a.dccode=b.dccode "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on (b.po_no=d.sheetid and d.venderid=a.supplier_no and a.logistics=d.logistics) join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(map).toString()+
					" union all select a.order_serial,to_char(a.request_date,'YYYY-MM-DD') request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1,name4code(a.logistics,'logistics') logistics,Substr(a.order_serial,13,13) as floor,to_char(a.rgst_date,'YYYY-MM-DD HH24:MI:SS') rgst_date "
					+ " from netorderhead a join netorderdetail b on a.order_serial=b.order_serial and a.dccode=b.dccode "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on (b.po_no=d.refsheetid and d.venderid=a.supplier_no and a.logistics=d.logistics) join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(map).toString();;
		}
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		Workbook.writeToFile(file, rs, title, "供应商网上预约明细查询");
		rs.close();
		pstmt.close();
	}

	private Filter cookFilter(Map<String,String[]> map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("order_serial");
		if (ss != null && ss.length > 0) {

			filter.add(" order_serial = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("dccode");
		if (ss != null && ss.length > 0) {

			filter.add(" dccode = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("logistics");
		if (ss != null && ss.length > 0) {

			filter.add(" logistics = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("supplier_no");
		if (ss != null && ss.length > 0) {

			filter.add("supplier_no = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("request_date_min");
		if (ss != null && ss.length > 0) {
			filter.add(" request_date >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("request_date_max");
		if (ss != null && ss.length > 0) {
			filter.add(" request_date <= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0) {

			filter.add("flag = " + Values.toString4String(ss[0]));
		}
		return filter;
	}

	/**
	 * 供应商网上预约查询条件设置
	 * 
	 * @return
	 */
	public Filter cookFilter_v(Map<String,String[]> map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		// 强制行输出
		filter.add(" rownum<= " + VSSConfig.getInstance().getExcelLimitSoft());

		ss = (String[]) map.get("order_serial");
		if (ss != null && ss.length > 0) {
			filter.add(" a.order_serial = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("dccode");
		if (ss != null && ss.length > 0) {
			filter.add(" a.dccode = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("logistics");
		if (ss != null && ss.length > 0) {
			filter.add(" a.logistics = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("supplier_no");
		if (ss != null && ss.length > 0) {
			filter.add("a.supplier_no = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("request_date_min");
		if (ss != null && ss.length > 0) {
			filter.add("a.request_date >= " + ValueAdapter.std2mdy(ss[0]));
		}
		ss = (String[]) map.get("request_date_max");
		if (ss != null && ss.length > 0) {
			filter.add("a.request_date <= " + ValueAdapter.std2mdy(ss[0]));
		}
		ss = (String[]) map.get("floor");
		if (ss != null && ss.length > 0) {
			filter.add(" Substr(a.order_serial,13,13) = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0) {
			filter.add(" a.flag = " + Values.toString4String(ss[0]));
		}
		return filter;
	}

	private void deleteBody(String order_serial, String dccode, List<String[]> dataList) throws SQLException {
		String sql = " delete from  netorderdetail where order_serial =? and dccode=? and po_no=? ";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (int i = 0; i < dataList.size(); i++) {
			String[] ss = dataList.get(i);
			SqlUtil.setPS(ps, new Object[] { order_serial, dccode, ss[0] });
			ps.executeUpdate();
		}
		ps.close();
	}

	/**
	 * 预约取消后清理单据状态记录
	 * @param dataList
	 * @throws SQLException
	 */
	private void deleteOrderStatus(List<String[]> dataList) throws SQLException {
		String sql = " delete from  netorderstatus where sheetid =? ";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (int i = 0; i < dataList.size(); i++) {
			String[] ss = dataList.get(i);
			SqlUtil.setPS(ps, new Object[] { ss[0] });
			ps.executeUpdate();
		}
		ps.close();
	}

	private int getCount(String sql) throws SQLException {
		List<String> list = SqlUtil.querySQL4SingleColumn(conn, sql);
		return Integer.parseInt(list.get(0));
	}

	/**
	 * 查看供应商在此预约日期的物流模式
	 * 
	 * @return
	 */
	public Element getNetOrderLogistics() {
		String dccode = request.getParameter("dccode");
		String logistics = request.getParameter("logistics");
		String request_date = request.getParameter("request_date");
		String vendcode = token.getBusinessid();
		String sql = " select order_serial,dccode,logistics,supplier_no,request_date from netorderhead where flag='Y' and logistics = '"
				+ logistics
				+ "' and dccode='"
				+ dccode
				+ "' and supplier_no='"
				+ vendcode
				+ "' and request_date = to_date('" + request_date + "','YYYY-MM-DD')";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netorderlogistics");
		return elm_cat;
	}

	public Element getNetOrderPo(String po_no) throws SQLException {
		String sql = " select t.sheetid, t.refsheetid, t.venderid,v.vendername, t.paytypeid,p.paytypename, t.validdays, t.orderdate, t.deliverdate,"
				+ " t.vdeliverdate, t.delivertimeid, t.deadline, t.sgroupid, t.destshopid, t.shopid,s.shopname, t.logistics, t.flag, "
				+ " t.note,t.checker, t.checkdate, discountrate from purchase t join vender v on t.venderid=v.venderid "
				+ " join paytype p on t.paytypeid=p.paytypeid "
				+ " join shop s on t.destshopid=s.shopid WHERE t.sheetid='" + po_no + "'";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netnetorderpo");
		return elm_cat;
	}

	public Element getNetOrderPoItem(String po_no) throws SQLException {
		String sql = " select t.sheetid, t.goodsid,g.goodsname, t.barcode, t.qty,nvl(decode(t.pkgvolume,0,0,t.qty/t.pkgvolume),0) pkgqty, t.pkgvolume, "
				+ " t.cost, t.concost, t.firstdisc, t.memo, t.presentqty "
				+ " from  purchaseitem t left join goods g on t.goodsid=g.goodsid join purchase h on t.sheetid=h.sheetid  where h.sheetid='"
				+ po_no + "' ";
		String sql_count = " select count(*) from purchaseitem where sheetid ='" + po_no + "'";
		int count = getCount(sql_count);
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netnetorderpoitem");
		elm_cat.setAttribute("totalCount", "" + count);
		return elm_cat;
	}

	/**
	 * 查询某时间段可预约情况
	 * @return
	 */
	public Element getNetOrderqk() {
		String dccode = request.getParameter("dccode");
		String logistics = request.getParameter("logistics");
		String time = request.getParameter("time");
		String requestdate = request.getParameter("requestdate");

		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}

		initNetOrderTimeST(requestdate);
		
		String sql = " select a.STDATE,a.DCCODE,a.LOGISTICS,a.STARTTIME,a.ENDTIME,(b.MAXSKU-a.MAXSKU) MAXSKU,(b.MAXXS-a.MAXXS) MAXXS, " +
				" (b.MAXSUPPLY-a.MAXSUPPLY) MAXSUPPLY,(b.MAXDZSUPPLY-a.MAXDZSUPPLY) MAXDZSUPPLY,(b.MAXYSSUPPLY-a.MAXYSSUPPLY) MAXYSSUPPLY "
				+ " from NETORDERTIMEST a" +
				" JOIN NETORDERTIME b ON (a.dccode=b.dccode AND a.LOGISTICS = b.LOGISTICS AND a.STARTTIME=b.STARTTIME AND a.ENDTIME=b.ENDTIME)"
				+ " where a.stdate=date'"+requestdate+"' and a.dccode=? and a.logistics=? and a.starttime=? and a.endtime=? ";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, new Object[] { dccode, logistics, starttime, endtime }, "netorderqk");
		return elm_cat;
	}

	/**
	 * 查看供应商类型
	 * 
	 * @return
	 */
	public Element getNetOrderSupply() {
		String dccode = request.getParameter("dccode");
		String vendcode = token.getBusinessid();
		String sql = " select dccode, vendcode, vendtype, operatertype, note, inputer, inputdate, isvalid from netlargervender where isvalid='Y' and dccode = '"
				+ dccode + "' and vendcode='" + vendcode + "'";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netordersuplly");
		return elm_cat;
	}

	/**
	 * 查看时间段参数
	 * 
	 * @return
	 */
	public Element getNetOrderTime() {
		String dccode = request.getParameter("dccode");
		String time = request.getParameter("time");
		String logistics = request.getParameter("logistics");
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String sql = " select dccode, starttime, endtime, timejg, maxsku, maxxs, maxsupply, maxdzsupply, maxyssupply, note, upper, uppdate, inputer, inputdate from netordertime where  dccode =? and logistics=? and starttime=? and endtime=?";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, new Object[] { dccode, logistics, starttime, endtime },
				"netordertime");
		return elm_cat;

	}
	
	private HashMap<String, Integer> getNetOrderTime(String dccode, String logistics,String starttime, String endtime){
		String sql = " select dccode,logistics,starttime,endtime,maxsku,maxxs,maxsupply,maxdzsupply,maxyssupply "
				+ " from netordertime "
				+ " where dccode=? and logistics=? and starttime=? and endtime=? ";
		
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, dccode, logistics, starttime, endtime);
		HashMap<String, Integer> resMap = new HashMap<String, Integer>();
		if(list.size()==1){
			HashMap<String, String> map = list.get(0);
			resMap.put("maxsku", Integer.parseInt(map.get("maxsku")));
			resMap.put("maxxs", Integer.parseInt(map.get("maxxs")));
			resMap.put("maxsupply", Integer.parseInt(map.get("maxsupply")));		
			resMap.put("maxdzsupply", Integer.parseInt(map.get("maxdzsupply")));
			resMap.put("maxyssupply", Integer.parseInt(map.get("maxyssupply")));
		}
		return resMap;
	}

	public Element getNetOrderyyPo() {
		String order_serial = request.getParameter("order_serial");
		String dccode = request.getParameter("dccode");
		String logistics = request.getParameter("logistics");
		String sql = "";
		if (logistics.equals("1")) {
			sql = " select a.sheetid,a.orderdate,a.vdeliverdate,a.validdays,name4code(a.logistics,'logistics') logistics,a.refsheetid,a.deadline,count(b.goodsid) qty,sum(nvl(decode(b.pkgvolume,0,0,b.qty/b.pkgvolume),0)) pkgqty, "
					+ " a.destshopid from netorderdetail c,purchase a ,purchaseitem b where c.po_no=a.sheetid and a.sheetid=b.sheetid "
					+ " and c.order_serial=? and c.dccode=? "
					+ " group by a.sheetid,a.orderdate,a.vdeliverdate,a.validdays,a.logistics,a.refsheetid,a.deadline,a.destshopid ";
		} else if (logistics.equals("2")) {
			sql = " select a.refsheetid,a.orderdate,a.vdeliverdate,a.validdays,name4code(a.logistics,'logistics') logistics,a.refsheetid,a.deadline,count(distinct b.goodsid) qty,sum(nvl(decode(b.pkgvolume,0,0,b.qty/b.pkgvolume),0)) pkgqty, "
					+ " a.destshopid from netorderdetail c,purchase a ,purchaseitem b where c.po_no=a.refsheetid and a.sheetid=b.sheetid "
					+ " and c.order_serial=? and c.dccode=? "
					+ "group by a.refsheetid,a.orderdate,a.vdeliverdate,a.validdays,a.logistics,a.refsheetid,a.deadline,a.destshopid ";
		} else {
			throw new DAOException("物流模式异常：" + logistics);
		}
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, new Object[] { order_serial, dccode }, "rowset");
		return elm_cat;
	}

	/**
	 * 查看参数表信息
	 * 
	 * @return
	 */
	public Element getNetParamStopDate() {
		String dccode = request.getParameter("dccode");
		String sql = " select dccode, isyesps, ordertime, orderkfts, stoporderdate, orderlastdate, ordernote from netorderpara where dccode = ?";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, dccode,"netparamstopdate");
		return elm_cat;
	}

	/**
	 * 查询可预约的订单
	 * @param dccode
	 * @param supplier_no
	 * @param logistics
	 * @param request_date
	 * @return
	 */
	public Element getNetSearchPo(String dccode, String supplier_no, String logistics, String request_date) {
		String sql = "";
		// 直送订单展示通知单
		//订货日期<=预约日 request_date ，截至日期 >=预约日
		if ("1".equals(logistics)) {
			sql = " select a.sheetid,a.orderdate,a.vdeliverdate,a.validdays,name4code(a.logistics,'logistics') logistics,a.refsheetid,c.deadline,count(b.goodsid) qty,sum(nvl(decode(b.pkgvolume,0,0,b.qty/b.pkgvolume),0)) pkgqty "
					+ " ,a.destshopid from purchase a "
					+ "  join purchaseitem b on a.sheetid=b.sheetid "
					+ " join purchase0 c on c.sheetid=a.refsheetid "
					+ " join cat_order d on d.sheetid=c.sheetid "
					+ " join shop s on a.destshopid =s.shopid and s.shoptype='22'  "
					+ " where d.venderid =? and a.logistics =? and a.destshopid =? and c.flag<>99 "
					+ " AND a.sheetid NOT IN (SELECT sheetid from netorderstatus where flag=0 and logistics=?)  " +
					" AND a.vdeliverdate<=to_date(?,'yyyy-MM-dd') AND c.deadline>=to_date(?,'yyyy-MM-dd') "
					+ " group by a.sheetid,a.orderdate,a.vdeliverdate,a.validdays,a.logistics,a.refsheetid,c.deadline,a.destshopid ";
		} else if ("2".equals(logistics)) {
			sql = " select a.refsheetid,a.orderdate,a.vdeliverdate,a.validdays,name4code(a.logistics,'logistics') logistics,a.refsheetid,c.deadline,count(distinct b.goodsid) qty,sum(nvl(decode(b.pkgvolume,0,0,b.qty/b.pkgvolume),0)) pkgqty "
					+ " ,a.destshopid from purchase a "
					+ " join purchaseitem b on a.sheetid=b.sheetid "
					+ " join purchase0 c on c.sheetid=a.refsheetid "
					+ " join cat_order d on d.sheetid=c.sheetid "
					+ " join shop s on a.destshopid =s.shopid and s.shoptype='22' "
					+ " where d.venderid =? and a.logistics =? and a.destshopid =? and c.flag<>99 "
					+ " AND d.sheetid NOT IN (SELECT sheetid from netorderstatus where flag=0 and logistics=?)  " +
					" AND a.vdeliverdate<=to_date(?,'yyyy-MM-dd') AND c.deadline>=to_date(?,'yyyy-MM-dd') "
					+ " group by a.refsheetid,a.orderdate,a.vdeliverdate,a.validdays,a.logistics,a.refsheetid,c.deadline,a.destshopid ";
		} else {
			throw new DAOException("物流模式异常：" + logistics);
		}
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql,
				new Object[] { supplier_no, logistics, dccode, logistics,request_date,request_date }, "rowset");
		return elm_cat;
	}

	public Element getOrdersh(Map<String,String[]> parms) {
		// 获取sql语句
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, "6000004000");
		String sql_where = cookFilter(parms).toString();
		String sql = sqlUnit.toString(sql_where);
		Element elm = SqlUtil.getRowSetElement(conn, sql, "netnetordersh", 1, VSSConfig.getInstance()
				.getRowsLimitSoft());
		return elm;
	}

	public Element getOrdershDetail(Map<String,String[]> parms) {
		String[] ss = (String[]) parms.get("logistics");
		int logistics = Integer.parseInt(ss[0]);
		String smid = "6000004001";
		if (logistics == 2) {
			smid = "6000004002";
		}
		parms.remove("logistics");
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, smid);
		String sql_where = cookFilter(parms).toString();
		String sql = sqlUnit.toString(sql_where);
		Element elm = SqlUtil.getRowSetElement(conn, sql, "netnetordershdetail", 1, VSSConfig.getInstance()
				.getRowsLimitSoft());
		return elm;
	}

	/**
	 * 网上预约明细查询
	 * 
	 * @return
	 */
	public Element getOrderVenderdetail(Map<String,String[]> parms) {

		String logistics = "";
		if (request.getParameter("logistics") != null) {
			logistics = request.getParameter("logistics");
		}
		String sql = "";
		if (logistics.equals("1")) {
			sql = " select a.order_serial,a.request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1 pkgnum,a.logistics,Substr(a.order_serial,13,13) as floor,a.rgst_date "
					+ " from netorderhead a join netorderdetail b on a.order_serial=b.order_serial and a.dccode=b.dccode "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on b.po_no=d.sheetid join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(parms).toString();
		} else if (logistics.equals("2")) {
			sql = " select a.order_serial,a.request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1 pkgnum,a.logistics,Substr(a.order_serial,13,13) as floor,a.rgst_date "
					+ " from netorderhead a join netorderdetail b on a.order_serial=b.order_serial and a.dccode=b.dccode "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on b.po_no=d.refsheetid join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(parms).toString();
		} else {
			sql = " select a.order_serial,a.request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1 pkgnum,a.logistics,Substr(a.order_serial,13,13) as floor,a.rgst_date "
					+ " from netorderhead a join netorderdetail b on (a.order_serial=b.order_serial and a.dccode=b.dccode) "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on (b.po_no=d.sheetid and d.venderid=a.supplier_no and a.logistics=d.logistics) join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where "
					+ cookFilter_v(parms).toString()
					+ " union all"
					+ " select a.order_serial,a.request_date,a.start_time,a.end_time,b.po_no,a.supplier_no,c.vendername,"
					+ " e.barcode,g.goodsname,nvl(decode(e.pkgvolume,0,0,e.qty/e.pkgvolume),0) pkgqty,a.temp1 pkgnum,a.logistics,Substr(a.order_serial,13,13) as floor,a.rgst_date "
					+ " from netorderhead a join netorderdetail b on (a.order_serial=b.order_serial and a.dccode=b.dccode) "
					+ " join vender c on a.supplier_no=c.venderid join purchase d on (b.po_no=d.refsheetid and d.venderid=a.supplier_no and a.logistics=d.logistics) join purchaseitem e on d.sheetid=e.sheetid "
					+ " join goods g on e.goodsid=g.goodsid where " + cookFilter_v(parms).toString();
		}
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netorderdetail");
		return elm_cat;
	}

	/**
	 * 网上预约汇总查询
	 * 
	 * @return
	 */
	public Element getOrderVenderhz(Map<String,String[]> parms) {
		String sql = " select a.request_date,a.supplier_no,b.vendername,(a.start_time ||'-'||a.end_time) time,count(po_no) yyds,a.order_serial,"
				+ " sum(d.pkgnum) pkgqty,a.logistics,Substr(a.order_serial,13,13) as floor,a.rgst_date,(select count( distinct rgst_date ) from netorderlog l where l.order_serial=a.order_serial ) yynum "
				+ " from netorderhead a join netorderdetail d on a.order_serial =d.order_serial and a.dccode=d.dccode "
				+ " join vender b on a.supplier_no=b.venderid where "
				+ cookFilter_v(parms).toString()
				+ " group by a.request_date,a.supplier_no,b.vendername,a.start_time ||'-'||a.end_time,a.order_serial,a.logistics,a.rgst_date ";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netorderhz");
		return elm_cat;
	}

	/**
	 * 查询dc,某物流方式配置的预约日期
	 * @return
	 */
	public Element getParamDate() {
		String dccode = request.getParameter("dccode");
		String logistics = request.getParameter("logistics");
		String sql = " select dccode, logistics, monday, tuesday, wednesday, thursday, friday, saturday, sunday from netorderparadate where dccode=? and logistics=?";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, new String[]{dccode,logistics},"netparamdate");
		return elm_cat;
	}
	
	private Document getParamDoc(HttpServletRequest request2) throws IOException, JDOMException {
		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "UTF-8");
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(reader);
		reader.close();
		return doc;
	}

	/**
	 * 获取配送日
	 * @return
	 */
	public Element getPsdate() throws IOException, JDOMException, SQLException {

		String dccode = request.getParameter("dccode");
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}
		String dataList = "0";
		for (Object object : elm_row) {
			Element elm = (Element) object;
			String shopid = elm.getChildTextTrim("destshopid");
			dataList = dataList + ",'" + shopid + "'";
		}

		String sql = " select dccode,shopid,pszq from netpsdate where dccode='" + dccode + "' and shopid in ("
				+ dataList.replace("0,", "") + ")";
		Element elm_sel = new Element("select");
		Element elm_opt = new Element("psdate");
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String vdccode = rs.getString("dccode");
			String vshopid = rs.getString("shopid");
			String vpszq = rs.getString("pszq");
			elm_opt = new Element("psdate");
			elm_opt.setAttribute("dccode", vdccode);
			elm_opt.setAttribute("shopid", vshopid);
			elm_opt.setAttribute("pszq", vpszq);
			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();
		return elm_sel;
	}

	private void insertBody(String orderserial, String dccode, List<String[]> dataList) throws SQLException {
		String sql = " insert into netorderdetail(order_serial,dccode,po_no,po_type,pkgnum,skunum,rgst_date,rgst_name) "
				+ " VALUES ( ?,?,?,?,?,?,sysdate,?) ";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (int i = 0; i < dataList.size(); i++) {
			String[] ss = dataList.get(i);
			SqlUtil.setPS(ps,
					new Object[] { orderserial, dccode, ss[0], ss[1], Integer.parseInt(ss[2]), Integer.parseInt(ss[3]),
							username });
			ps.executeUpdate();
		}
		ps.close();
	}

	private int insertHead(String orderserial, String dccode, String supplier_no, String logistics, String requestdate,
			String starttime, String endtime, int num,String note) throws SQLException {
		String sql = " insert into netorderhead (order_serial, dccode, logistics, supplier_no, request_date, start_time, end_time, flag, rgst_name, rgst_date,temp1,note)"
				+ " VALUES ( ?, ?, ?, ?, to_date('" + requestdate + "','YYYY-MM-DD'), ?, ?, ?,?,sysdate,?,?) ";
		String flag = "Y";
		return SqlUtil.executePS(conn, sql, orderserial, dccode, logistics, supplier_no, starttime,
				endtime, flag, username, num,note);
	}

	/**
	 * 插入操作日志表
	 * @param orderserial
	 * @param dccode
	 * @param dataList
	 * @param potype
	 * @throws SQLException
	 */
	private void insertLog(String orderserial, String dccode, List<String[]> dataList, String potype)
			throws SQLException {
		String sql = " insert into netorderlog(order_serial,dccode,po_no,po_type,rgst_date,rgst_name) "
				+ " VALUES (?,?,?,?,sysdate,?) ";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (int i = 0; i < dataList.size(); i++) {
			String[] ss = dataList.get(i);
			SqlUtil.setPS(ps, new Object[] { orderserial, dccode, ss[0], potype, username });
			ps.executeUpdate();
		}
		ps.close();
	}

	/**
	 * 插入订单状态控制表
	 * @param dataList
	 * @throws SQLException
	 */
	private void insertSheetStatus(List<String[]> dataList) throws SQLException {
		String sql = " insert into netorderstatus(sheetid,flag,logistics,inputer,inputdate) "
				+ " VALUES (?,?,?,?,sysdate) ";
		String flag = "0";
		for (int i = 0; i < dataList.size(); i++) {
			String[] ss = dataList.get(i);
			SqlUtil.executePS(conn, sql, ss[0], flag, ss[1], username);
		}
	}

	/**
	 * 供应商网上预约汇总
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public void netorderhzExcelFile(Map<String,String[]> map, File file) throws SQLException, InvalidDataException, IOException,
			RowsExceededException, WriteException {
		String[] title = { "预约送货日期", "供应商编码", "供应商名称", "预约时段", "预约次数","预约单数", "预约流水号", "箱数", "实际预约箱数","物流模式", "楼层", "VSS预约时间" };

		String sql = " select to_char(a.request_date,'YYYY-MM-DD') request_date,a.supplier_no,b.vendername,(a.start_time ||'-'||a.end_time) time,(select count( distinct rgst_date ) from netorderlog l where l.order_serial=a.order_serial ) yynum,count(po_no) yyds,a.order_serial,"
				+ " sum(d.pkgnum) pkgqty,a.temp1,(case when a.logistics='1' then '直送' when a.logistics='2' then '直通' else '配送' end) logistics,Substr(a.order_serial,13,13) as floor,to_char(a.rgst_date,'YYYY-MM-DD HH24:MI:SS') rgst_date "
				+ " from netorderhead a join netorderdetail d on a.order_serial =d.order_serial and a.dccode=d.dccode "
				+ " join vender b on a.supplier_no=b.venderid where "
				+ cookFilter_v(map).toString()
				+ " group by a.request_date,a.supplier_no,b.vendername,a.start_time ||'-'||a.end_time,a.order_serial,a.logistics,a.rgst_date,a.temp1 ";
		System.err.println(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		Workbook.writeToFile(file, rs, title, "供应商网上预约汇总查询");
		rs.close();
		pstmt.close();
	}

	/**
	 * 零售商网上特殊预约
	 * @return
	 */
	public Element saveNetOrderPo() throws IOException, JDOMException, SQLException {
		String dccode = request.getParameter("dccode");
		String supplier_no = request.getParameter("supplier_no");
		String logistics = request.getParameter("logistics");
		String request_date = request.getParameter("request_date");
		String note = request.getParameter("note");
		String pkgnum = request.getParameter("pkgnum");
		int num = 0;
		if(pkgnum!=null && pkgnum.length()>0){
			num = Integer.parseInt(pkgnum);
		}
		String time = request.getParameter("time");
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String v_time = starttime.substring(0, 2);
		String potype = "0";
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		String orderserial = null;
		int sumsku = 0;
		int floor = 1;
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		for (Element object : elm_row) {
			Element elm = (Element) object;
			String sheetid = "";
			String qty = elm.getChildTextTrim("qty");
			String pkgqty = elm.getChildTextTrim("pkgqty");
			sumsku += Integer.parseInt(qty);

			if (logistics.equals("1")) {
				sheetid = elm.getChildTextTrim("sheetid");
				// 处理直送的楼层问题
				String sql_floor = " select min(t.floor) floor from netgoodsfloor t,purchaseitem b where t.goodsid=b.goodsid and b.sheetid=? and t.dccode=? ";
				PreparedStatement pstmt = this.conn.prepareStatement(sql_floor);
				pstmt.setString(1, sheetid);
				pstmt.setString(2, dccode);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					floor = rs.getInt("floor");
				}
				rs.close();
				dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
			} else if (logistics.equals("2")) {
				sheetid = elm.getChildTextTrim("refsheetid");
				dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
			}
		}
		try {
			conn.setAutoCommit(false);
			// 预约控制表
			int spkg = Integer.parseInt(pkgnum);//实际送货箱数
			spkg = spkg<0?0:spkg;
			addUptSheetNetOrderTimeST(true, request_date,true, dccode, logistics, starttime, endtime, sumsku, spkg,spkg);

			// 直通默认楼层为1
			if (logistics.equals("2")) {
				orderserial = Orderserial.getOrderserial(conn, 1, 1, request_date, v_time);
				// 直送楼层为
			} else if (logistics.equals("1")) {
				orderserial = Orderserial.getOrderserial(conn, 1, floor, request_date, v_time);
				// 其它
			} else {
				orderserial = Orderserial.getOrderserial(conn, 1, 1, request_date, v_time);
			}

			insertHead(orderserial, dccode, supplier_no, logistics, request_date, starttime, endtime, num,note);
			// 增加信息到预约明细
			insertBody(orderserial, dccode, dataList);
			// 增加信息到日志信息
			insertLog(orderserial, dccode, dataList, potype);
			// 增加订单状态
			insertSheetStatus(dataList);

			toITF(orderserial, dccode, 0);

			conn.commit();
			Element res = new Element("result");
			res.addContent(new Element("orderserial").addContent(orderserial));
			return res;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 特殊预约加单操作
	 * 
	 * @return
	 */
	public void saveNetOrderAddPo() throws IOException, JDOMException, SQLException {
		String order_serial = request.getParameter("order_serial");
		String dccode = request.getParameter("dccode");
		String time = request.getParameter("time");
		String logistics = request.getParameter("logistics");
		String pkgnum = request.getParameter("pkgnum");
		String request_date = request.getParameter("request_date");
		String note = request.getParameter("note");
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String potype = "0";
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		ArrayList<String[]> dataList = new ArrayList<String[]>();
		int sumsku = 0;
		int sumpkg = 0;
		for (Element object : elm_row) {
			Element elm = (Element) object;
			String sheetid = "";
			if (logistics.equals("1")) {
				sheetid = elm.getChildTextTrim("sheetid");
			} else if (logistics.equals("2")) {
				sheetid = elm.getChildTextTrim("refsheetid");
			}
			String qty = elm.getChildTextTrim("qty");
			String pkgqty = elm.getChildTextTrim("pkgqty");
			sumsku += Integer.parseInt(qty);
			sumpkg += Integer.parseInt(pkgqty);

			dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
		}

		try {
			conn.setAutoCommit(false);
			// 预约控制表
			int spkg = Integer.parseInt(pkgnum);//实际送货箱数
			spkg = spkg<0?0:spkg;
			//取原单信息
			int oldpkg =getSheetPkg( order_serial,  dccode);
			addUptSheetNetOrderTimeST(false, request_date,true, dccode, logistics, starttime, endtime, sumsku, spkg,oldpkg);

			// 加单修改主表修改信息
			updatehead(order_serial, dccode,pkgnum,note);
			// 加单到预约明细
			insertBody(order_serial, dccode, dataList);
			// 加单信息到日志信息
			insertLog(order_serial, dccode, dataList, potype);
			// 加单订单状态
			insertSheetStatus(dataList);
			toITF(order_serial, dccode, 1);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 供应商加单操作
	 * @return
	 */
	public void saveNetOrderAddVender() throws IOException, JDOMException, SQLException {
		String order_serial = request.getParameter("order_serial");
		String dccode = request.getParameter("dccode");
		String note = request.getParameter("note");
		String logistics = request.getParameter("logistics");
		String time = request.getParameter("time");
		String pkgnum = request.getParameter("pkgnum");
		String request_date = request.getParameter("request_date");
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String potype = "0";
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		ArrayList<String[]> dataList = new ArrayList<String[]>();
		int sumsku = 0;
		for (Object object : elm_row) {
			Element elm = (Element) object;
			String sheetid = "";
			if (logistics.equals("1")) {
				sheetid = elm.getChildTextTrim("sheetid");
			} else if (logistics.equals("2")) {
				sheetid = elm.getChildTextTrim("refsheetid");
			}
			String qty = elm.getChildTextTrim("qty");
			String pkgqty = elm.getChildTextTrim("pkgqty");
			sumsku += Integer.parseInt(qty);
			dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
		}
		try {
			conn.setAutoCommit(false);

			// 更新预约控制表
			//取原单信息
			int oldpkg =getSheetPkg( order_serial,  dccode);
			int spkg = Integer.parseInt(pkgnum);//实际送货箱数
			spkg = spkg<0?0:spkg;
			addUptSheetNetOrderTimeST(false, request_date,false, dccode, logistics, starttime, endtime, sumsku, spkg,oldpkg);
			// 加单修改主表修改信息
			updatehead(order_serial, dccode,pkgnum,note);
			// 加单到预约明细
			insertBody(order_serial, dccode, dataList);
			// 加单信息到日志信息
			insertLog(order_serial, dccode, dataList, potype);
			// 加单订单状态
			insertSheetStatus(dataList);

			toITF(order_serial, dccode, 1);

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 特殊预约减单操作
	 * @return
	 */
	public void saveNetOrderDelPo() throws IOException, JDOMException, SQLException {
		String order_serial = request.getParameter("order_serial");
		String dccode = request.getParameter("dccode");
		String logistics = request.getParameter("logistics");
		String time = request.getParameter("time");
		String pkgnum = request.getParameter("pkgnum");
		String request_date = request.getParameter("request_date");
		String note = request.getParameter("note");
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String potype = "1";
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		ArrayList<String[]> dataList = new ArrayList<String[]>();
		int sumsku = 0;
		int sumpkg = 0;
		for (Element object : elm_row) {
			Element elm = (Element) object;
			String sheetid = "";
			if (logistics.equals("1")) {
				sheetid = elm.getChildTextTrim("sheetid");
			} else if (logistics.equals("2")) {
				sheetid = elm.getChildTextTrim("refsheetid");
			}
			String qty = elm.getChildTextTrim("qty");
			String pkgqty = elm.getChildTextTrim("pkgqty");
			sumsku += Integer.parseInt(qty);
			sumpkg += Integer.parseInt(pkgqty);
			dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
		}
		try {
			conn.setAutoCommit(false);

			// 预约控制表
			//取原单信息
			int oldpkg =getSheetPkg( order_serial,  dccode);
			int spkg = Integer.parseInt(pkgnum);//实际送货箱数
			spkg = spkg<0?0:spkg;
			delSheetNetOrderTimeST(false, request_date,dccode, logistics, starttime, endtime, sumsku, spkg,oldpkg);

			// 减单修改主表修改信息
			updatehead(order_serial, dccode,pkgnum,note);
			// 减单预约明细
			deleteBody(order_serial, dccode, dataList);
			// 减单信息到日志信息
			insertLog(order_serial, dccode, dataList, potype);
			// 减单订单状态
			deleteOrderStatus(dataList);
			toITF(order_serial, dccode, 1);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 供应商减单操作
	 * 
	 * @return
	 */
	public void saveNetOrderDelVender() throws IOException, JDOMException, SQLException {
		String order_serial = request.getParameter("order_serial");
		String dccode = request.getParameter("dccode");
		String logistics = request.getParameter("logistics");
		String time = request.getParameter("time");
		String pkgnum = request.getParameter("pkgnum");
		String request_date = request.getParameter("request_date");
		String note = request.getParameter("note");
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String potype = "1";
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		ArrayList<String[]> dataList = new ArrayList<String[]>();
		int sumsku = 0;
		int sumpkg = 0;
		for (Element object : elm_row) {
			Element elm = (Element) object;
			String sheetid = "";
			if (logistics.equals("1")) {
				sheetid = elm.getChildTextTrim("sheetid");
			} else if (logistics.equals("2")) {
				sheetid = elm.getChildTextTrim("refsheetid");
			}
			String qty = elm.getChildTextTrim("qty");
			String pkgqty = elm.getChildTextTrim("pkgqty");
			sumsku += Integer.parseInt(qty);
			sumpkg += Integer.parseInt(pkgqty);
			dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
		}
		try {
			conn.setAutoCommit(false);
			// 预约控制表
			//取原单信息
			int oldpkg =getSheetPkg( order_serial,  dccode);
			//实际送货箱数
			int spkg = Integer.parseInt(pkgnum);//实际送货箱数
			spkg = spkg<0?0:spkg;
			delSheetNetOrderTimeST(false, request_date,dccode, logistics, starttime, endtime, sumsku,spkg,oldpkg);

			// 减单修改主表修改信息
			updatehead(order_serial, dccode,pkgnum,note);
			// 减单预约明细
			deleteBody(order_serial, dccode, dataList);
			// 减单信息到日志信息
			insertLog(order_serial, dccode, dataList, potype);
			// 减单订单状态
			deleteOrderStatus(dataList);

			toITF(order_serial, dccode, 1);

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 供应商网上预约
	 * 
	 * @return
	 */
	public Element saveNetOrderVender() throws IOException, JDOMException, SQLException {
		String dccode = request.getParameter("dccode");
		String supplier_no = token.getBusinessid();
		String logistics = request.getParameter("logistics");
		String request_date = request.getParameter("request_date");
		String time = request.getParameter("time");
		String note = request.getParameter("note");
		String pkgnum = request.getParameter("pkgnum");
		int num = 0;
		if(pkgnum!=null && pkgnum.length()>0){
			num = Integer.parseInt(pkgnum);
		}
		String starttime = "";
		String endtime = "";
		if (time.length() > 0) {
			String[] ary = time.split(",");
			starttime = ary[0];
			endtime = ary[1];
		}
		String v_time = starttime.substring(0, 2);
		String potype = "0";
		Document doc = getParamDoc(request);
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");
		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		ArrayList<String[]> dataList = new ArrayList<String[]>();
		int sumsku = 0;
		int sumpkg = 0;
		for (Element object : elm_row) {
			Element elm = (Element) object;
			String sheetid = "";
			if (logistics.equals("1")) {
				sheetid = elm.getChildTextTrim("sheetid");
			} else if (logistics.equals("2")) {
				sheetid = elm.getChildTextTrim("refsheetid");
			}
			String qty = elm.getChildTextTrim("qty");
			String pkgqty = elm.getChildTextTrim("pkgqty");
			sumsku += Integer.parseInt(qty);
			sumpkg += Integer.parseInt(pkgqty);
			dataList.add(new String[] { sheetid, logistics, pkgqty, qty });
		}

		try {
			conn.setAutoCommit(false);
			// 预约控制表
			int spkg = Integer.parseInt(pkgnum);//实际送货箱数
			spkg = spkg<0?0:spkg;
			addUptSheetNetOrderTimeST(true, request_date,false, dccode, logistics, starttime, endtime, sumsku, spkg,spkg);
			String orderserial = Orderserial.getOrderserial(conn, 1, 1, request_date, v_time);
			insertHead(orderserial, dccode, supplier_no, logistics, request_date, starttime, endtime, num,note);
			// 增加信息到预约明细
			insertBody(orderserial, dccode, dataList);
			// 增加信息到日志信息
			insertLog(orderserial, dccode, dataList, potype);
			// 增加订单状态
			insertSheetStatus(dataList);
			toITF(orderserial, dccode, 0);
			conn.commit();
			Element res = new Element("result");
			res.addContent(new Element("orderserial").addContent(orderserial));
			return res;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * 写接口表
	 * 
	 * @param order_serial
	 * @param dccode
	 * @param type
	 *            //新增0、修改1、取消2
	 */
	private void toITF(String order_serial, String dccode, int type) {
		String sql = "insert into itf_netorder "
				+ " (dccode, supplier_no, order_serial, po_no, po_type, request_date, start_time, end_time, temp1,temp2,temp3,temp4,temp5,flag) "
				+ " select a.dccode,a.supplier_no,a.order_serial,b.po_no,b.po_type,a.request_date,a.start_time,a.end_time,a.temp1,a.temp2,a.temp3,a.temp4,a.temp5,"
				+ type
				+ " from NETORDERHEAD a,NETORDERDETAIL b where a.order_serial=b.order_serial and a.dccode=b.dccode "
				+ " and a.order_serial=? and a.dccode=?";
		SqlUtil.executePS(conn, sql, order_serial, dccode);
	}

	/**
	 * 更新预约表头操作人信息
	 * 
	 * @param order_serial
	 * @param dccode
	 */
	private void updatehead(String order_serial, String dccode,String pkgnum,String note) {
		String sql = " update netorderhead set updt_name=?,temp1=?,note=?,updt_date=sysdate where order_serial=? and dccode=? ";
		SqlUtil.executePS(conn, sql, username,pkgnum, note,order_serial, dccode);
	}

	/**
	 * 初始化预约控制表
	 * @param requestdate 预约日期
	 */
	private void initNetOrderTimeST(String requestdate) {
		if (!isNetOrderTimeST(requestdate)) {
			String sql = "insert into NETORDERTIMEST "
					+ " select date'"+requestdate+"',DCCODE,LOGISTICS,STARTTIME,ENDTIME,0,0,0,0,0,sysdate "
					+ " from NETORDERTIME ";
			SqlUtil.executeSQL(conn, sql);
		}
	}

	/**
	 * 判断是否已初始化预约控制表
	 * 
	 * @return
	 */
	private boolean isNetOrderTimeST(String requestdate) {
		String sql = " select count(*) from NETORDERTIMEST where stdate=date'"+requestdate+"'";
		List<String> list = SqlUtil.querySQL4SingleColumn(conn, sql);
		if (list.size() > 0) {
			String tmp = list.get(0);
			if (Integer.parseInt(tmp) > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取预约控制表信息
	 * @param requesdate
	 * @param dccode
	 * @param logistics
	 * @param starttime
	 * @param endtime
	 * @param skunum
	 * @param pkgnum
	 * @return
	 */
	private HashMap<String, Integer> getNetOrderTimeST(String requestdate,String dccode, String logistics,String starttime, String endtime){
		String sql = " select stdate,dccode,logistics,starttime,endtime,maxsku,maxxs,maxsupply,maxdzsupply,maxyssupply "
				+ " from NETORDERTIMEST "
				+ " where stdate=date'"+requestdate+"' and dccode=? and logistics=? and starttime=? and endtime=? for update ";
		
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, dccode, logistics, starttime, endtime);
		HashMap<String, Integer> resMap = new HashMap<String, Integer>();
		if(list.size()==1){
			HashMap<String, String> map = list.get(0);
			resMap.put("maxsku", Integer.parseInt(map.get("maxsku")));
			resMap.put("maxxs", Integer.parseInt(map.get("maxxs")));
			resMap.put("maxsupply", Integer.parseInt(map.get("maxsupply")));		
			resMap.put("maxdzsupply", Integer.parseInt(map.get("maxdzsupply")));
			resMap.put("maxyssupply", Integer.parseInt(map.get("maxyssupply")));
		}
		return resMap;
	}
	/**
	 * 新单或加单时操作预约控制表
	 * @param isNew 是否新单，新单时将增加供应商数量等列记录
	 * @param dccode
	 * @param requesdate
	 * @param logistics
	 * @param starttime
	 * @param endtime
	 * @param skunum
	 * @param pkgnum箱数
	 */
	private void addUptSheetNetOrderTimeST(boolean isNew, String requesdate,boolean ts, String dccode, String logistics, String starttime, String endtime, int skunum, int pkgnum,int oldpkgnum) {
		// 查询当前预约控制信息
		HashMap<String, Integer> map = getNetOrderTimeST(requesdate, dccode, logistics, starttime, endtime);
		if (map.size() == 0) {
			throw new DAOException(starttime + " - " + endtime + "时段可预约控制获取异常，请尝试其它时段");
		}
		//取可预约数
		HashMap<String, Integer> canMap = getNetOrderTime(dccode, logistics, starttime, endtime);
		if (canMap.size() == 0) {
			throw new DAOException(starttime + " - " + endtime + "时段预约控制获取异常，请尝试其它时段");
		}
		
		int cursku = map.get("maxsku");
		int curxs = map.get("maxxs");
		int cursupply = map.get("maxsupply");
		int curdzsupply = map.get("maxdzsupply");
		int curyssupply = map.get("maxyssupply");

		int cansku = canMap.get("maxsku");
		int canxs = canMap.get("maxxs");
		int cansupply = canMap.get("maxsupply");
		int candzsupply = canMap.get("maxdzsupply");
		int canyssupply = canMap.get("maxyssupply");
		
		// 判断sku数
		cursku += skunum;
		if (!ts && cursku > cansku) {
			throw new DAOException("该时段最大sku数已满，请选择其它时段");
		}
		
		// 判断箱数
		if(isNew){
			curxs +=pkgnum;
		}else{
			curxs = curxs -oldpkgnum+pkgnum;
		}
		
		if (!ts && curxs > canxs) {
			throw new DAOException("该时段最大箱数已满，请选择其它时段");
		}
		
		if(isNew){ //仅新单时核加应商数量
			// 判断综合
			if (isZH(dccode, token.getBusinessid())) {
				curyssupply += 1;
				if (!ts && curyssupply > canyssupply) {
					throw new DAOException("您是综合预约，该时段综合预约数已满，请选择其它时段");
				}
				
			}else{
				// 判断大宗
				if ( isDZ(dccode, pkgnum)) {
					curdzsupply += 1;
					if (!ts && curdzsupply > candzsupply) {
						throw new DAOException("您是大宗预约，该时段大宗预约数已满，请选择其它时段");
					}
				}else{
					cursupply += 1;
					if (!ts && cursupply > cansupply) {
						throw new DAOException("该时段预约供应商已满，请选择其它时段");
					}
					
				}
			}
		}else{
			if (! isZH(dccode, token.getBusinessid())) {
				//如果是修改单据，需要判断大宗和普通供应商之间的切换
				boolean olddz = isDZ(dccode, oldpkgnum);
				boolean dz = isDZ(dccode, pkgnum);
				//如果原来是大宗，现在不是大宗则核减大宗数量，增加普通数量
				if(olddz && !dz){
					curdzsupply -= 1;
					cursupply += 1;
					if (!ts && cursupply > cansupply) {
						throw new DAOException("该时段预约供应商已满，请选择其它时段");
					}
				}
				//如果原来是普通供应商，现在是大宗供应商则，减少普通数量
				if(!olddz && dz){
					curdzsupply += 1;
					cursupply -= 1;
					if (!ts && curdzsupply > candzsupply) {
						throw new DAOException("您是大宗预约，该时段大宗预约数已满，请选择其它时段");
					}
				}
			}
		}
		updateNetOrderTimeST(requesdate,dccode, logistics, starttime, endtime, cursku, curxs, cursupply, curdzsupply, curyssupply);
	}

	/**
	 * 减单或删单对预约控制表操作
	 * @param isDel 是否取消单据，取消单据时将核加供应商数量
	 * @param dccode
	 * @param logistics
	 * @param starttime
	 * @param endtime
	 * @param skunum
	 * @param pkgnum
	 */
	private void delSheetNetOrderTimeST(boolean isDel,String requesdate, String dccode, String logistics, String starttime,
			String endtime, int skunum, int pkgnum,int oldpkgnum) {
		// 查询当前预约控制信息
		HashMap<String, Integer> map = getNetOrderTimeST(requesdate, dccode, logistics, starttime, endtime);
		if (map.size() == 0) {
			throw new DAOException(starttime + " - " + endtime + "时段可预约控制获取异常，请尝试其它时段");
		}
		//取可预约数
		HashMap<String, Integer> canMap = getNetOrderTime(dccode, logistics, starttime, endtime);
		if (canMap.size() == 0) {
			throw new DAOException(starttime + " - " + endtime + "时段预约控制获取异常，请尝试其它时段");
		}
		
		int cursku = map.get("maxsku");
		int curxs = map.get("maxxs");
		int cursupply = map.get("maxsupply");
		int curdzsupply = map.get("maxdzsupply");
		int curyssupply = map.get("maxyssupply");

		int cansupply = canMap.get("maxsupply");
		int candzsupply = canMap.get("maxdzsupply");

		// 消减sku数目
		cursku -= skunum;
		// 核减箱数
		if(isDel){
			curxs -=oldpkgnum;
		}else{
			curxs = curxs -oldpkgnum+pkgnum;
		}
		
		//仅取消单据时消减供应商数量
		if(isDel){
			// 判断综合
			if (isZH(dccode, token.getBusinessid())) {
				curyssupply -= 1;
			}else{
				// 判断大宗
				if (isDZ(dccode, pkgnum)) {
					curdzsupply -= 1;
				}else{
					cursupply -= 1;
				}
			}
		}else{
			if (! isZH(dccode, token.getBusinessid())) {
				//如果是修改单据，需要判断大宗和普通供应商之间的切换
				boolean olddz = isDZ(dccode, oldpkgnum);
				boolean dz = isDZ(dccode, pkgnum);
				//如果原来是大宗，现在不是大宗则核减大宗数量，增加普通数量
				if(olddz && !dz){
					curdzsupply -= 1;
					cursupply += 1;
					if ( cursupply > cansupply) {
						throw new DAOException("该时段预约供应商已满，请选择其它时段");
					}
				}
				//如果原来是普通供应商，现在是大宗供应商则，减少普通数量
				if(!olddz && dz){
					curdzsupply += 1;
					cursupply -= 1;
					if ( curdzsupply > candzsupply) {
						throw new DAOException("您是大宗预约，该时段大宗预约数已满，请选择其它时段");
					}
				}
			}
			
		}
		updateNetOrderTimeST(requesdate,dccode, logistics, starttime, endtime, cursku, curxs, cursupply, curdzsupply, curyssupply);
	}


	/**
	 * 更新预约控制
	 * @param requestdate 预约日期
	 * @param dccode DC
	 * @param logistics 物流模式
	 * @param starttime 时间段
	 * @param endtime
	 * @param skunum sku数
	 * @param pkgnum 箱数
	 * @param supply 供应商数
	 * @param dz 大宗供应商数
	 * @param zh 综合供应商数
	 */
	private void updateNetOrderTimeST(String requestdate,String dccode, String logistics, String starttime, String endtime, int skunum,
			int pkgnum, int supply, int dz, int zh) {
		String sql = " update NETORDERTIMEST  set maxsku=?,maxxs=?,maxsupply=?,maxdzsupply=?,maxyssupply=? "
				+ " where stdate=date'"+requestdate+"' and dccode=? and logistics=? and starttime=? and endtime=?  ";
		int i = SqlUtil.executePS(conn, sql, skunum, pkgnum, supply, dz, zh, dccode, logistics, starttime, endtime);
		if (i == 0) {
			throw new DAOException(starttime + " - " + endtime + "时段预约控制更新异常，请尝试其它时段");
		}
	}

	/**
	 * 是否大宗
	 * 根据箱数判断
	 * @param pkgnum
	 * @return
	 */
	private boolean isDZ(String dccode, int pkgnum) {
		String sql = "select dzpqty from netorderpara where dccode=?";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, dccode);
		if (list.size() > 0) {
			HashMap<String, String> map = list.get(0);
			String tmp = map.get("dzpqty");
			if (tmp != null && tmp.length() > 0) {
				int dzpqty = Integer.parseInt(tmp);
				if (pkgnum >= dzpqty) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 是否综合
	 * @param dccode
	 * @param skunum
	 * @return
	 */
	private boolean isZH(String dccode,String venderid) {
		String sql = "select count(*) from netlargervender where isvalid='Y' and dccode=? and vendcode=? and vendtype=1";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, dccode,venderid);
		if(list.size()>0){
			String tmp = list.get(0);
			if(Integer.parseInt(tmp)>0){
				return true;
			}
		}
		return false;
	}

}
