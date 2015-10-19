package com.royalstone.vss.main;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.DXSHelper;
import com.royalstone.util.fiscal.FiscalValue;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.Sheet;
import com.royalstone.workbook.Workbook;

/**
 * @author baij
 * 结算单查询
 */
public class Paymentsheet extends Sheet {
	
	private double payamt;

	public Paymentsheet(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return  new SqlFilter(map).
		addFilter2String( "venderid", "a.venderid",true).
		addFilter2String( "sheetid", "a.sheetid",true).
		addFilter2String( "payshopid", "a.payshopid",true).
		addFilter2String( "buid", "a.buid",true).
		addFilter2String( "flag", "a.flag",true).
		addFilter2String( "invoiceflag", "a.invoiceflag",true).
		addFilter2MinDate( "date_min", "a.planpaydate",true).
		addFilter2MaxDate( "date_max", "a.planpaydate",true).
		addFilter2String( "reqflag", "e.reqflag",true).
		addFilter2MinDate( "reqdate_min", "e.reqdate",true).
		addFilter2MinDate( "reqdate_max", "e.reqdate",true);
	}
		
	public Element show() {
		String sheetid = request.getParameter("sheetid");
		Element elm_sheet = setSheetInfo(sheetid);
		elm_sheet.setAttribute("showType","detail");
		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getBody(sheetid));
		elm_sheet.addContent(getBodyChargeWithTax(sheetid));
		elm_sheet.addContent(getBodyChargeNotTax(sheetid));
		elm_sheet.addContent(getBodyInvoice(sheetid));
		return elm_sheet;
	}
	protected void initInnerValue(String sheetid){
		String sql="select payshopid,payamt from paymentnote where sheetid=?";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, sheetid);
		if(list.size()>0){
			HashMap<String, String> map = list.get(0);
			this.payamt = Double.parseDouble(map.get("payamt")) ;
		}else{
			this.payamt = 0 ;
		}
		title = "华润万家供应商对账单";
	}
	
	
	public Element showGroupBy(){
		String sheetid = request.getParameter("sheetid");
		return showGroupByCharge(sheetid);
	}
	private Element showGroupByCharge(String sheetid){
		Element elm_sheet = setSheetInfo(sheetid);
		elm_sheet.setAttribute("showType","group");
		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getBodyGrouyByShop(sheetid));
		elm_sheet.addContent(getBodyChargeWithTaxGroupByCharge(sheetid));
		elm_sheet.addContent(getBodyChargeNotTaxGroupByCharge(sheetid));
		elm_sheet.addContent(getBodyInvoice(sheetid));
		return elm_sheet;
	}
	
	public Element sjPrint(){
		String sheetid = request.getParameter("sheetid");
		
		Element elmCharge = getBodyChargeInvoice(sheetid);
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("title","华润万家账扣费用收据");
		elm_sheet.setAttribute("logo", logo);
		elm_sheet.setAttribute("xsl", "../xsl/paymentsheet_sj.xsl");
		elm_sheet.setAttribute("xslprint", "../xsl/paymentsheet_sj.xsl");
		
		//当费用记录为0时，直接返回
		if("0".equals(elmCharge.getAttributeValue("row_count"))){
			return elm_sheet.addContent(elmCharge);
		}
				
		elm_sheet.addContent(getHead(sheetid));
		
		elm_sheet.addContent(elmCharge);
		List<Element> list = elmCharge.getChildren("row");
		payamt = 0.0;
		for (Element elm : list) {
			String str  = elm.getChildText("chargeamt");
			payamt += Double.parseDouble(str);
		}
		//计算合计金额大写
		elm_sheet.addContent(new Element("payamtToChinese").addContent(payamtToChinese()));
		return elm_sheet;
	}
	
	/**
	 * 表头打印
	 * @return
	 */
	public Element headPrint(){
		String sheetid = request.getParameter("sheetid");
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("title","华润万家结算单");
		elm_sheet.setAttribute("logo", logo);
		elm_sheet.setAttribute("xsl", "../xsl/paymentsheet_head.xsl");
		elm_sheet.setAttribute("xslprint", "../xsl/paymentsheet_head.xsl");
		initInnerValue(sheetid);
		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getFinFee(sheetid));
		return elm_sheet;
	}
	
	public Element getHead(){
		String sheetid = request.getParameter("sheetid");
		return getHead(sheetid);
	}
	
	
	//表头
	public Element getHead(String sheetid){
		String sql4Head="select a.SheetID,a.BUID,b.buname,a.PayShopID,c.payshopname,a.VenderID,d.vendername,a.MajorID," +
		"a.PayModeID,e.paymodename,a.DueDate,a.PlanPayDate," +
		"a.PayableAmt,a.TaxAmt17,a.TaxAmt13,a.ChargeAmt,a.SusPayAmt,a.FinFeeAmt," +
		"a.InvTotalAmt17,a.InvTotalAmt13,a.InvTotalAmt0,a.invtotaltaxamt17,a.invtotaltaxamt13," +
		"a.PayAmt,a.UnpaidAmt,a.FreezeAmt,a.LastPVSheetID,a.LastPVAmt,a.LastPVDate," +
		"a.Note,a.Checker,a.CheckDate,a.editor,a.editdate,a.inveditor,a.approver," +
		"a.emsno,a.emsdate,a.emsremark,a.flag,name4paymentnoteflag(a.flag) flagname,f.bankaccount,f.bankbranchname," +
		"c.taxername,c.taxpayerid,c.taxeradress,d.contracttype,decode(d.daypayflag,0,'周付','天付') daypay,c.taxertelno,c.bankid,c.bankaccount cbankaccount "+
		" from paymentnote a " +
		" join buinfo b on a.buid=b.buid " +
		" join payshop c on a.payshopid=c.payshopid " +
		" join vender d on (a.venderid=d.venderid and a.buid=d.buid) " +
		" join paymode e on a.paymodeid=e.paymodeid " +
		" left join venderbank f ON (f.buid = d.buid and f.vendercode = d.vendercode  and f.defaaccountflag=1 )" +
		" where sheetid=? and rownum=1";
		Element elm = SqlUtil.getRowSetElement(conn,sql4Head,sheetid, "head");
		elm.getChild("row").addContent(new Element("payamtToChinese").addContent(payamtToChinese()));
		return elm;
	}
	private String payamtToChinese(){
		String res = "NAC";
		if(this.payamt>0){
			FiscalValue f = new FiscalValue(this.payamt);
			res = f.toChinese();
		}
		return res;
	}
	
	
	//结算明细
	public Element getBody(String sheetid){
		String unit4Body1 = "SELECT a.sheetid, a.docbuid,b.buname, a.doctype,c.SHEETTYPENAME, a.docno, a.shopid,d.shopname,a.docdate," +
		"a.paytypeid,e.paytypename, f.categoryid,f.categoryname,(a.payamt+a.PayTaxAmt17+a.PAYTAXAMT13) totalpayamt, " +
		"a.logisticsid,name4logistics(a.logisticsid) logisticsname, a.remark " +
		" FROM paymentnoteitem a " +
		" join buinfo b on a.DOCBUID=b.buid " +
		" join sheettype c on a.DOCTYPe=c.sheettype " +
		" join shop d on (a.sysshopid=d.sysshopid) " +
		" join paytype e on e.paytypeid=a.paytypeid" +
		" join category f on f.hqcategoryid=a.majorid " +
		" where a.SheetID=? order by a.shopid,a.docdate";
		return SqlUtil.getRowSetElement(conn,unit4Body1,sheetid, "item");
	}
	
	//结算单据，按门店汇总
	private Element getBodyGrouyByShop(String sheetid){
		String unit4Body1 = "SELECT count(*) rowcount, a.doctype,c.sheettypename,a.shopid,d.shopname," +
		"　sum(a.payamt+a.PayTaxAmt17+a.PAYTAXAMT13) totalpayamt,MAX(a.docdate) docdate,e.paytypename,f.categoryid,f.categoryname " +
		" FROM paymentnoteitem a " +
		" join sheettype c on a.doctype=c.sheettype " +
		" join shop d on (a.SHOPID=d.shopid and a.docbuid=d.buid) " +
		" join paytype e on e.paytypeid=a.paytypeid" +
		" join category f on f.hqcategoryid=a.majorid " +
		" where a.SheetID=? group by a.doctype,c.sheettypename,a.shopid,d.shopname,e.paytypename,f.categoryid,f.categoryname order by a.doctype,a.shopid";
		return SqlUtil.getRowSetElement(conn,unit4Body1,sheetid, "groupitem");
	}
	
	/**
	 * 取财务费用
	 * @param sheetid
	 * @return
	 */
	private Element getFinFee(String sheetid){
		String sql = "SELECT nvl(sum(case when p.FinFeeTypeId='01' then p.Amt else 0 end),0) as AMT1," +
				"nvl(sum(case when p.FinFeeTypeId='02' then p.Amt else 0 end),0) as AMT2," +
				" nvl(sum(case when p.FinFeeTypeId='03' then p.Amt else 0 end),0) as AMT3," +
				" nvl(sum(case when p.FinFeeTypeId='05' then p.Amt else 0 end),0) as AMT5," +
				" nvl(sum(case when p.FinFeeTypeId in('01','02','03','05') then p.Amt else 0 end),0) as AMTOther" +
				" from PaymentNoteFinFee p " +
				" where p.SheetID=? ";
				return SqlUtil.getRowSetElement(conn,sql,sheetid, "finfee");
	}
	
	//扣项一(票扣明细)
    private  Element getBodyChargeWithTax(String sheetid){
		return getCharge(sheetid,"charge_tax",1);
	}
	//扣项二(非票扣明细)
	private  Element getBodyChargeNotTax(String sheetid){
		return getCharge(sheetid,"charge_notax",0);
	}
	//扣项一(票扣明细)
	private  Element getBodyChargeWithTaxGroupByCharge(String sheetid){
		return getChargeGroupByCharge(sheetid,"group_charge_tax",1);
	}
	//扣项二(非票扣明细)
	private  Element getBodyChargeNotTaxGroupByCharge(String sheetid){
		return getChargeGroupByCharge(sheetid,"group_charge_notax",0);
	}
	
	/**
	 * 收据费用
	 * @param sheetid
	 * @return
	 */
	private  Element getBodyChargeInvoice(String sheetid){
		String unit4Body2 = "select 1 as Type, b.ChargeName as chargename, sum(a.ChargeAmt) as chargeamt " +
				" from paymentnoteCharge a, ChargeCode b " +
				" where a.ChargeCodeID = b.ChargeCodeID " +
				" and a.InvoiceMode IN (0,5) " +
				" and a.SheetID = ? " +
				" group by b.ChargeName " +
				" union all " +
				" select 3 as Type, b.FinFeetypeName as chargename, a.Amt as chargeamt " +
				" FROM PaymentNoteFinFee a, FinFeeType b " +
				" where a.FinFeeTypeID = b.FinFeeTypeID " +
				" and b.FinFeeTypeID in ('01','02','03') " +
				" and a.SheetID = ? " +
				" order by 1,2";
				return SqlUtil.getRowSetElement(conn,unit4Body2,new Object[]{sheetid,sheetid}, "charge");
	}
	
	/**
	 * 费用查询
	 * @param sheetid
	 * @param elmName
	 * @param invoiceMode 0=非票扣 1=可票扣
	 * @return
	 */
	private Element getCharge(String sheetid,String elmName,int invoiceMode){
		String unit4Body2 = "SELECT a.noteno, a.buid,b.buname, a.shopid,c.shopname, a.chargecodeid,d.chargename, a.chargeamt, TL_GetChargeMemo(a.chargecodeid,a.remark) remark" +
		" from paymentnoteCharge a " +
		" join buinfo b on a.buid=b.buid " +
		" join shop c on (a.sysshopid=c.sysshopid) " +
		" join chargecode d on ( a.chargecodeid=d.chargecodeid)" +
		" where a.sheetid=? and a.InvoiceMode=? ";
		return SqlUtil.getRowSetElement(conn,unit4Body2,new Object[]{sheetid,invoiceMode}, elmName);
	}
	
	
	/**
	 * 费用查询，按费用类型汇总
	 * @param sheetid
	 * @param elmName
	 * @param invoiceMode 0=非票扣 1=可票扣
	 * @return
	 */
	private Element getChargeGroupByCharge(String sheetid,String elmName,int invoiceMode){
		String unit4BodyGorup2 = "SELECT count(*) rowcount, a.chargecodeid,d.chargename, sum(a.chargeamt) chargeamt" +
		" from paymentnoteCharge a " +
		" join chargecode d on (a.chargecodeid=d.chargecodeid)" +
		" where a.sheetid=? and a.InvoiceMode=? " +
		" group by a.chargecodeid,d.chargename";
		return SqlUtil.getRowSetElement(conn,unit4BodyGorup2,new Object[]{sheetid,invoiceMode}, elmName);
	}
	
	//发票明细
	private Element getBodyInvoice(String sheetid){
		String unit4Body3 = " select sheetid,serialid,a.invoicetypeid,b.invoicetypename,invoicecode,invoiceno,invoicedate,goodsdesc,taxrate,taxableamt,taxamt,totalamt from paymentnoteInvoice a,invoicetype b where a.INVOICETYPEID=b.invoicetypeid and SheetID=? ";
		return SqlUtil.getRowSetElement(conn,unit4Body3,sheetid, "invoice");
	}
	
	/**
	 * 导出付款单信息，包括明细
	 * @param file
	 */
	public void excelSheet(File file) {
		String sheetid = this.getParamNotNull("sheetid");
		try {
			FileOutputStream fout = new FileOutputStream( file );
			Workbook book = new Workbook(fout);
			String[] tt = {"单据编码","单据状态","区域","结算主体","供应商编码","供应商名称",
       		"费用金额","应付金额","计划付款日","开票申请状态","开票申请时间","付款次数"};
			String sql4Head = "select a.SheetID,name4paymentnoteflag(a.flag) flagname," +
					" b.buname,c.payshopname,a.VenderID,d.vendername,a.chargeamt,a.PayAmt,a.PlanPayDate," +
					" decode(decode(a.drawinvflag,-1,-1,2,2,e.reqflag),-1,'无需开票',0,'未申请',1,'已申请',2,'已开票','未知') reqflagname, " +
					" e.reqdate,a.PayTimes " +
					" from PaymentNote a " +
					" join buinfo b on a.buid=b.buid " +
					" join payshop c on a.payshopid=c.payshopid " +
					" join vender d on (a.venderid=d.venderid and a.buid=d.buid) " +
					" left join paymentnotevenderask e on e.sheetid=a.sheetid " +
					" where a.sheetid=? ";
			book.addSheet(conn,sql4Head ,new Object[]{sheetid},"结算单",tt);
			
			String sqlBody = "SELECT b.buname,c.SHEETTYPENAME, a.docno,a.docdate, d.shopname," +
			"e.paytypename, (a.payamt+a.PayTaxAmt17+a.PAYTAXAMT13) totalpayamt, " +
			"name4logistics(a.logisticsid) logisticsname, a.remark " +
			" FROM paymentnoteitem a " +
			" join buinfo b on a.DOCBUID=b.buid " +
			" join sheettype c on a.DOCTYPe=c.sheettype " +
			" join shop d on (a.sysshopid=d.sysshopid) " +
			" join paytype e on e.paytypeid=a.PAYTYPEID " +
			" where a.SheetID=? ";
			tt = new String[]{"区域","单据类型","单据编码","业务发生日期","门店",
		       		"结算方式","金额","物流模式","备注"};
			book.addSheet(conn,sqlBody ,new Object[]{sheetid},"结算单明细",tt);
			
			String sqlCharge1 = "SELECT a.noteno,b.buname,c.shopname, d.chargename, a.chargeamt, TL_GetChargeMemo(a.chargecodeid,a.remark) remark" +
			" from paymentnoteCharge a " +
			" join buinfo b on a.buid=b.buid " +
			" join shop c on (a.sysshopid=c.sysshopid) " +
			" join chargecode d on (a.chargecodeid=d.chargecodeid)" +
			" where a.sheetid=? and a.InvoiceMode=? ";
			
			tt = new String[]{"扣项单号","所属区域","门店","扣项名称","金额","备注"};
			book.addSheet(conn,sqlCharge1 ,new Object[]{sheetid,1},"扣项一(票扣明细)",tt);
			book.addSheet(conn,sqlCharge1 ,new Object[]{sheetid,0},"扣项二(非票扣明细)",tt);
			
			String sqlInvoice = " select serialid,b.invoicetypename,invoicecode,invoiceno,invoicedate,goodsdesc,taxrate,taxableamt,taxamt,totalamt from paymentnoteInvoice a,invoicetype b where a.INVOICETYPEID=b.invoicetypeid and SheetID=? ";
			tt = new String[]{"序号","发票类型","发票代码","发票号码","开票日期","税率",
		       		"价额","税额","价税合计","备注"};
			book.addSheet(conn,sqlInvoice ,new Object[]{sheetid},"发票明细",tt);
			
			book.write();
			fout.close();			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * 开票申请
	 * @return
	 */
	public Element invoiceAsk(){
		String sheetid = getParamNotNull("sheetid");
		Element res = new Element("result");
		res.addContent("OK");
		//更新 paymentnotevenderask 表
		try {
			conn.setAutoCommit(false);
		
			String sql = "select reqflag from paymentnotevenderask where sheetid=?";
			List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid );
			int row = 0;
			if(list.size()==0){
				row = SqlUtil.executePS(conn, "insert into paymentnotevenderask(sheetid,reqflag,reqdate) values(?,1,trunc(sysdate))", new Object[]{sheetid});
			}else{
				//仅当状态由0变为1时才写传单请求
				row = SqlUtil.executePS(conn, "update paymentnotevenderask set reqflag=1 , reqdate=trunc(sysdate) where sheetid=? and reqflag=0", new Object[]{sheetid});
			}
			//写传单请求 当更新或插入有返回行数时才触发传单
			if(row>0){
				String proc = "fi_paymentnotevenderask('"+sheetid+"')";
				DXSHelper.send2DXSTask(conn, DXSHelper.SEE_NODE_NAME, "pnvenderask", "sheetid", sheetid, "I", "", "", "", proc,"开票申请");
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 收据打印
	 * @return
	 */
	public Element setSJPrint(){
		String sheetid = this.getParamNotNull("sheetid");
		Element res = new Element("result");
		res.addContent("OK");
		//更新 paymentnotevenderask 表
		String sql = "select sjflag from paymentnotevenderask where sheetid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid );
		if(list.size()==0){
			SqlUtil.executePS(conn, "insert into paymentnotevenderask(sheetid,sjflag,sjprintdate) values(?,1,trunc(sysdate))", new Object[]{sheetid});
		}else{
			SqlUtil.executePS(conn, "update paymentnotevenderask set sjflag=1 , sjprintdate=trunc(sysdate) where sheetid=?", new Object[]{sheetid});
		}
		return res;
	}
}
