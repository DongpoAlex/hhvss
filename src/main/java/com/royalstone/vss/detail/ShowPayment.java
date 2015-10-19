package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.fiscal.FiscalValue;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于查询付款单/付款申请单的内容, 包括单据头, 已经选业务单据, 扣项单, 发票等信息.
 * sheetid 必须是一个已经存在的单据, 如果指定的单据号在数据库中找不到对应记录, 则抛出例外.
 * @author liuwendong
 * @modefication baijian
 */
public class ShowPayment 
{

	final private Token	token;
	public ShowPayment( Connection conn, Token token,String sheetid ) throws SQLException 
	{
		this.conn = conn;
		this.sheetid = sheetid;
		this.flag = getFlag();
		this.token = token;

		/**
		 * 根据单据状态确定存放数据的表名
		 */
		if( this.flag >= 6 ) {
			this.tab_head = "paymentnote";
			this.tab_item = "paymentnoteitem";
			this.tab_invoice = "paymentnotedtl";
			this.tab_chargesum = "chargesum";
		} else {
			this.tab_head = "paymentnote0";
			this.tab_item = "paymentnoteitem0";
			this.tab_invoice = "paymentnotedtl0";
			this.tab_chargesum = "chargesum0";
		}
	}
	

	/**
	 * 此方法用于查询付款单的头部信息
	 * NOTE: real_pay: 表示实际应支付金额( 已扣除"补税差应付调整金额" 及 "电汇费" )
	 * @return	XML element.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public Element getHead() throws SQLException, IOException
	{
		double[] res = getSheetid(token.site.getSid(),sheetid);
		double amt17 = res[0];
		double amt13 = res[1];
		double pamt17 = res[2];
		double pamt13 = res[3];
		double pamt0 = res[4];
		
		String sql = " SELECT "
			+" p.sheetid,  b.bookname, b.booktitle,b.booktypeid,b.booklogofname,"
			+" p.venderid, v.vendername, "
			+" vb.bankname, vb.accno bankaccno, (payamt-adjpayamt-financialfee-suspayamt) chequeamt,"
			+" paymode.paymodename paymodename, p.payableamt, suspayamt,"
			+" p.chargeamt, p.planpaydate,p.TaxAmt17,p.TaxAmt13, "
			+" "+pamt17+" invtotalamt17,"+pamt13+" invtotalamt13,"+pamt0+" invtotalamt0,p.payamt, p.note, "
			+" p.adjpayamt, p.financialfee, p.editor, p.editdate, '"+ SqlUtil.toLocal(PayamtToChinese()) +"' as real_pay "
			+" FROM "+ this.tab_head +" p "
			+" JOIN book b ON (b.bookno=p.bookno) " 
			+" LEFT JOIN paymode ON (p.paymode=paymode.paymodeid) "
			+" JOIN vender v ON (v.venderid=p.venderid) " 
			+" LEFT JOIN vender_bank vb ON (vb.venderid=p.venderid) " 
			+" WHERE sheetid = ? ";

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_sheethead = adapter.getRowSetElement( "head", "rows" );
		rs.close();
		pstmt.close();
		return elm_sheethead;
	}
	
	public double[] getSheetid( int sid, String sheetid )throws SQLException
	{
        String sql             	= "call sp_gettaxamt(?, ?, ?,?,?,?,?)";
        double[] res = new double[5];
        
        CallableStatement psc = conn.prepareCall(sql);
        psc.setInt(1, sid );
        psc.setString(2, sheetid );
        psc.registerOutParameter(3, Types.DOUBLE);
        psc.registerOutParameter(4, Types.DOUBLE);
        psc.registerOutParameter(5, Types.DOUBLE);
        psc.registerOutParameter(6, Types.DOUBLE);
        psc.registerOutParameter(7, Types.DOUBLE);
        psc.execute();
        res[0] = psc.getDouble(3);
        res[1] = psc.getDouble(4);
        res[2] = psc.getDouble(5);
        res[3] = psc.getDouble(6);
        res[4] = psc.getDouble(7);
        psc.close();
        return res;
    }

	/**
	 * 此方法用于查询付款单内所选的业务单据清单
	 * @return	XML element.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public Element getSheetset() throws SQLException, IOException
	{
		String sql_sheet =" SELECT "  
			+" p.sheetid, p.docno, p.doctype, sn.name doctypename, "
			+" p.shopid, s.shopname, "
			+" name4code(p.logisticsid,'logistics') as logisticsid, "
			+" p.noteremark, p.saleamt,p.paydocamt, p.paytaxamt17, p.paytaxamt13, "
			+" p.docamt, p.docamt17, p.taxamt13, " +
			" p.docdate,p.majorid,p.paytypeid,pt.paytypename "
			+" FROM "+ this.tab_item +" p "
			+" JOIN  shop s on ( s.shopid=p.shopid ) "
			+" JOIN  serialnumber sn on ( sn.serialid = p.doctype  and p.doctype<>5201 and p.doctype<>5258) " 
			+" left join paytype pt on (pt.paytypeid=p.paytypeid) " 
			+" WHERE p.sheetid= ? order by p.shopid";
		
//		System.out.println(sql_sheet);
		PreparedStatement pstmt = conn.prepareStatement( sql_sheet );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_charge = adapter.getRowSetElement( "unpaidsheet_list", "rows" );
		rs.close();
		pstmt.close();
		return new Element("sheetset").addContent(elm_charge);
	}
	
	
	
	/**
	 * 此方法用于查询付款单内所选的业务单据清单，按门店分组
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element getSheetsetGroupByShop() throws SQLException, IOException{
		String sql4unpaidsheet = " SELECT "  
			+" p.sheetid, p.docno, p.doctype, sn.name doctypename, "
			+" p.shopid, s.shopname, "
			+" name4code(p.logisticsid,'logistics') as logisticsid, "
			+" p.noteremark, p.saleamt,p.paydocamt, p.paytaxamt17, p.paytaxamt13, (p.paydocamt+p.paytaxamt17+p.paytaxamt13) total_payamt, "
			+" p.docamt, p.docamt17, p.taxamt13, " +
			" p.docdate,p.majorid,p.paytypeid,pt.paytypename "
			+" FROM "+ this.tab_item +" p "
			+" JOIN  shop s on ( s.shopid=p.shopid ) "
			+" JOIN  serialnumber sn on ( sn.serialid = p.doctype  and p.doctype<>5201 and p.doctype<>5258 and p.doctype<>5205 ) "
			+" left join paytype pt on (pt.paytypeid=p.paytypeid) " 
			+" WHERE p.sheetid= ? order by p.shopid";
		
		String sql4csalecost = "select sh.shopid,sh.shopname,s.name doctypename,to_char(p.docdate,'YYYYMM') docdate,sum(p.saleamt) saleamt,sum(p.paydocamt+p.paytaxamt17+p.paytaxamt13) total_payamt "
			+ "from "+ this.tab_item +" p join shop sh on(p.shopid=sh.shopid) "
			+ "join serialnumber s on(p.doctype=s.serialid) "
			+ "where p.sheetid= ? and p.doctype=5205 " +
					" group by sh.shopid,sh.shopname,s.name,to_char(p.docdate,'YYYYMM') order by sh.shopid";
		
		PreparedStatement pstmt = conn.prepareStatement( sql4unpaidsheet );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elmSet = new Element( "sheetset" );
		Element elm_unpaid = adapter.getRowSetElement( "unpaidsheet_list", "rows" );
		elmSet.addContent( elm_unpaid );
		rs.close();
		pstmt.close();
		
		pstmt = conn.prepareStatement( sql4csalecost );
		pstmt.setString( 1, this.sheetid );
		rs = pstmt.executeQuery();
		XResultAdapter adapter2 = new XResultAdapter( rs );
		Element elm_salecost = adapter2.getRowSetElement( "salecost_list", "rows" );
		elmSet.addContent( elm_salecost );
		rs.close();
		pstmt.close();
		
		
		return elmSet;
	}
	
	
	public Element getChargeGroupByShop(int with_tax) throws InvalidDataException, SQLException, IOException{
		
		String sql_charge = " SELECT  "
			+ " c.noteno docno, c.shopid, s.shopname, "
			+ " c.chargecodeid, cc.chargename, c.chargeamt,  "
			+ " name4code( c.invoicemode,'invoicemode' ) as invoicemode, c.noteremark  "
			+ " FROM "+this.tab_chargesum+" c "
			+ " JOIN shop s ON ( s.shopid = c.shopid ) "
			+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) " 
			+ " JOIN "+this.tab_item+" p ON (p.docno = c.noteno) "
			+ " WHERE  p.sheetid = ? " 
			+ " AND c.invoicemode =? " +
			" union " +
			" select '' as docno, i.shopid, s.shopname,   " +
			" i.chargecodeid, cc.chargename,sum(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt,  " +
			" '' as invoicemode,'' noteremark  " +
			" from "+this.tab_item+" i " +
			" JOIN shop s ON ( s.shopid = i.shopid ) " +
			" JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) " +
			" WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode=? " +
					" group by i.shopid,s.shopname,i.chargecodeid, cc.chargename ";
//		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement( sql_charge );
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		pstmt.setString(3, this.sheetid);
		pstmt.setInt(4, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		String sheetName = (with_tax==1)?"chargesum_with_tax":"chargesum_without_tax";
		Element elm_charge = adapter.getRowSetElement( sheetName, "rows" );
		rs.close();
		pstmt.close();

		return elm_charge;
		
	}
	
public Element getChargeGroupByCharge(int with_tax) throws InvalidDataException, SQLException, IOException{
		
		String sql_charge = " SELECT  "
			+ " c.noteno docno, c.shopid, s.shopname, "
			+ " c.chargecodeid, cc.chargename, c.chargeamt,  "
			+ " name4code( c.invoicemode,'invoicemode' ) as invoicemode, c.noteremark  "
			+ " FROM "+this.tab_chargesum+" c "
			+ " JOIN shop s ON ( s.shopid = c.shopid ) "
			+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) " 
			+ " JOIN "+this.tab_item+" p ON (p.docno = c.noteno) "
			+ " WHERE  p.sheetid = ? " 
			+ " AND c.invoicemode =? " +
			" union " +
			" select '' as docno, '' as shopid, '' as shopname,   " +
			" i.chargecodeid, cc.chargename,sum(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt,  " +
			" '' as invoicemode,'' noteremark  " +
			" from "+this.tab_item+" i " +
			" JOIN shop s ON ( s.shopid = i.shopid ) " +
			" JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) " +
			" WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode=? " +
					" group by i.chargecodeid, cc.chargename ";
//		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement( sql_charge );
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		pstmt.setString(3, this.sheetid);
		pstmt.setInt(4, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		String sheetName = (with_tax==1)?"chargesum_with_tax":"chargesum_without_tax";
		Element elm_charge = adapter.getRowSetElement( sheetName, "rows" );
		rs.close();
		pstmt.close();

		return elm_charge;
		
	}

	/**
	 * 此方法用于查询付款单内的扣项信息. 
	 * 对于状态为"审定"及以后的付款单, 包括固定扣项; 对于状态为"审定"之前的付款单, 不包括固定扣项;
	 * @param	with_tax	主调模块用此参数表达待查询的扣项是否含税. 
	 * 不含税: {0}
	 * 含税: {1}
	 * 含税+不含税: {0,1}
	 * NOTE: 对参数值的合理性, 本方法不作检查.
	 * @return	XML element.
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 * @throws IOException 
	 */
	public Element getChargesum( int with_tax) throws SQLException, InvalidDataException, IOException
	{
		String sql_charge = " SELECT  "
			+ " c.noteno docno, c.shopid, s.shopname, "
			+ " c.chargecodeid, cc.chargename, c.chargeamt, c.ksheetid, "
			+ " name4code( c.invoicemode,'invoicemode' ) as invoicemode, c.noteremark  "
			+ " FROM "+this.tab_chargesum+" c "
			+ " JOIN shop s ON ( s.shopid = c.shopid ) "
			+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) " 
			+ " JOIN "+this.tab_item+" p ON (p.docno = c.noteno) "
			+ " WHERE  p.sheetid = ? " 
			+ " AND  c.invoicemode = ? " +
			" union " +
			" select i.docno, i.shopid, s.shopname, " +
			" i.chargecodeid, cc.chargename,(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt, '' ksheetid, " +
			" name4code( i.invoicemode,'invoicemode' ) as invoicemode,i.noteremark " +
			" from "+this.tab_item+" i " +
			" JOIN shop s ON ( s.shopid = i.shopid ) " +
			" JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) " +
			" WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode =? ";
//		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement( sql_charge );
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		pstmt.setString(3, this.sheetid);
		pstmt.setInt(4, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		String sheetName = (with_tax==1)?"chargesum_with_tax":"chargesum_without_tax";
		Element elm_charge = adapter.getRowSetElement( sheetName, "rows" );
		rs.close();
		pstmt.close();

		return elm_charge;
	}
	


	/**
	 * 查询没有付款确认前的付款单的固定扣项信息
	 * @param	with_tax	主调模块用此参数表达待查询的扣项是否含税. 
	 * 不含税: {0}
	 * 含税: {1}
	 * 含税+不含税: {0,1}
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public Element getChargeGenerated(int with_tax) throws SQLException, InvalidDataException, IOException
	{
		String sql_charge = " SELECT p.chargecodeid docno, "
			+ " p.shopid, s.shopname, "
			+ " p.chargecodeid, c.chargename, "
			+ " -p.chargeamt chargeamt, "
			+ " name4code( p.invoicemode,'invoicemode' ) as invoicemode, p.noteremark,name4code(p.settleflag,'settlemode') as settleflag "
			+ " FROM paymentnotecharge p "
			+ " JOIN chargecode c ON (p.chargecodeid=c.chargecodeid) "
			+ " JOIN shop s ON (s.shopid=p.shopid) "
			+ " WHERE  p.sheetid = ? "
			+ " AND p.invoicemode =? and p.settleflag=1 ";

		PreparedStatement pstmt = conn.prepareStatement( sql_charge );
		pstmt.setString( 1, this.sheetid );
		pstmt.setInt(2, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		String sheetName = (with_tax==1)?"chargegenerated_with_tax":"chargegenerated_without_tax";
		Element elm_charge = adapter.getRowSetElement( sheetName, "rows" );
		rs.close();
		pstmt.close();
		return elm_charge;
	}


	/**
	 * 此方法用于查询付款单的发票信息
	 * @return	XML element.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public Element getInvoice() throws SQLException, IOException
	{
		String sql_inovice =  " SELECT  "
			+ " d.sheetid, d.invoiceno, d.invoicetype, d.invoicedate, d.goodsdesc, "
			+ " d.taxrate, d.taxableamt, d.taxamt "
			+ " FROM " + this.tab_invoice + " d "
			+ " WHERE d.sheetid = ?  " ;

		PreparedStatement pstmt = conn.prepareStatement( sql_inovice );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_invoice = adapter.getRowSetElement( "paymentnotedtl", "rows" );
		rs.close();
		pstmt.close();
		return elm_invoice;
	}
	
	/**
	 * Flag 取值与含义: 1-制单 2-制单审核 3-发票确认 4-审批 5-审定 6-付款确认
	 * paymentnote0 表中, Flag 为1-5.
	 * paymentnote  表中, Flag 为6.
	 * @return flag
	 * @throws SQLException 
	 */
	private int getFlag( ) throws SQLException
	{
		int flag = -1;
		String sql0 =  " SELECT flag FROM paymentnote0 WHERE sheetid = ? " ;
		String sql1 =  " SELECT flag FROM paymentnote WHERE sheetid = ? " ;
		PreparedStatement pstmt = conn.prepareStatement( sql0 );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		if( rs.next() ) flag = rs.getInt(1);
		rs.close();
		pstmt.close();
		
		if( flag <0 ) {
			pstmt = conn.prepareStatement( sql1 );
			pstmt.setString( 1, this.sheetid );
			rs = pstmt.executeQuery();
			if( rs.next() ) flag = rs.getInt(1);
			rs.close();
			pstmt.close();
		}
		if( flag <0 ) throw new SQLException( "没有找到这个付款单: " + sheetid, "NOT_FOUND", 100 );

		return flag;
	}

	/**
	 * 从数据库查询付款单的应付金额, 并转换成大写中文.
	 * 付款单的存放随状态而定: paymentnote0/paymentnote.
	 * @return	字符串, 内容为金额的中文大写表示
	 * @throws SQLException
	 */
	private String PayamtToChinese() throws SQLException
	{		
		String payamt = "NAC";
		String sql_amt = " SELECT payamt, adjpayamt, financialfee, suspayamt FROM "+ this.tab_head +" WHERE sheetid = ? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_amt );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {	
			double get_payamt		= rs.getDouble(1);
			double get_adjpayamt	= rs.getDouble(2);
			double get_financialfee	= rs.getDouble(3);
			double get_suspayamt	= rs.getDouble(4);
			double cal_amt = get_payamt-get_adjpayamt-get_financialfee - get_suspayamt;			
			FiscalValue f = new FiscalValue( cal_amt );
			payamt = f.toChinese();			
		}			
		rs.close();
		pstmt.close();
		return payamt;
	}
	
	/**
	 * 此方法用于查询指定付款单的供应商编码. 
	 * 付款单状态可能为: 已经结算/待结算.
	 * @author baijian
	 * @param  sheetid	付款单号
	 * @return	供应商编码
	 * @throws SQLException
	 */
	public String getPaymentnoteVenderid( String sheetid ) throws SQLException
	{
		String venderid = "";
		String sql = " SELECT venderid FROM "+ tab_head + " WHERE sheetid=? " ;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		if ( rs.next() ) venderid = rs.getString(1);
		else throw new SQLException( "sheet NOT_FOUND:" + sheetid, "NOT_FOUND", 100 );
		
		return venderid ;
	}
	final private Connection conn;
	private String sheetid;
	private String tab_head;
	private String tab_item;
	private String tab_invoice;
	private String tab_chargesum;
	private int flag = -1;
	public void sjPrint(String sheetid) {  
		String sqlUp="update paymentnotevenderask set sjprintdate=sysdate,sjflag=1 where sheetid=?";
		
		String sqlIns="insert into paymentnotevenderask(sheetid,sjprintdate) values(?,sysdate)";
		
		//先更新，更新失败，直接插入
		
		int rows = SqlUtil.executePS(conn, sqlUp, new String[]{sheetid});
		
		if(rows==0){
			rows = SqlUtil.executePS(conn, sqlIns, new String[]{sheetid});
		}
		
	}
}