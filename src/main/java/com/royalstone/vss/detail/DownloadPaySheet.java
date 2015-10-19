package com.royalstone.vss.detail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.excel.Workbook;


/**
 * 此模块用于下载付款单/付款申请单. 
 * sheetid 必须是一个已经存在的付款单的单据号. 数据库可能存放在paymentnote0/paymentnote中, DownloadPayment会自动判断存储位置.
 * DownloadPayment 在构造函数中调用一系列私有的方法, 查询数据库, 并把相应的付款单内的信息添加到 Workbook对象中.
 * 后台程序通过Daemon输出Workbook对象, 即可完成下载功能.
 * 如果指定的单据号在数据库中找不到对应记录, 则抛出例外.
 * NOTE: 固定扣项的存放位置可能在以下几个表中: paymentnotecharge/paymentnoteitem/paymentnoteitem0. 
 * 输出文件中, 关于固定/临时扣项的分类可能不准确, 但票扣/非票扣属性是准确的.
 */

/**
 * 此模块用于下载付款单/付款申请单.
 * sheetid 必须是一个已经存在的单据, 如果指定的单据号在数据库中找不到对应记录, 则抛出例外.
 *
 */
public class DownloadPaySheet 
{

	/**
	 * Constructor 
	 * @param conn		数据库连接
	 * @param sheetid	单据号
	 * @throws SQLException
	 * @throws InvalidDataException
	 */

	public DownloadPaySheet( Connection conn, String sheetid,Token token ) throws SQLException, InvalidDataException 
	{
		this.conn = conn;
		this.sheetid = sheetid;
		this.token = token;
		try {
			this.flag = getFlag();
		} catch (SQLException e) {
			throw e;
		}

		/**
		 * 如果付款单状态为付款确认, 相应信息存放在paymentnote及其子表中, 否则, 存放在paymentnote0及相应子表中.
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
		
		/**
		 * 调用私有方法, 向Workbook对象中添加付款单信息.
		 */
		getHead();
		getSheetset();
		getChargesum(1);
		getChargesum(0);
		getChargeGenerated(1);
		getChargeGenerated(0);
		getInvoice();
	}
	
	
	/**
	 * 此方法用于查询付款单的头部信息
	 * @return	XML element.
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 */
	public void getHead() throws SQLException, InvalidDataException
	{
		String[] title = {"单据编号", "城市公司", "支付方式", "本期对帐金额", "计划付款日期", "17%税建议开票金额", "13%税建议开票金额","0%税建议开票金额"};
		double[] res = getSheetid(token.site.getSid(),sheetid);
		double amt17 = res[0];
		double amt13 = res[1];
		double pamt17 = res[2];
		double pamt13 = res[3];
		double pamt0 = res[4];
		
		String sql = " SELECT "
			+" p.sheetid,  b.bookname, "
			+" paymode.paymodename , "
			+" (p.payamt - p.chargeamt) as pay, p.planpaydate, "
			+pamt17+"  invtotalamt17, "+pamt13+" invtotalamt13,"+pamt0+" invtotalamt0 "
			+" FROM "+ this.tab_head +" p "
			+" JOIN book b ON (b.bookno=p.bookno) "
			+" JOIN paymode ON (p.paymode=paymode.paymodeid) "
			+" WHERE sheetid = ? ";

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();

		this.workbook.addSheet(rs, "表头信息", title);
		
		rs.close();
		pstmt.close();

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
	 * @throws InvalidDataException 
	 */
	private void getSheetset() throws SQLException, InvalidDataException
	{

		String[] title = { "业务单据号", "单据类型", "门店号", "门店名称", "对帐金额", "物流模式", "单据说明" };
				
		String sql_sheet =" SELECT "  
			+" p.docno, sn.name doctypename, "
			+" p.shopid, s.shopname, ( p.paydocamt + p.paytaxamt17 + p.paytaxamt13 ) AS pay, "
			+" name4code(p.logisticsid,'logistics') as logisticsid, "
			+" p.noteremark "
			+" FROM "+ this.tab_item +" p "
			+" JOIN  shop s on ( s.shopid=p.shopid ) "
			+" JOIN  serialnumber sn on ( sn.serialid = p.doctype and p.doctype<>5201  and p.doctype<>5258) "
			+" WHERE p.sheetid= ? ";
		
		PreparedStatement pstmt = conn.prepareStatement( sql_sheet );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		
		workbook.addSheet( rs, "对帐单据", title );
		

		rs.close();
		pstmt.close();

	}
	
	/**
	 * 此方法用于查询付款单内的扣项信息, 包括临时/固定扣项.
	 * @param	with_tax	主调模块用此参数表达待查询的扣项是否含税. 
	 * 不含税: {0}
	 * 含税: {1}
	 * 含税+不含税: {0,1}
	 * NOTE: 对参数值的合理性, 本方法不作检查.
	 * @return	XML element.
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 */
	private void getChargesum( int invoicemode ) throws SQLException, InvalidDataException
	{
		
		String[] title = {"扣项单号",  "门店编号",  "门店名称",  "扣项名称",  "扣项金额",  "扣项说明"  };
		String book_name = "";
		if ( invoicemode == 1 )
		{
			book_name = "票扣费用";
		}else{
			book_name = "非票扣费用";
		}
		
		String sql_charge = " SELECT  "
			+ " c.noteno docno, c.shopid, s.shopname, "
			+ " cc.chargename , c.chargeamt , "
			+ " c.noteremark  "
			+ " FROM "+this.tab_chargesum+" c "
			+ " JOIN shop s ON ( s.shopid = c.shopid ) "
			+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) " 
			+ " JOIN "+this.tab_item+" p ON (p.docno = c.noteno ) "
			+ " WHERE c.invoicemode = ? " 
			+ " AND p.sheetid = ?"+
			" union " +
			" select i.docno, i.shopid, s.shopname, " +
			" cc.chargename,(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt, " +
			" i.noteremark " +
			" from "+this.tab_item+" i " +
			" JOIN shop s ON ( s.shopid = i.shopid ) " +
			" JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) " +
			" WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode = ?";
		
		PreparedStatement pstmt = conn.prepareStatement( sql_charge );
		pstmt.setInt( 1, invoicemode );
		pstmt.setString(2, this.sheetid );
		pstmt.setString(3, this.sheetid );
		pstmt.setInt( 4, invoicemode );
		ResultSet rs = pstmt.executeQuery();
		
		this.workbook.addSheet(rs, book_name, title);
		
		rs.close();
		pstmt.close();

	}
		
	/**
	 * 此方法从数据库中查询付款单内所选的固定扣项,并添加到Workbook中.
	 * @param	with_tax	是否票扣扣项
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void getChargeGenerated(int with_tax) throws SQLException, InvalidDataException
	{
		String book_name= "";
		if ( with_tax == 1 ){
			 book_name = "固定扣项（票扣）";
		}else{
			 book_name = "固定扣项（非票扣）";
		}
		
		String[] title = { "扣项单号", "门店编号",  "门店名称",  "扣项名称",  "扣项金额",  "扣项说明"  };
		String sql_charge = " SELECT p.chargecodeid docno, "
			+ " p.shopid, s.shopname, c.chargename, "
			+ " p.chargeamt chargeamt, p.noteremark"
			+ " FROM paymentnotecharge p "
			+ " JOIN chargecode c ON (p.chargecodeid=c.chargecodeid) "
			+ " JOIN shop s ON (s.shopid=p.shopid) "
			+ " WHERE p.invoicemode = ?"
			+ " AND p.sheetid = ? and p.settleflag=1 ";	
		

		PreparedStatement pstmt = conn.prepareStatement( sql_charge );
		pstmt.setInt( 1, with_tax );
		pstmt.setString( 2, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		this.workbook.addSheet( rs, book_name, title );
		rs.close();
		pstmt.close();

	}
		
	/**
	 * 此方法用于查询付款单的发票信息, 并添加到Workbook中.
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 */
	private void getInvoice() throws SQLException, InvalidDataException
	{
		String[] title = { "发票号", "发票类型", "开票日期", "税率",  "税额",  "价额",  "发票说明" };
		String sql_inovice =  " SELECT  "
			+ " d.invoiceno, d.invoicetype, d.invoicedate, "
			+ " d.taxrate, d.taxamt, d.taxableamt, d.goodsdesc "
			+ " FROM " + this.tab_invoice + " d "
			+ " WHERE d.sheetid = ? " ;

		PreparedStatement pstmt = conn.prepareStatement( sql_inovice );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();

		this.workbook.addSheet( rs, "发票信息", title );
		
		rs.close();
		pstmt.close();
		
	}
	
	/**
	 * 此方法查询数据库, 返回单据的flag值.
	 * 由于单据存放位置不确定, 需要查询两个表: paymentnote0, paymentnote.
	 * Flag 取值与含义: 1-制单 2-制单审核 3-发票确认 4-审批 5-审定 6-付款确认
	 * paymentnote0 表中, Flag 为1-5.
	 * paymentnote  表中, Flag 为6.
	 * @return		付款单的flag 值
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
	 * 此方法用于查询指定付款单的供应商编码, 主要用于访问权限检查.
	 * 如果在数据库中没有找到指定的单据号,则返回长度为零的字串.
	 * @param sheetid	付款单单据号
	 * @return			供应商编码
	 * @throws SQLException
	 */
	public String getPaymentnoteVenderid( String sheetid ) throws SQLException
	{
		String rel = "";
		String sql = " SELECT venderid FROM "+ tab_head + " WHERE sheetid=? " ;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		if ( rs.next() ) rel = rs.getString(1);
		
		return rel ;
	}
	
	/**
	 * 此方法返回一个Workbook 对象, 
	 * 其中包含指定付款单内一系列信息: 单据头部; 入选的物流单据; 入选的扣项单(固定/临时/票扣项/非扣项); 供应商提供的发票信息.
	 * 用Excel打开后, 这几组信息显示为不同的datasheet.
	 * @return	a Workbook obj, including infomation in paymentnote.
	 */
	public Workbook getWorkbook()
	{
		return workbook;
	}
	
	final private Connection conn;
	final private String sheetid;
	final private Token	token;
	private String tab_head;
	private String tab_item;
	private String tab_invoice;
	private String tab_chargesum;
	private int flag = -1;
	private Workbook workbook = new Workbook();

}
