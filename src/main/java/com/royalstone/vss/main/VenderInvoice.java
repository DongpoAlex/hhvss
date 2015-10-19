package com.royalstone.vss.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.royalstone.security.Token;
import com.royalstone.util.DXSHelper;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.Sheet;

/**
 * @author baij
 * 供应商发票录入
 */
public class VenderInvoice extends Sheet{

	private String	payshopid;
	public VenderInvoice(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}
	
	/**
	 * 获取发票明细
	 * @return
	 */
	public Element getItem(){
		String sheetid = request.getParameter("sheetid");
		String sql="select a.sheetid,a.seqno,a.invoicetypeid,a.invoicecode,a.invoiceno,a.invoicedate,a.goodsdesc,b.invoicetypename,a.taxrate,a.taxableamt,a.taxamt,(a.taxableamt+a.taxamt) amttax from venderinvoiceitem a,invoicetype b where a.invoicetypeid=b.invoicetypeid(+) and a.sheetid=? order by seqno";
		return SqlUtil.getRowSetElement(conn, sql, new String[]{sheetid}, "rowset");
	}
	public Element getHead(){
		String sheetid = request.getParameter("sheetid");
		String sql="select a.sheetid,a.pnsheetid,a.venderid,a.flag,a.operator,b.flag payflag from venderinvoice a,paymentnote b where a.pnsheetid=b.sheetid and a.sheetid=?";
		return SqlUtil.getRowSetElement(conn, sql, new String[]{sheetid}, "rowset");
	}
	
	public Element addItem(){
		String sheetid = request.getParameter("sheetid");
		String invoiceno = request.getParameter( "invoiceno" );
		String invoicecode = request.getParameter( "invoicecode" );
		String invoicetypeid = request.getParameter( "invoicetypeid" );
		String invoicedate = request.getParameter( "invoicedate" );
		String goodsdesc = request.getParameter( "goodsdesc" );
		String taxrate = request.getParameter( "taxrate" );
		String taxamt = request.getParameter( "taxamt" );
		String taxableamt = request.getParameter( "taxableamt" );
		
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
		if( invoiceno == null || invoiceno.length() == 0 ) throw new InvalidDataException( "invoiceno is invalid!" );
		if( invoicecode == null || invoicecode.length() == 0 ) throw new InvalidDataException( "invoicecode is invalid!" );
		if( invoicedate == null || invoicedate.length() == 0 ) throw new InvalidDataException( "invoicedate is invalid!" );
		if( taxrate == null || taxrate.length() == 0 ) throw new InvalidDataException( "taxrate is invalid!" );
		if( taxamt == null || taxamt.length() == 0 ) throw new InvalidDataException( "taxamt is invalid!" );
		if( taxableamt == null || taxableamt.length() == 0 ) throw new InvalidDataException( "taxableamt is invalid!" );
		
		if( getDupRows( invoiceno, invoicecode ) >0 ) throw new InvalidDataException( "此发票已经录入:" + invoiceno );
		
		double rate = ValueAdapter.parseDouble( taxrate );
		double amt_tax = ValueAdapter.parseDouble( taxamt );
		double amt_taxable = ValueAdapter.parseDouble( taxableamt );
		
		String sql = " INSERT INTO venderinvoiceitem " +
		" (sheetid, seqno, invoicetypeid, invoicecode, invoiceno, invoicedate, goodsdesc, taxableamt, taxrate, taxamt) " +
		" VALUES ( ?,venderinvoiceitem_id.nextval, ?, ?, ?,to_date(?,'YYYY-MM-DD'), ?, ?, ?, ? ) ";
		
		SqlUtil.executePS(conn, sql, new Object[]{sheetid,invoicetypeid,invoicecode,invoiceno,invoicedate,goodsdesc,amt_taxable,rate,amt_tax});
		
		return getItem();
	}
	
	public Element delItem(){
		String sheetid = request.getParameter("sheetid");
		int seqno   = Integer.valueOf(request.getParameter("seqno"));
		String sql="delete from venderinvoiceitem where sheetid=? and seqno=?";
		SqlUtil.executePS(conn, sql, new Object[]{sheetid,seqno});
		return getItem();
	}
	
	public Element updateItem(){
		String sheetid = request.getParameter("sheetid");
		String invoiceno = request.getParameter( "invoiceno" );
		String invoicecode = request.getParameter( "invoicecode" );
		String invoicetypeid = request.getParameter( "invoicetypeid" );
		String invoicedate = request.getParameter( "invoicedate" );
		String goodsdesc = request.getParameter( "goodsdesc" );
		String taxrate = request.getParameter( "taxrate" );
		String taxamt = request.getParameter( "taxamt" );
		String taxableamt = request.getParameter( "taxableamt" );
		int seqno   = Integer.valueOf(request.getParameter("seqno"));
		
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
		if( invoiceno == null || invoiceno.length() == 0 ) throw new InvalidDataException( "invoiceno is invalid!" );
		if( invoicecode == null || invoicecode.length() == 0 ) throw new InvalidDataException( "invoicecode is invalid!" );
		if( invoicedate == null || invoicedate.length() == 0 ) throw new InvalidDataException( "invoicedate is invalid!" );
		if( taxrate == null || taxrate.length() == 0 ) throw new InvalidDataException( "taxrate is invalid!" );
		if( taxamt == null || taxamt.length() == 0 ) throw new InvalidDataException( "taxamt is invalid!" );
		if( taxableamt == null || taxableamt.length() == 0 ) throw new InvalidDataException( "taxableamt is invalid!" );
		
		double rate = ValueAdapter.parseDouble( taxrate );
		double amt_tax = ValueAdapter.parseDouble( taxamt );
		double amt_taxable = ValueAdapter.parseDouble( taxableamt );
		
		 String sql = " UPDATE venderinvoiceitem " +
         " SET invoicedate=to_date(?,'YYYY-MM-DD') ,goodsdesc=? , taxableamt=?, taxrate=? , taxamt=? ,  invoicetypeid=? " +
         " WHERE sheetid=? AND seqno=?";
		 
		 SqlUtil.executePS(conn, sql, new Object[]{invoicedate,goodsdesc,amt_taxable,rate,amt_tax,invoicetypeid,sheetid,seqno});
		 
		return getItem();
	}
	
	public Element confirm() throws SQLException{
		String sheetid = request.getParameter("sheetid");
		String contacttel = request.getParameter("contacttel");
		String contact = request.getParameter("contact");
		String sql;
		
		// 检查账套是否冻结
//		sql=" select c.isfreacc from venderinvoice a,paymentnote b,venderpaybalance c where a.pnsheetid=b.sheetid and b.buid=c.buid and b.payshopid=c.payshopid and b.venderid=c.venderid and a.sheetid=?";
//		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
//		if(VenderPayBalance.isFreacc(list.size()==0?null:list.get(0))){
//			throw new InvalidDataException("当前结算体公司已冻结。");
//		}
		//检查单据目前的状态
		if( getFlag(sheetid) != 0 ) throw new InvalidDataException( "该单据已经提交, 不能重复提交！" );
		
		//判断发票表体信息是否为空，为空则拒绝提交
		sql = "select count(*) from venderinvoice join venderinvoiceitem on venderinvoice.sheetid=venderinvoiceitem.sheetid where venderinvoice.sheetid = ? ";
		String s = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid).get(0);
		if(Integer.valueOf(s)==0){
			throw new InvalidDataException("该发票录入单没有发票明细，无法提交！");
		}
			
		try{
			conn.setAutoCommit(false);
			sql = " UPDATE venderinvoice set flag=2, checkdate=sysdate, checker=?,contact=? ,contacttel=? ,operator=? WHERE sheetid = ? ";
			SqlUtil.executePS(conn, sql, new Object[]{token.getBusinessid(),contact,contacttel,getOperator(),sheetid});
			
			//发DXS传单请求
			String proc = "fi_venderinvoice('"+sheetid+"')";
			DXSHelper.send2DXSTask(conn, DXSHelper.SEE_NODE_NAME, "venderinvoice", "sheetid", sheetid, "R", "", "", "", proc,"发票录入单");
			conn.commit();
		}catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
		}
		Element res = new Element("rowset");
		res.addContent(new Element("sheetid").addContent("sheetid"));
		return res;
	}

	public Element batchCheckItem() throws SQLException, JDOMException, IOException{
		Document doc = getParamDoc();
		Element elm_root = doc.getRootElement();
		
		boolean valid = true;
		if (elm_root == null) throw new InvalidDataException("elm_set is invalid!");

		String sql = " SELECT vi.sheetid FROM venderinvoiceitem vit "
				+ " JOIN venderinvoice vi ON (vi.sheetid = vit.sheetid) "
				+ " WHERE vi.venderid=? AND vit.invoiceno = ? AND vit.invoicecode = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = null;
		List<Element> lst = elm_root.getChildren("row");
		HashMap<String,String> primaryMap = new HashMap<String,String>();
		for (int i = 0; i < lst.size(); i++) {
			Element elm = (Element) lst.get(i);
			Element elm_seqno = new Element("seqno");
			elm_seqno.setText(String.valueOf(i + 1));
			elm.addContent(elm_seqno);

			// 发票ID
			Element elm_invoiceno = elm.getChild("invoiceno");
			String invoiceno = elm_invoiceno.getTextTrim();
			if (invoiceno == null || invoiceno.length() > 8) {
				elm_invoiceno.setAttribute("title", "发票号应为8位");
				elm_invoiceno.setAttribute("error", "warning");
				valid = false;
			} else if (!ValueAdapter.isNumber(invoiceno)) {
				elm_invoiceno.setAttribute("title", "发票号必须为数字");
				elm_invoiceno.setAttribute("error", "warning");
				valid = false;
			} else {
				// 发票位补零
				invoiceno = setInvoiceno(invoiceno);
				elm_invoiceno.setText(invoiceno);
			}

			// 发票类型号
			Element elm_invoicecode = elm.getChild("invoicecode");
			String invoicecode = elm_invoicecode.getTextTrim();
			if (invoicecode == null) {
				valid = false;
			} else if (!ValueAdapter.isNumber(invoicecode)) {
				elm_invoicecode.setAttribute("title", "发票类型必须为数字");
				elm_invoicecode.setAttribute("error", "warning");
				valid = false;
			}

			// 拼发票号＋发票类型号(简单检查)
			String it = invoiceno + invoicecode;
			if (primaryMap.get(it) == null) {
				primaryMap.put(it, it);
				/*
				 * 发票唯一性检查与数据库比较，复杂检查
				 */
				SqlUtil.setPS(ps, token.getBusinessid(),invoiceno,invoicecode);
				rs = ps.executeQuery();
				if (rs.next()) {
					String sheetid = rs.getString(1);
					elm_invoiceno.setAttribute("title", "发票号已在发票录入单：" + sheetid + " 中录入");
					elm_invoiceno.setAttribute("error", "warning");
					valid = false;
				}
				rs.close();

				// ============================================================================//
			} else {
				elm_invoiceno.setAttribute("title", "发票号有重复");
				elm_invoiceno.setAttribute("error", "warning");
				valid = false;
			}
			
			//发票种类
			Element elm_invoicetypeid = elm.getChild("invoicetypeid");
			String invoicetypeid = elm_invoicetypeid.getTextTrim();
			if (invoicetypeid == null) {
				valid = false;
			} else if (!ValueAdapter.isNumber(invoicetypeid)) {
				elm_invoicecode.setAttribute("title", "发票种类必须为数字");
				elm_invoicecode.setAttribute("error", "warning");
				valid = false;
			}
			int typeid = Integer.parseInt(invoicetypeid);
			if(typeid<1 || typeid>5){
				elm_invoicecode.setAttribute("title", "发票种类数字为1 - 5");
				elm_invoicecode.setAttribute("error", "warning");
				valid = false;
			}
			

			// 日期
			Element elm_invoicedate = elm.getChild("invoicedate");
			String invoicedate = elm_invoicedate.getTextTrim();
			if (!ValueAdapter.isDate(invoicedate)) {
				try {
					String newdate = parseDate(invoicedate);
					elm_invoicedate.setText(newdate);
				}
				catch (Exception e) {
					elm_invoicedate.setAttribute("title", "日期格式应为1.1 表示当年的1月1号");
					elm_invoicedate.setAttribute("error", "warning");
					valid = false;
				}
			}

			// 税率
			Element elm_taxrate = elm.getChild("taxrate");
			String taxrate = elm_taxrate.getTextTrim();
			double dTax = ValueAdapter.parseDouble(taxrate);
			if (!ValueAdapter.isDecimal(taxrate)) {
				elm_taxrate.setAttribute("title", "无法识别的税率");
				elm_taxrate.setAttribute("error", "warning");
				valid = false;
			} else {
				if (dTax != 0.00 && dTax != 17.00 && dTax != 13.00 && dTax != 14.94 && dTax != 6.00
						&& dTax != 4.00 && dTax != 3.00) {
					elm_taxrate.setAttribute("title", "您输入的税率不被支持");
					elm_taxrate.setAttribute("error", "warning");
					valid = false;
				}
			}

			// 税额
			Element elm_taxamt = elm.getChild("taxamt");
			String taxamt = elm_taxamt.getTextTrim();

			if (!ValueAdapter.isDecimal(taxamt)) {
				elm_taxamt.setAttribute("title", "无法识别的税额");
				elm_taxamt.setAttribute("error", "warning");
				valid = false;
			} else {
				if (dTax == 0.00) {
					elm_taxamt.setText("0.00");
				} else {
					elm_taxamt.setText(String.valueOf(ValueAdapter.parseDouble(taxamt)));
				}
			}

			if (valid) {
				// 返回价额
				// 计算价额
				Element elm_taxableamt = new Element("taxableamt");
				Element elm_taxablevalue = elm.getChild("taxablevalue");
				if (dTax == 0.00) {
					String taxablevalue = elm_taxablevalue.getTextTrim();
					if (!ValueAdapter.isDecimal(taxablevalue)) {
						elm_taxablevalue.setAttribute("title", "无法识别金额");
						elm_taxablevalue.setAttribute("error", "warning");
						valid = false;
					} else {
						elm_taxableamt.setText(taxablevalue);
						elm_taxablevalue.setText(taxablevalue);
					}
				} else {
					double dTaxamt = ValueAdapter.parseDouble(taxamt);
					double dTaxrate = ValueAdapter.parseDouble(taxrate);
					double dTaxableamt = round(countTax(dTaxrate, dTaxamt), 2, BigDecimal.ROUND_HALF_UP);
					double dTaxablevalue = round(dTaxableamt + dTaxamt, 2, BigDecimal.ROUND_HALF_UP);

					elm_taxableamt.setText(String.valueOf(dTaxableamt));
					elm_taxablevalue.setText(String.valueOf(dTaxablevalue));
				}
				elm.addContent(elm_taxableamt);
			}
		}
		ps.close();
		Element elm = new Element("result");
		if (!valid){
			elm.addContent("验证数据格式错误，请检查改正后重新提交");
		}else{
			elm.addContent("OK");
		}
		elm_root.addContent(elm);
		return elm_root;
	}
	
	public Element batchAddItem() throws Exception{
		String sheetid = request.getParameter("sheetid");
		Document doc = getParamDoc();
		Element elm_root = doc.getRootElement();
		
		try {
			conn.setAutoCommit(false);
			saveDetail(sheetid,elm_root);
			conn.commit();
		}
		catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
		
		return getItem();
	}
	private void saveDetail(String sheetid,Element elm_input) throws InvalidDataException, SQLException, ParseException {
		String sql = " INSERT INTO venderinvoiceitem " +
		" (sheetid, seqno, invoicetypeid, invoicecode, invoiceno, invoicedate, goodsdesc, taxableamt, taxrate, taxamt) " +
		" VALUES ( ?,venderinvoiceitem_id.nextval, ?, ?, ?,to_date(?,'YYYY-MM-DD'), ?, ?, ?, ? ) ";
		if (elm_input == null) throw new InvalidDataException("elm_set is invalid!");
		List<Element> lst = elm_input.getChildren("row");
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for (int i = 0; i < lst.size(); i++) {
			Element elm = (Element) lst.get(i);
			String invoiceno = setInvoiceno(elm.getChild("invoiceno").getTextTrim());
			String invoicecode = elm.getChild("invoicecode").getTextTrim();
			String invoicetypeid = elm.getChild("invoicetypeid").getTextTrim();
			String invoicedate = elm.getChild("invoicedate").getTextTrim();
			String taxrate = elm.getChild("taxrate").getTextTrim();
			String taxamt = elm.getChild("taxamt").getTextTrim();
			String taxablevalue = elm.getChild("taxablevalue").getTextTrim();

			if (!ValueAdapter.isDate(invoicedate)) {
				invoicedate = parseDate(invoicedate);
			}

			// 计算价额
			double dTaxamt = ValueAdapter.parseDouble(taxamt);
			double dTaxrate = ValueAdapter.parseDouble(taxrate);
			double dTaxableamt = countTax(dTaxrate, dTaxamt);

			
			//零税率，税额强制为零，价额=界面录入的价税合计
			if (dTaxrate == 0) {
				dTaxableamt = ValueAdapter.parseDouble(taxablevalue);
			}
			SqlUtil.setPS(pstmt, new Object[]{sheetid,invoicetypeid,invoicecode,invoiceno,invoicedate,"",dTaxableamt,dTaxrate,dTaxamt});
			pstmt.executeUpdate();
		}
		pstmt.close();
	}
	
	public SqlFilter cookFilter(Map<String,String[]> map) {
		SqlFilter filter = new SqlFilter();
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
	 * 在发票接收单内查找发票号和发票类型相同的记录数
	 * @param invoiceno
	 * @param invoicecode
	 * @return
	 * @throws SQLException
	 */
	private int getDupRows ( String invoiceno, String invoicecode )
	{
		String sql = " SELECT count(*) FROM venderinvoiceitem i "
			+ " JOIN venderinvoice v ON (v.sheetid = i.sheetid) "
			+ " WHERE v.venderid=? AND i.invoiceno = ? AND i.invoicecode = ? ";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, new Object[]{token.getBusinessid(),invoiceno,invoicecode});
		return Integer.valueOf(list.get(0));
	}
	
	private int getFlag ( String sheetid )
	{
		String sql = "SELECT flag FROM venderinvoice WHERE sheetid=? ";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		return Integer.valueOf(list.get(0));
	}

	/**
	 * 该方法将发票号自动补零
	 * @param invoiceno
	 * @return
	 */
	static public String setInvoiceno(String invoiceno) {
		int num = 8 - invoiceno.length();
		String relno = "";
		if (num != 0) {
			for (int j = 0; j < num; j++) {
				relno += "0";
			}
			relno += invoiceno;
		} else {
			return invoiceno;
		}
		return relno;
	}

	/**
	 *将特殊格式的日期统一转换成yyyy-mm-dd ymd 的格式可以为 2006.1.1 ; 1,1 ; 2006,1,1 ; 1,1 ;
	 * 2006/1/1 ; 1/1 ; 没有年的日期将自动转为当年 月份与天数超过有效值将自动推后，例如：2006.13.50 ->
	 * 2007-01-19
	 * 
	 * @param ymd
	 * @return
	 * @throws InvalidDataException
	 * @throws ParseException
	 */
	static public String parseDate(String ymd) throws InvalidDataException, ParseException {
		String rel = "";
		if (ymd != null && ymd.length() > 1) {
			String temp = ymd.replaceAll("/", "-");
			temp = temp.replaceAll(",", "-");
			temp = temp.replaceAll("\\.", "-");
			String[] arr_date = temp.split("-");
			if (arr_date.length == 2 || arr_date.length == 3) {
				rel = compagesArrayDate(arr_date);
			} else {
				throw new InvalidDataException(" date string can't format:" + ymd);
			}
		} else {
			throw new InvalidDataException(" date string is null");
		}
		return rel;
	}
	/**
	 * 将数组arr里的数字拼接成标准日期格式YYYY-MM-DD 支持的格式为 2006.1.5 或 1.5
	 * @param arr
	 * @return
	 * @throws InvalidDataException
	 * @throws ParseException
	 */
	static public String compagesArrayDate(String[] arr) throws InvalidDataException, ParseException {
		String yy = "";
		String mm = "";
		String dd = "";
		String rel = "";
		for (int i = 0; i < arr.length; i++) {
			String strTemp = arr[i];
			try {
				Integer.parseInt(strTemp);
			}
			catch (Exception e) {
				throw new InvalidDataException(" date string can't format");
			}
		}
		if (arr.length == 3) { // 假定数据中含有 年月日
			if (arr[0].length() != 4 && arr[0].length() != 2) throw new InvalidDataException(
					" date string can't format");
			if (arr[0].length() == 2) {
				StringBuffer sb_year = new StringBuffer("20"); // 补上年世纪
				sb_year.append(arr[0]);
				yy = sb_year.toString();
			} else {
				yy = arr[0];
			}
			mm = arr[1];
			dd = arr[2];
		} else if (arr.length == 2) { // 假定数据中含有 月日
			if (arr[0].length() > 2 || arr[1].length() > 2) throw new InvalidDataException(
					" date string can't format");
			yy = Integer.toString(new GregorianCalendar().get(GregorianCalendar.YEAR));
			mm = arr[0];
			dd = arr[1];
		} else {
			throw new InvalidDataException(" date string can't format");
		}
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		rel = s.format(s.parse(yy + "-" + mm + "-" + dd));
		return rel;
	}
	
	static private double countTax(double tax, double taxamt) {
		double rel = 0.0;
		rel = taxamt * 100 / tax;
		return rel;
	}
	static public double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}
	
	private String getOperator(){
		String res = "";
		try {
			String[] ss = token.getEnv("defaultPayer");
			if(ss.length>0){
				res = ss[0];
			}
		} catch (NamingException e) {
		} catch (SQLException e) {
		}
		return res;
	}
	protected void initInnerValue(String sheetid){
		this.title = "华润万家发票录入单";
	}
}
