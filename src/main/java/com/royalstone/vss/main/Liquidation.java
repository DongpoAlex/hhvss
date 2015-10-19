package com.royalstone.vss.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.royalstone.common.Sheetid;
import com.royalstone.security.Token;
import com.royalstone.util.DXSHelper;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.Sheet;
import com.royalstone.workbook.Workbook;

/**
 * @author baij
 *         结算对账
 */
public class Liquidation extends Sheet {

	private String	payshopid;
	
	public Liquidation(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public Element batchCheck() throws JDOMException, IOException, SQLException {
		Document doc = getParamDoc();
		Element elm_root = doc.getRootElement();

		boolean valid = true;
		if (elm_root == null)
			throw new InvalidDataException("elm_set is invalid!");

		String sql = "select a.buid,b.buname,a.sheetid,a.sheettype,a.docdate,a.payflag,(a.unpaidamt+a.taxamt17+a.taxamt13) unpaidamt "
				+ " from unpaidsheet0 a,buinfo b where a.buid=b.buid and a.buid=? and a.sheetid=? and a.sheettype=? and a.venderid=? and unpaidamt>0 ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = null;
		
		@SuppressWarnings("unchecked")
		List<Element> lst = elm_root.getChildren("row");
		HashMap<String, String> primaryMap = new HashMap<String, String>();
		for (int i = 0; i < lst.size(); i++) {
			String note = "";
			String buname = "";
			String unpaidamt = "0";

			Element elm = (Element) lst.get(i);

			String buid = elm.getChildText("buid");
			String sheetid = elm.getChildText("sheetid");
			String sheettype = elm.getChildText("sheettype");

			if (!("2301".equals(sheettype) || "5205".equals(sheettype))) {
				note = "单据类型错误。";
			}

			SqlUtil.setPS(ps, buid,sheetid,sheettype,token.getBusinessid());
			rs = ps.executeQuery();

			if (rs.next()) {
				int flag = rs.getInt("payflag");
				if (flag == 1) {
					note = "通过验证。";
				} else if (flag == -1) {
					note = "合同已经过期，请尽快与采购员联系。";
				} else if (flag == 0) {
					note = "该单据冻结，请与对账员联系！";
				}
				String tmp = sheetid + sheettype;
				if (primaryMap.get(tmp) != null) {
					note = "提交的数据重复。";
				} else {
					primaryMap.put(tmp, tmp);
				}

				buname = rs.getString("buname");
				unpaidamt = rs.getString("unpaidamt");
			} else {
				note = "该单据不存在或已对账，请核实后提交！";
			}
			rs.close();
			
			if (note.equals("通过验证。")) {
				elm.setAttribute("error", "ok");
			} else {
				valid = false;
				elm.setAttribute("error", "warning");
			}
			Element elm_seqno = new Element("seqno").setText(String.valueOf(i + 1));
			elm.addContent(elm_seqno);
			Element elm_note = new Element("note").setText(note);
			elm.addContent(elm_note);
			Element elm_buid = new Element("buid").setText(buid);
			elm.addContent(elm_buid);
			Element elm_buname = new Element("buname").setText(buname);
			elm.addContent(elm_buname);
			Element elm_unpaidamt = new Element("unpaidamt").setText(unpaidamt);
			elm.addContent(elm_unpaidamt);

		}

		ps.close();

		Element elm = new Element("result");
		if (!valid) {
			elm.addContent("验证数据格式错误，请检查改正后重新提交");
		} else {
			elm.addContent("OK");
		}
		elm_root.addContent(elm);

		return elm_root;
	}

	public Element checkPayshopStatus() {
		String payshopid = request.getParameter("payshopid");

		Element res = new Element("result");
		// 检查结算主体是否冻结
		String lqSheetid = "";
		String pnSheetid = "";
		boolean isFreeze = true;
		boolean isHas = false;
		String maxAmt = "0";
		String minAmt = "0";
		String contracttype = "0";
		String payableAmt = "0";
		boolean isProssing = true;
		String venderid = token.getBusinessid();
		String sql = "call tl_getvenderpayableamt(?,?,?,?,?,?,?,?)";
		CallableStatement psc;
		try {
			psc = conn.prepareCall(sql);
			psc.setString(1, venderid);
			psc.setString(2, payshopid);
			psc.registerOutParameter(3, Types.INTEGER);
			psc.registerOutParameter(4, Types.VARCHAR);
			psc.registerOutParameter(5, Types.DOUBLE);
			psc.registerOutParameter(6, Types.DOUBLE);
			psc.registerOutParameter(7, Types.DOUBLE);
			psc.registerOutParameter(8, Types.INTEGER);
			psc.execute();

			isHas = psc.getBoolean(3);
			isFreeze = VenderPayBalance.isFreacc(psc.getString(4));
			payableAmt = psc.getString(5);
			minAmt = psc.getString(6);
			maxAmt = psc.getString(7);
			contracttype = psc.getString(8);
			psc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//检查LQ和PN
		if (!isFreeze) {
			lqSheetid = getProssingLQSheetid(payshopid, token.getBusinessid());
			isProssing = lqSheetid == null ? false : true;
			if (lqSheetid == null) {
				pnSheetid = getProssingPNSheetid(payshopid, token.getBusinessid());
				isProssing = pnSheetid == null ? false : true;
			}
		}

		res.addContent(new Element("isHas").addContent(String.valueOf(isHas)));
		res.addContent(new Element("isFreeze").addContent(String.valueOf(isFreeze)));
		res.addContent(new Element("isProssing").addContent(String.valueOf(isProssing)));
		res.addContent(new Element("lqSheetid").addContent(lqSheetid));
		res.addContent(new Element("pnSheetid").addContent(pnSheetid));
		// 最高勾单金额、最底勾单金额
		res.addContent(new Element("maxAmt").addContent(maxAmt));
		res.addContent(new Element("minAmt").addContent(minAmt));
		// 经营类型
		res.addContent(new Element("contracttype").addContent(contracttype));
		// 应付账款余额
		res.addContent(new Element("payableAmt").addContent(payableAmt));

		return res;
	}

	public SqlFilter cookCanSelectedSheetFilter(Map<String, String[]> map) {
		SqlFilter filter = new SqlFilter();
		String[] ss = null;

		// filter.add("a.sheettype in (2301)");
		filter.add(" a.sheettype in(2301,5205) ");
		filter.add(" a.payflag=1 ");
		filter.add(" a.unpaidamt>0 ");

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.sheetid IN (" + Values.toString4in(ss) + ") ");
			return filter;
		}

		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.payshopid IN (" + Values.toString4in(ss) + ") ");
		}

		ss = (String[]) map.get("hqmajorid");
		if (ss != null && ss.length > 0) {
			if (!"-1".equals(ss[0])) {
				filter.add(" a.majorid IN (" + Values.toString4in(ss) + ") ");
			}
		}

		ss = (String[]) map.get("docdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" a.docdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("docdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" a.docdate <= " + ValueAdapter.std2mdy(ss[0]));
		}
		return filter;
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		SqlFilter filter = new SqlFilter(map);
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.sheetid = " + Values.toString4String(ss[0]));
			return filter;
		}

		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.payshopid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("buid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.buid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0) {
			filter.add(" a.flag IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("editdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" a.editdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("editdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" a.editdate <= " + ValueAdapter.std2mdy(ss[0]));
		}

		return filter;
	}

	/**
	 * 导出可对账单据
	 * @param file
	 */
	public void excelCanSelectedSheetFilter(File file) {
		String sql = " select a.buid,a.sheettype,a.sheetid,(a.unpaidamt+a.taxamt17+a.taxamt13) unpaidamt,to_char(docdate,'yyyy-MM-dd') docdate,to_char(duedate,'yyyy-MM-dd') duedate,b.sheettypename "
				+ " from unpaidsheet0 a,sheettype b where a.sheettype=b.sheettype and "
				+ cookCanSelectedSheetFilter(getParams()).toString() + " order by a.docdate,a.sheetid ";
		try {
			ResultSet rs = SqlUtil.querySQL(conn, sql);
			String[] tt = { "BUID", "单据类型", "单据号", "结算金额", "发生日期", "应结日期", "单据类型名称" };
			com.royalstone.workbook.Workbook.writeToFile(file, rs, tt, "导出.xls");
			SqlUtil.close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * 导出具体单据
	 * @param file
	 */
	public void excelSheet(File file) {
		String sheetid = this.getParamNotNull("sheetid");
		try {
			FileOutputStream fout = new FileOutputStream( file );
			Workbook book = new Workbook(fout);
			String[] tt = {"单据编码","结算单编码","区域","结算主体","供应商编码","供应商名称",
       		"对账日期","处理日期","备注","申请状态","结算状态"};
			String sql4Head = "select a.sheetid,a.pnsheetid,b.buname,c.payshopname,a.venderid,d.vendername," +
					" a.editdate,a.checkdate,a.note,decode(a.flag,1,'提交',100,'对账成功',-100,'对账失败',-101,'已作废') flagname," +
					" decode(a.flag,1,''对账中'',decode(e.flag,null,''已删除'',name4paymentnoteflag(e.flag))) payflagname " +
					" from liquidation a " +
					" join buinfo b on a.buid=b.buid " +
					" join payshop c on a.payshopid=c.payshopid " +
					" join vender d on (a.venderid=d.venderid and a.buid=d.buid) " +
					" left join paymentnote e on e.sheetid=a.pnsheetid " +
					" where a.sheetid=? ";
			book.addSheet(conn,sql4Head ,new Object[]{sheetid},"对账申请单",tt);
			
			String sqlBody = " select a.seqno,c.buname,b.sheettypename,a.sheetno,a.unpaidamt,a.note " +
					" from liquidationitem a " +
					" join sheettype b on a.sheettype=b.sheettype " +
					" join buinfo c on a.buid=c.buid " +
					" where a.SheetID=? ";
			tt = new String[]{"顺序","区域","单据类型","单据编码","对账金额","备注"};
			book.addSheet(conn,sqlBody ,new Object[]{sheetid},"对账申请单明细",tt);
			book.write();
			fout.close();			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * 获取可结算单据
	 * 
	 * @return
	 */
	public Element getCanSelectedSheet() {
		String sql = " select a.buid,a.sheettype,a.sheetid,(a.unpaidamt+a.taxamt17+a.taxamt13) unpaidamt,a.docdate,to_char(duedate,'yyyy-MM-dd') duedate,b.sheettypename "
				+ "from unpaidsheet0 a ,sheettype b where a.sheettype=b.sheettype and "
				+ cookCanSelectedSheetFilter(getParams()).toString() + " order by docdate";
		return SqlUtil.getRowSetElement(conn, sql, "rowset");
	}

	/**
	 * 是否有待处理的对账申请单
	 * 
	 * @param payshopid
	 * @return
	 */
	private String getProssingLQSheetid(String payshopid, String venderid) {
		String sql = "select sheetid from liquidation where payshopid=? and venderid=? and flag=1";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, payshopid, venderid);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	private String getProssingPNSheetid(String payshopid, String venderid) {
		// 检查是否有新建状态付款单
		String sql = "select sheetid from paymentnote where payshopid=? and venderid=? and flag=1";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, payshopid, venderid);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	private void insertBody(String sheetid, List<String[]> dataList) throws SQLException {
		String sql = " INSERT INTO liquidationitem ( sheetid, seqno, buid, sheettype, sheetno, unpaidamt, flag ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, 0) ";
		PreparedStatement ps = conn.prepareStatement(sql);

		for (int i = 0; i < dataList.size(); i++) {
			String[] ss = dataList.get(i);
			SqlUtil.setPS(ps, new Object[] { sheetid, (i + 1), ss[0], ss[1], ss[2], ss[3] });
			ps.executeUpdate();
		}

		ps.close();
	}

	private int insertHead(String sheetid, String buid, String payshopid, String majorid) throws SQLException {
		String sql = " INSERT INTO liquidation ( sheetid, venderid, payshopid, buid, majorid,flag,editor,editdate ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, sysdate ) ";
		return SqlUtil.executePS(conn, sql, new Object[] { sheetid, token.getBusinessid(), payshopid, buid, majorid, 1,
				token.loginid });
	}

	/**
	 * 检查是否课类供应商
	 * 
	 * @return
	 */
	public Element isMajorVender() {
		Element res = new Element("result");
		boolean isMajorVender = isMajorVender(token.getBusinessid(),token.getBuid());
		res.addContent(new Element("isMajorVender").addContent(String.valueOf(isMajorVender)));
		return res;
	}

	private boolean isMajorVender(String venderid,String buid) {
		boolean res = false;
		String sql = "select prepayflag from vender where venderid=? and buid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, venderid,buid);
		if (list.size() > 0 && "1".equals(list.get(0))) {
			res = true;
		}
		return res;
	}

	/**
	 * 生成新的对账单据
	 * 
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SQLException
	 */
	public Element newSheet() throws JDOMException, IOException, SQLException {
		// 检查
		String payshopid = getParamNotNull("payshopid");
		String hqmajorid = getParamNotNull("hqmajorid");
		String buid = getParamNotNull("buid");
		
		//检查对账次数
		LQConfig lqConfig = new LQConfig(request, conn, token);
		int count = lqConfig.getVenderLQCount(buid, token.getBusinessid(),payshopid);
		if(count<=0){
			throw new SQLException("贵司本月当前对账次数剩余0次，暂不能提交对账申请，请于次月1日再提交或联系对账员!");
		}
		if (getProssingLQSheetid(payshopid, token.getBusinessid()) != null) {
			throw new SQLException("已有对账申请正在处理中，无法新建。");
		}
		if (getProssingPNSheetid(payshopid, token.getBusinessid()) != null) {
			throw new SQLException("已有新建结算单等待审核中，无法新建。");
		}

		Document doc = getParamDoc();
		Element elm_root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elm_row = elm_root.getChildren("row");

		if (elm_row.size() == 0) {
			throw new SQLException("没有勾选单据。");
		}

		ArrayList<String[]> dataList = new ArrayList<String[]>();
		for (Object object : elm_row) {
			Element elm = (Element) object;
			String tmpbuid = elm.getChildTextTrim("buid");
			String sheetid = elm.getChildTextTrim("sheetid");
			String sheettype = elm.getChildTextTrim("sheettype");
			String unpaidamt = elm.getChildTextTrim("unpaidamt");
			dataList.add(new String[] { tmpbuid, sheettype, sheetid, unpaidamt });
		}

		try {
			conn.setAutoCommit(false);

			String sheetid = Sheetid.getSheetid(conn, 7001, payshopid);
			insertHead(sheetid, buid, payshopid, hqmajorid);
			insertBody(sheetid, dataList);

			// 发起传单指令
			String proc = "fi_liquidation('" + sheetid + "')";
			DXSHelper.send2DXSTask(conn, DXSHelper.SEE_NODE_NAME, "liquidation", "sheetid", sheetid, "I", "", "", "", proc, "对账申请单");

			// 增加对账次数
			if(! lqConfig.addLQCount(buid, token.getBusinessid(),payshopid)){
				throw new SQLException("贵司本月当前对账次数剩余0次，暂不能提交对账申请，请于次月1日再提交或联系对账员!");
			}
			
			conn.commit();
			Element res = new Element("result");
			res.addContent(new Element("sheetid").addContent(sheetid));
			return res;
		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
			throw e;
		}
	}

	protected void initInnerValue(String sheetid) {
		String sql = "select payshopid from liquidation where sheetid=?";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, sheetid);
		if (list.size() > 0) {
			HashMap<String, String> map = list.get(0);
			this.payshopid = map.get("payshopid");
		} else {
			this.payshopid = "";
		}

		HashMap<String, String> map = PayShop.getTitleInfo(conn, this.payshopid);
		this.title = map.get("title");
		this.logo = map.get("logo");
		if (title == null)
			title = "";
	}
}
