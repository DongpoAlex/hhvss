package com.royalstone.vss.liquidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import com.royalstone.common.Sheetid;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于发票提交和添加，修改和删除
 * @author baibai
 * @date 
 */
public class VenderInvoiceEditor 
{
	public VenderInvoiceEditor( Connection conn, Token token ) throws NamingException, SQLException, InvalidDataException
	{
		this.conn = conn;
		this.token = token;
		String[] arr_vender = token.getEnv( "venderid" );
		if( arr_vender == null || arr_vender.length == 0 
				|| arr_vender[0] == null || arr_vender[0].length() == 0 ) throw new InvalidDataException( "Env venderid is invalid!");
		
		this.venderid = arr_vender[0].trim();
	}
	
	/**
	 * 此方法用于对发票接收单作"提交"操作. VSS系统中的发票接收单一经提交即通过后台传递到FAS,不可以撤消.
	 * @param sheetid_payment	付款单据的单据号
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void confirm ( String sheetid_payment,String contacttel,String contact ) throws SQLException, InvalidDataException
	{
        
		/*
		 * 检查帐套冻结情况
		 * 
		 */
		{
			String sql = "select nvl(vps.payflag,0)  from paymentnote0 pn0 " +
					" left join venderpaystatus vps on (vps.venderid=pn0.venderid and vps.bookno = pn0.bookno) " +
					" where pn0.sheetid=?";
			PreparedStatement pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, sheetid_payment );
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				if(rs.getInt(1)==1){
					throw new InvalidDataException( "该公司单据冻结, 请联系采购！" );
				}
			}
			rs.close();
			pstmt.close();
		}
		
		
		/**
		 * 检查单据目前的状态
		 */
		{
				int flag = getFlag( sheetid_payment );
				if( flag != 0 ) throw new InvalidDataException( "该单据已经提交, 不能重复提交！" );

		}
		/**
		 * 判断发票表体信息是否为空，为空则拒绝提交
		 */
		{
			String sql = "select venderinvoice.sheetid from venderinvoice join venderinvoiceitem on venderinvoice.sheetid=venderinvoiceitem.sheetid where venderinvoice.refsheetid = ? ";
			PreparedStatement pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, sheetid_payment );
			ResultSet rs = pstmt.executeQuery();
			boolean b = rs.next();
			rs.close();
			pstmt.close();
			
			if(!b){
				throw new InvalidDataException("该发票没有发票明细，无法正常提交！");
			}
		}
		try{
			conn.setAutoCommit(false);
			String sql_upd = " UPDATE venderinvoice set flag = 2, checkdate = sysdate, checker = ?,contact=? ,contacttel=? WHERE refsheetid = ? ";
			PreparedStatement pstmt = conn.prepareStatement( sql_upd );
			pstmt.setString( 1,  token.getBusinessid() ) ;
			pstmt.setString( 2, SqlUtil.toLocal(contact) );
			pstmt.setString( 3, contacttel );
			pstmt.setString( 4, sheetid_payment );
			pstmt.executeUpdate();
			pstmt.close();
			
			//发DXS传单请求
			int sid = token.site.getSid();
			if(sid==3 || sid==4){
					String sql_sp = " call TL_FAS_VenderInvoice( ? ) ";
					PreparedStatement pstmt_sp = conn.prepareStatement( sql_sp ); 
					pstmt_sp.setString( 1, getSheetid(sheetid_payment));
					pstmt_sp.execute();
					pstmt_sp.close();
			}
			
			conn.commit();
		}catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
		}
		
		
	}
	
	/**
	 * 插入新的发票接收单表头
	 * @param sheetid		venderinvoice.sheetid
	 * @param sheetid_payment	paymentnote0.sheetid
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private void insertHead( String sheetid, String sheetid_payment ) throws SQLException, InvalidDataException 
	{
        
		String sql = " INSERT INTO venderinvoice  "
				+ " ( sheetid, refsheetid, venderid, editor ) "
				+ " VALUES  ( ?, ?, ?, ? ) ";
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.setString(2, sheetid_payment);
		pstmt.setString(3, venderid);
		pstmt.setString(4, token.getBusinessid());
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	/**
	 * 此方法查找与付款单相对应的发票接收单的单据号. 如果数据库中找不到对应的发票接收单,则返回值为null.
	 * @param sheetid_payment	付款单单号
	 * @return	发票接收单单号
	 * @throws SQLException 
	 */
	public String getSheetidByPayment( String sheetid_payment ) throws SQLException
	{
		String sheet_invoice = null;
		String sql = "SELECT sheetid FROM venderinvoice WHERE refsheetid=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, sheetid_payment);
        ResultSet rs = pstmt.executeQuery();
        if( rs.next() ) sheet_invoice = rs.getString(1);
        pstmt.close();
		
		return sheet_invoice;
	}
	/**
     * 此方法为发票录入做准备 提供发票id ，对发票的flag 和venderid 做检测
     * 返回 sheet_payment 对应的发票id ，如果没有对应的发票id，则新申请一个发票id返回 
     * @param sheetid_payment
     * @return
     * @throws SQLException
     * @throws InvalidDataException
	 */
	public String openSheet( String sheetid_payment ) throws SQLException, InvalidDataException
	{   
		String sheet_invoice = getSheetidByPayment( sheetid_payment );     

		if( sheet_invoice == null || sheet_invoice.length() < 1 ) {
			//没有找到相应的发票，则生成新的发票
			sheet_invoice = Sheetid.getSheetid(conn, 7002, "01");
			insertHead( sheet_invoice, sheetid_payment );
		}else{
	        /**
	         * 检查发票的flag 只能为0 才允许录入发票
	         */
	        int flag = getFlag( sheetid_payment );
	        if ( flag != 0 ) throw new InvalidDataException( "该发票信息已经提交，不能再次编辑了！" );
		}
		
		return sheet_invoice;
	}

    /**
     * 此方法根据付款单号，返回发票状态。
     * @param sheetid_payment
     * @return -1 代表，没有该发票
     * @throws SQLException
     */
    public int getFlag(String sheetid_payment) throws SQLException
    {
        int rel_flag = -1;
        
        String sql_s = "SELECT flag FROM venderinvoice WHERE refsheetid=? ";
        PreparedStatement pstmt = conn.prepareStatement(sql_s);
        pstmt.setString( 1 , sheetid_payment );
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) rel_flag = rs.getInt(1);
        
        pstmt.close();
        
        return rel_flag;
    }
    /**
     * 此方法根据付款单号，返回发票单。
     * @param sheetid_payment
     * @throws SQLException
     */
    public String getSheetid(String sheetid_payment) throws SQLException
    {
        String sheetid = "";
        
        String sql_s = "SELECT sheetid FROM venderinvoice WHERE refsheetid=? ";
        PreparedStatement pstmt = conn.prepareStatement(sql_s);
        pstmt.setString( 1 , sheetid_payment );
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) sheetid = rs.getString(1);
        
        pstmt.close();
        
        return sheetid;
    }
    /**
     * 此方法根据付款单号 返回该付款单的供应商id
     * @param sheetid_payment
     * @return
     * @throws SQLException
     */
    public String getPaymentVenderid(String sheetid_payment) throws SQLException
    {
        String rel_venderid = "";
        String sql_s = "SELECT venderid FROM paymentnote0 WHERE sheetid=? ";
        if(token.site.getSid()==11){
        	sql_s = "SELECT venderid FROM paymentsheet0 WHERE sheetid=? ";
        }
        PreparedStatement pstmt = conn.prepareStatement(sql_s);
        pstmt.setString( 1 , sheetid_payment );
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) rel_venderid = rs.getString(1).trim();
        
        pstmt.close();
        
        return rel_venderid;
    }
    
    /**
     * 此方法根据发票接收单号 返回该单的供应商id
     * @param sheetid_invoice
     * @return
     * @throws SQLException
     */
    public String getInvoiceVenderid(String sheetid_invoice) throws SQLException
    {
        String rel_venderid = "";
        
        String sql_s = "SELECT venderid FROM venderinvoice WHERE sheetid=? ";
        PreparedStatement pstmt = conn.prepareStatement(sql_s);
        pstmt.setString( 1 , sheetid_invoice );
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) rel_venderid = rs.getString(1).trim();
        
        pstmt.close();
        
        return rel_venderid;
    }
    
	/**
	 * 此方法用于向发票接收单添加明细记录.
	 * @param sheetid	venderinvice.sheetid
	 * @param request	servlet 参数, 用于解析发票信息.
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void addItem ( String sheetid, HttpServletRequest request ) throws SQLException, InvalidDataException
	{    
        
		String invoiceno = request.getParameter( "invoiceno" );
		String invoicetype = request.getParameter( "invoicetype" );
		String invoicedate = request.getParameter( "invoicedate" );
		String goodsdesc = request.getParameter( "goodsdesc" );
		String taxrate = request.getParameter( "taxrate" );
		String taxamt = request.getParameter( "taxamt" );
		String taxableamt = request.getParameter( "taxableamt" );
		
		goodsdesc = ( goodsdesc==null )? "" : SqlUtil.toLocal( goodsdesc ).trim();
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
		if( invoiceno == null || invoiceno.length() == 0 ) throw new InvalidDataException( "invoiceno is invalid!" );
		if( invoicetype == null || invoicetype.length() == 0 ) throw new InvalidDataException( "invoicetype is invalid!" );
		if( invoicedate == null || invoicedate.length() == 0 ) throw new InvalidDataException( "invoicedate is invalid!" );
		if( taxrate == null || taxrate.length() == 0 ) throw new InvalidDataException( "taxrate is invalid!" );
		if( taxamt == null || taxamt.length() == 0 ) throw new InvalidDataException( "taxamt is invalid!" );
		if( taxableamt == null || taxableamt.length() == 0 ) throw new InvalidDataException( "taxableamt is invalid!" );

		if( getDupRows( invoiceno, invoicetype ) >0 ) throw new InvalidDataException( "此发票已经录入:" + invoiceno );
		double rate = ValueAdapter.parseDouble( taxrate );
		double amt_tax = ValueAdapter.parseDouble( taxamt );
		double amt_taxable = ValueAdapter.parseDouble( taxableamt );
		
		String sql_ins = " INSERT INTO venderinvoiceitem " +
				" ( sheetid, seqno, invoiceno, invoicetype, invoicedate, goodsdesc,  taxableamt, taxrate, taxamt ) " +
				" VALUES ( ?,venderinvoiceitem_id.nextval, ?, ?, to_date(?,'YYYY-MM-DD'), ?, ?, ?, ? ) ";
		
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setString( 1, sheetid );
		pstmt.setString( 2, invoiceno );
		pstmt.setString( 3, invoicetype );
		pstmt.setString( 4, invoicedate );
		pstmt.setString( 5, goodsdesc );
		pstmt.setDouble( 6, amt_taxable );
		pstmt.setDouble( 7, rate );
		pstmt.setDouble( 8, amt_tax );
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	/**
	 * 此方法用于修改一条发票记录
	 * @param sheetid
	 * @param request
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public void replaceItem ( String sheetid, HttpServletRequest request ) throws InvalidDataException, SQLException
	{
          
        String invoiceno = request.getParameter( "invoiceno" );
        String invoicetype = request.getParameter( "invoicetype" );
        String invoicedate = request.getParameter( "invoicedate" );
        String goodsdesc = request.getParameter( "goodsdesc" );
        String taxrate = request.getParameter( "taxrate" );
        String taxamt = request.getParameter( "taxamt" );
        String taxableamt = request.getParameter( "taxableamt" );
        String sqno = request.getParameter("seqno");
        
        goodsdesc = ( goodsdesc==null )? "" : SqlUtil.toLocal( goodsdesc ).trim();
        
        if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
        if( invoiceno == null || invoiceno.length() == 0 ) throw new InvalidDataException( "invoiceno is invalid!" );
        if( invoicetype == null || invoicetype.length() == 0 ) throw new InvalidDataException( "invoicetype is invalid!" );
        if( invoicedate == null || invoicedate.length() == 0 ) throw new InvalidDataException( "invoicedate is invalid!" );
        if( taxrate == null || taxrate.length() == 0 ) throw new InvalidDataException( "taxrate is invalid!" );
        if( taxamt == null || taxamt.length() == 0 ) throw new InvalidDataException( "taxamt is invalid!" );
        if( taxableamt == null || taxableamt.length() == 0 ) throw new InvalidDataException( "taxableamt is invalid!" );
        if( sqno == null || sqno.length() == 0 ) throw new InvalidDataException( "sqno is invalid!" );
        
        double rate = ValueAdapter.parseDouble( taxrate );
        double amt_tax = ValueAdapter.parseDouble( taxamt );
        double amt_taxable = ValueAdapter.parseDouble( taxableamt );
        int iseqno = Integer.parseInt(sqno);
        
        String sql_update = " UPDATE venderinvoiceitem " +
                " SET invoicedate=to_date(?,'YYYY-MM-DD') , goodsdesc=? , taxrate=? , taxamt=? , taxableamt=? " +
                " WHERE sheetid=? AND seqno=?";
                
        
        PreparedStatement pstmt = conn.prepareStatement(sql_update);
        pstmt.setString( 1, invoicedate );
        pstmt.setString( 2, goodsdesc );
        pstmt.setDouble( 3, rate );
        pstmt.setDouble( 4, amt_tax );
        pstmt.setDouble( 5, amt_taxable );
        pstmt.setString( 6, sheetid );
        pstmt.setInt( 7 , iseqno );
        pstmt.executeUpdate();
        pstmt.close();
	}

	/**
	 * 此方法删除发票接收单的一条明细记录
	 * @param sheetid
	 * @param seqno
	 * @throws SQLException
	 * @throws InvalidDataException 
	 */
	public void deleteItem ( String sheetid, int seqno ) throws SQLException, InvalidDataException
	{
     
		String sql_del = " DELETE FROM venderinvoiceitem WHERE sheetid = ? AND seqno = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql_del);
		pstmt.setString( 1, sheetid );
		pstmt.setInt( 2, seqno );
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	/**
	 * 在发票接收单内查找发票号和发票类型相同的记录数
	 * @param invoiceno
	 * @param invoicetype
	 * @return
	 * @throws SQLException
	 */
	private int getDupRows ( String invoiceno, String invoicetype ) throws SQLException
	{
		int rows = 0;
		String sql_un = " SELECT count(*) FROM venderinvoiceitem i "
			+ " JOIN venderinvoice v ON (v.sheetid = i.sheetid) "
			+ " WHERE v.venderid=? AND i.invoiceno = ? AND i.invoicetype = ? ";
		PreparedStatement ps = conn.prepareStatement(sql_un);
		ps.setString( 1, venderid );
		ps.setString( 2, invoiceno );
		ps.setString( 3, invoicetype );
		ResultSet rs = ps.executeQuery();
		while (rs.next()) rows = rs.getInt(1) ;
		rs.close();
		ps.close();
		return rows;
	}
	

	
	final private Connection conn;
	final private Token token;
	final private String venderid;
}
