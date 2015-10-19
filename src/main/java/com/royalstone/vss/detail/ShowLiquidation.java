package com.royalstone.vss.detail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.excel.Workbook;

/**
 * 此模块用于显示对帐申请单明细
 * @author baibai
 * @date 2006-11-9
 */
public class ShowLiquidation 
{
	/**
	 * 构造函数
	 * @param conn
	 * @param sheetid
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public ShowLiquidation( Connection conn, String sheetid ) throws SQLException, InvalidDataException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		this.conn = conn;
		this.sheetid = sheetid;
	}
	
    
    /**
     * 返回XML格式对帐申单
     * @param sheetid
     * @return
     * @throws SQLException
     * @throws InvalidDataException
     * @throws IOException 
     */
    public Element toElement() throws SQLException, InvalidDataException, IOException
    {
        Element elm_sheet = null;
        elm_sheet = new Element( "sheet" );
        elm_sheet.setAttribute( "title", title );
        elm_sheet.setAttribute( "sheetid", sheetid );
        
        elm_sheet.addContent( this.getHead(  ) );
        elm_sheet.addContent( this.getBody(  ) );

        return elm_sheet;
    }
    

	/**
	 * 返回对帐申请表头信息
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	private Element getHead() throws SQLException, InvalidDataException, IOException
	{
			String sql_head =  " SELECT lq.sheetid, lq.venderid, v.vendername,"
			+ " lq.bookno, b.bookname, b.booklogofname,lq.touchtime, "
			+ " lq.flag, lq.note, lq.editor, lq.editdate,b.booktitle,b.booktypeid "
			+ " FROM liquidation lq "
			+ " JOIN vender v ON (lq.venderid=v.venderid) "
			+ " JOIN book b ON (lq.bookno=b.bookno)  "
			+ " WHERE lq.sheetid = ? " ;

		PreparedStatement pstmt = conn.prepareStatement( sql_head );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_head = adapter.getRowSetElement( "head", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "Sheet not found: " + sheetid );
		elm_head.setAttribute( "rows", ""+rows );
		elm_head.setAttribute( "name", "purchase" );
		elm_head.setAttribute( "title", title );
		
		rs.close();
		pstmt.close();
		return elm_head;
	}
	
	/**
	 * 返回对帐申请表体信息
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	private Element getBody() throws SQLException, InvalidDataException, IOException
	{
		String sql_body = " SELECT li.sheetid, "
			+ " li.noteno, s.name, li.notevalue "
			+ "FROM liquidationitem li "
			+ " JOIN SerialNumber s ON ( li.notetype=s.SerialID ) "
			+ " WHERE li.sheetid = ?" ;
		
		PreparedStatement pstmt = conn.prepareStatement( sql_body );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_body = adapter.getRowSetElement( "body", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "This sheet does not have detail info: " + sheetid );
		elm_body.setAttribute( "rows", ""+rows );
		elm_body.setAttribute( "name", "purchaseitem" );
		rs.close();
		pstmt.close();
		return elm_body;
	}
	
    /**
     * 返回对帐申请表体excel格式
     * @param sheetid
     * @return
     * @throws SQLException
     * @throws InvalidDataException
     */
    public Workbook getWorkbook( String venderid ) throws SQLException, InvalidDataException
    {
        Workbook relBook = new Workbook();
        String[] title = { "单据号","单据类型","金额" };
        String sql = "select li.noteno,s.name,li.notevalue from liquidationitem li "
            + " JOIN SerialNumber s ON ( li.notetype=s.SerialID ) "
            + " JOIN liquidation l ON ( l.sheetid = li.sheetid ) "
            + " WHERE li.sheetid =? AND l.venderid = ?"; 
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString( 1, sheetid );
        pstmt.setString( 2, venderid );
        ResultSet rs = pstmt.executeQuery();
        relBook.addSheet(rs, "liquidation_list", title);
        rs.close();
        pstmt.close();
        return relBook;
    }
 
    
    /**
     * 对账申请明细
     * @param file
     * @return
     * @throws SQLException
     * @throws InvalidDataException
     * @throws IOException
     */
    public int cookExcelFile(File file) throws SQLException, InvalidDataException, IOException
    {
        String[] title = { "单据号","单据类型","金额" };
        String sql = "select li.noteno,s.name,li.notevalue from liquidationitem li "
            + " JOIN SerialNumber s ON ( li.notetype=s.SerialID ) "
            + " JOIN liquidation l ON ( l.sheetid = li.sheetid ) "
            + " WHERE li.sheetid =? "; 
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString( 1, sheetid );
        ResultSet rs = pstmt.executeQuery();
        SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "对账申请" );
        rs.close();
        pstmt.close();
        return rows;
    }
    /**
     * 得到对帐申请单的vendrid ，主要用于权限验证
     * @param sheetid_li
     * @return
     * @throws SQLException
     */
    public String getVenderId( ) throws SQLException
    {
        String rel_venderid = "";
        String sql = " SELECT venderid FROM liquidation WHERE sheetid=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, sheetid);
        ResultSet rs = pstmt.executeQuery();
        if ( rs.next() ) rel_venderid = rs.getString(1);
        
        rs.close();
        pstmt.close();
        return rel_venderid;
    }
    final private String title = "对帐申请单";
	private String sheetid = "";
	final private Connection conn;
}
