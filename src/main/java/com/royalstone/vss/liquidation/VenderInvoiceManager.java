package com.royalstone.vss.liquidation;

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

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XErr;

/**
 * 此模块用于上传发票信息. 对发票类型和发票号码, 只作基本的格式检查, 不作内容检查.
 * 
 * @author baijian
 * @date 20061105
 */
public class VenderInvoiceManager {
	public VenderInvoiceManager(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 此方法验证上传的发票数据格式，返回处理后的elm_input
	 * 
	 * @param elm_input
	 * @return
	 * @throws IOException
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public Element validate(Element elm_input, String venderid) throws IOException, InvalidDataException,
			SQLException {
		boolean valid = true;
		if (elm_input == null) throw new InvalidDataException("elm_set is invalid!");

		String sql_un = " SELECT vi.sheetid FROM venderinvoiceitem vit "
				+ " JOIN venderinvoice vi ON (vi.sheetid = vit.sheetid) "
				+ " WHERE vi.venderid=? AND vit.invoiceno = ? AND vit.invoicetype = ?";
		PreparedStatement ps = conn.prepareStatement(sql_un);
		ResultSet rs = null;
		List lst = elm_input.getChildren("row");
		HashMap map = new HashMap();
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
			Element elm_invoicetype = elm.getChild("invoicetype");
			String invoicetype = elm_invoicetype.getTextTrim();
			if (invoicetype == null) {
				valid = false;
			} else if (!ValueAdapter.isNumber(invoicetype)) {
				elm_invoicetype.setAttribute("title", "发票类型必须为数字");
				elm_invoicetype.setAttribute("error", "warning");
				valid = false;
			}

			// 拼发票号＋发票类型号(简单检查)
			String it = invoiceno + invoicetype;
			if (map.get(it) == null) {
				map.put(it, it);
				/*
				 * 发票唯一性检查与数据库比较，复杂检查
				 */
				ps.setString(1, venderid);
				ps.setString(2, invoiceno);
				ps.setString(3, invoicetype);
				rs = ps.executeQuery();
				if (rs.next()) {
					String sheetid = rs.getString(1);
					elm_invoiceno.setAttribute("title", "发票号已在发票接收单：" + sheetid + " 中录入");
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
		if (!valid) xerr = new XErr(200, "验证数据格式错误，请检查改正后重新提交");
		return elm_input;
	}

	public double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	/**
	 * 保存数据到表
	 * 
	 * @param elm_input
	 *            此节点内包含上传的发票信息. 节点名为row_set.
	 * @throws SQLException
	 * @throws Exception
	 */
	public void save(Element elm_input, String sheetid_payment) throws Exception {
		String sheetid_invoice = getInvoiceSheetid(sheetid_payment);
		if (sheetid_invoice == null) throw new InvalidDataException("该付款申请单还没有对应的发票接收单");

		try {
			conn.setAutoCommit(false);
			this.sheetid = sheetid_invoice;
			saveDetail(elm_input);
			conn.commit();
			xerr = new XErr(0, "保存成功");
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 此方法返回根据参数 sheetid_payment 返回相对应的发票id 返回Null 则说明该发票号不存在，请做好空值判断
	 * 
	 * @param sheetid_payment
	 * @return
	 * @throws SQLException
	 */
	private String getInvoiceSheetid(String sheetid_payment) throws SQLException {
		String rel_sheetid = null;
		String sql_search = "SELECT sheetid FROM venderinvoice " + " WHERE refsheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql_search);
		pstmt.setString(1, sheetid_payment);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) rel_sheetid = rs.getString(1).trim();

		rs.close();
		pstmt.close();
		return rel_sheetid;
	}

	/**
	 * 保存发票信息表体
	 * 
	 * @param elm_input
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws ParseException
	 */
	private void saveDetail(Element elm_input) throws InvalidDataException, SQLException, ParseException {
		String sql = " INSERT INTO venderinvoiceitem ( sheetid, seqno, invoiceno, invoicetype, invoicedate,  "
				+ " taxableamt, taxrate, taxamt "
				+ " ) VALUES ( ?, venderinvoiceitem_id.nextval, ?, ?, to_date(?,'YYYY-MM-DD'), ?, ?, ? )  ";

		if (elm_input == null) throw new InvalidDataException("elm_set is invalid!");
		List lst = elm_input.getChildren("row");
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for (int i = 0; i < lst.size(); i++) {
			Element elm = (Element) lst.get(i);

			String invoiceno = setInvoiceno(elm.getChild("invoiceno").getTextTrim());
			String invoicetype = elm.getChild("invoicetype").getTextTrim();
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

			pstmt.setString(1, this.sheetid);
			pstmt.setString(2, invoiceno);
			pstmt.setString(3, invoicetype);
			pstmt.setString(4, invoicedate);
			pstmt.setDouble(5, dTaxableamt);
			pstmt.setDouble(6, dTaxrate);
			pstmt.setDouble(7, ValueAdapter.parseDouble(taxamt));
			pstmt.executeUpdate();
		}
		pstmt.close();

	}

	/**
	 * 计算价额
	 * 
	 * @param tax
	 * @param taxamt
	 * @return
	 */
	static private double countTax(double tax, double taxamt) {
		double rel = 0.0;
		rel = taxamt * 100 / tax;
		return rel;
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
	private String parseDate(String ymd) throws InvalidDataException, ParseException {
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
	 * 将特殊格式的日期统一转换成yyyy-mm-dd ymd 的格式可以为 2006.1.1 ; 1,1 ; 2006,1,1 ; 1,1 ;
	 * 2006/1/1 ; 1/1 ; 没有年的日期将自动转为当年 月份与天数超过有效值将自动推后，例如：2006.13.50 ->
	 * 2007-01-19
	 * 
	 * @see 此方法需使用jdk1.5以上
	 * @param ymd
	 * @return
	 * @throws InvalidDataException
	 * @throws ParseException
	 */
	/*
	 * private static String parseDate(String ymd) throws InvalidDataException,
	 * ParseException { String rel = ""; if (ymd != null && ymd.length() > 1) {
	 * String temp = ymd.replace(".", "-"); temp = temp.replace(",", "-"); temp
	 * = temp.replace("/", "-"); String[] arr_date = temp.split("-");
	 * 
	 * if (arr_date.length == 2 || arr_date.length == 3) { rel =
	 * compagesArrayDate(arr_date); } else { throw new
	 * InvalidDataException(" date string can't format:" + ymd); } } else {
	 * throw new InvalidDataException(" date string is null"); }
	 * 
	 * return rel; }
	 */
	/**
	 * 将数组arr里的数字拼接成标准日期格式 支持的格式为 2006.1.5 或 1.5
	 * 
	 * @param arr
	 * @return
	 * @throws InvalidDataException
	 * @throws ParseException
	 */
	private static String compagesArrayDate(String[] arr) throws InvalidDataException, ParseException {
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

	/**
	 * 该方法将发票号自动补零
	 * @param invoiceno
	 * @return
	 */
	private String setInvoiceno(String invoiceno) {
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

	public XErr getXerr() {
		return xerr;
	}

	final private Connection	conn;

	private String				sheetid	= "";

	private XErr				xerr	= new XErr(0, "OK");

}
