package com.royalstone.vss.noteboard;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.common.Sheetid;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于支持数据群发功能.
 * @author baibai
 * date:2006-10-23
 * 2009-07-27：增加到20列
 */
public class VenderMessageUpload 
{

	/**
	 * 构造函数
	 * @param conn
	 * @param token
	 * @param parms
	 */
	public VenderMessageUpload(Connection conn, Token token, Map<?, ?> parms)
	{
		this.conn = conn;
		this.token = token;
		this.parms = parms;
	}
	
	/**
	 * 保存数据
	 * @throws InvalidDataException 
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	
	public void save( String content,Element elm_message ) throws SQLException, InvalidDataException, UnsupportedEncodingException
	{
		this.content = content;
		this.elm_message = elm_message;
		String sheetid = Sheetid.getSheetid( conn, 7004, "" );
		insertHead( sheetid );
		insertBody( sheetid );
	}

	/**
	 * 返回指定的 venderid && msgid 所对应数据生成的 Workbook
	 * @param msgid
	 * @param venderid
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws NamingException
	 * @throws IOException 
	 */
	public int fetchRows4Vender(String msgid, String venderid,File file) throws InvalidDataException, SQLException, NamingException, IOException
	{
      
		String sql = "SELECT field01 , field02 , field03 , field04 , field05 , field06 , field07 , field08 , field09 , field10, " +
				" field11 , field12 , field13 , field14 , field15 , field16 , field17 , field18 , field19 , field20 "+
				" FROM vendermsgitem WHERE venderid=? AND msgid=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString( 1, venderid);
		pstmt.setString( 2, msgid);
		ResultSet rs = pstmt.executeQuery();
		String[] title = getMesTitle(msgid);
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "群发消息" );
		rs.close();
		pstmt.close();

		/**
		 * 写日志
		 */
		addLog( msgid, token ,"DOWNLOAD");
		return rows;
	}
	
	/**
	 * 返回指定的标识（msgid）所对应的数据
	 * @param msgid
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws IOException 
	 */
	public int fetchRows4Sender(String msgid, File file) throws InvalidDataException, SQLException, IOException
	{
		String[] title = {""};
		String sql = "SELECT venderid, field01 , field02 , field03 , field04 , field05 , field06 , field07 , field08 , field09 , field10," +
				" field11 , field12 , field13 , field14 , field15 , field16 , field17 , field18 , field19 , field20 " +
				" FROM vendermsgitem WHERE msgid=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString( 1, msgid);
		ResultSet rs = pstmt.executeQuery();
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "群发消息" );
		rs.close();
		pstmt.close();

		return rows;
	}
    
    /**
     * 返回消息标题列
     * @param msgid
     * @return
     * @throws SQLException
     */
    public String[] getMesTitle(String msgid) throws SQLException
    {
        String[] title = {"信息段1", "信息段2", "信息段3", "信息段4", "信息段5", "信息段6", "信息段7", "信息段8", "信息段9", "信息段10","","","","","","","","","",""};
        
        String sql_title = " SELECT field01 , field02 , field03 , field04 , field05 , field06 , field07 , field08 , field09 , field10,field11 , field12 , field13 , field14 , field15 , field16 , field17 , field18 , field19 , field20 " +
                " FROM vendermsgitem WHERE msgid=? AND venderid='title' ";
        PreparedStatement pstmt = conn.prepareStatement(sql_title);
        pstmt.setString(1, msgid);
        ResultSet rs = pstmt.executeQuery();
        if ( rs.next() )
        {
            for(int i=0; i<title.length; i++ )
            {
                String s = rs.getString(i+1);         
                title[i] = ( s == null ) ? "" : SqlUtil.fromLocal(s);
            }
        }
        rs.close();
        pstmt.close();
        return title;
    }

	/**
	 * 返回供应商群消息列表
	 * @param venderid
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws NamingException
	 * @throws IOException 
	 */
	public Element getMsgCatalogue(String venderid) throws SQLException, InvalidDataException, NamingException, IOException
	{


		Element elm_rel = new Element("list");
		String sql = " SELECT DISTINCT vm.msgid , vm.title , vm.editor , vm.editdate , vm.expiredate,vm.content " +
				" FROM vendermsg vm " +
				" JOIN  vendermsgitem vmi ON (vmi.msgid = vm.msgid) " +
				" WHERE vmi.venderid=? AND trunc(vm.expiredate) >= trunc(sysdate) " ;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString( 1, venderid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_rel = adapter.getRowSetElement( "catalogue", "row" );
		int rows = adapter.rows();
		elm_rel.setAttribute( "rows", "" + rows );
		elm_rel.setAttribute( "sheetname", "vendermsglist" );
		
		rs.close();
		pstmt.close();
		
		
		return elm_rel;
	}
	

	/**
	 * 插入数据到表头vendermsg
	 * @param sheetid
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws UnsupportedEncodingException 
	 */
	private void insertHead(  String sheetid ) throws SQLException, InvalidDataException, UnsupportedEncodingException
	{
		String title, expiredate;
		
		String[] ss = (String[])parms.get("title");
		if( ss ==null || ss.length == 0)
			throw new InvalidDataException("request for parms's title is null");
		title = new String(ss[0].getBytes("ISO8859-1"),"UTF-8");		
		ss = (String[])parms.get("expiredate");
		if( ss ==null || ss.length == 0)
			throw new InvalidDataException("request for parms's expiredate is null");
		expiredate = ss[0];
		
		String sql = "INSERT INTO vendermsg (msgid , title , editor , content,expiredate ) " +
				" VALUES (?,?,?,?,"+ValueAdapter.std2mdy(expiredate)+")";
		
		SqlUtil.executePS(conn, sql, sheetid,title,token.loginid,content);
	}
	

	/**
	 * 插入表体到vendermsgitem
	 * @param sheetid
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private void insertBody( String sheetid ) throws SQLException, InvalidDataException
	{
	    List lst = elm_message.getChildren("row");	
		String sql = "INSERT INTO vendermsgitem(seqno,msgid,venderid,field01 , field02 , field03 , field04 , field05 , field06 , field07 , field08 , field09 , field10,field11 , field12 , field13 , field14 , field15 , field16 , field17 , field18 , field19 , field20) " +
					" VALUES (vendermsgitem_id.nextval,? , ? , ? , ? , ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement pstmt = this.conn.prepareStatement(sql);

		for (int i = 0; i < lst.size(); i++) {
			Element elm = (Element) lst.get(i);
			String venderid = elm.getChildText("venderid");
			pstmt.setString(1, sheetid);
			pstmt.setString(2,SqlUtil.toLocal(venderid));
			for(int j=1; j<21; j++){
				String field_value = elm.getChildText("field"+j);
				field_value = ( field_value==null )? "" : SqlUtil.toLocal( field_value.trim() );
				pstmt.setString(j+2 , field_value);
			}
			pstmt.executeUpdate();
		}
		pstmt.close();
	}

	/**
	 * 返回发送消息列表
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element getSendsCatalogue() throws SQLException, IOException{
		//统计读取人数的表
		Element elm_rel = new Element("list");
		String sql = " SELECT vm.msgid , vm.title  , vm.editor, " +
				" vm.editdate, vm.expiredate, tmp.call_number,vm.content " +
				" FROM vendermsg vm " +
				" LEFT JOIN (SELECT msgid, COUNT( DISTINCT venderid ) call_number " +
				" FROM vendermsg_log " +
				" GROUP BY msgid) tmp ON ( tmp.msgid = vm.msgid ) " ;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_rel = adapter.getRowSetElement( "catalogue", "row" );
		int rows = adapter.rows();
		elm_rel.setAttribute( "rows", "" + rows );
		elm_rel.setAttribute( "sheetname", "sendslist" );
		
		rs.close();
		pstmt.close();

		return elm_rel;
	}

	
	/**
	 * 删除消息
	 * @param msgid
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws NamingException 
	 */
	public void delMessage(String msgid) throws SQLException, InvalidDataException, NamingException{
		String sql_head = "DELETE vendermsg WHERE msgid=?";
		String sql_body = "DELETE vendermsgitem WHERE msgid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql_head);
		PreparedStatement pstmt2 = conn.prepareStatement(sql_body);
		pstmt.setString( 1, msgid );
		pstmt2.setString( 1, msgid );
		try{
			conn.setAutoCommit(false);
			pstmt.executeUpdate();
			pstmt2.executeUpdate();
			/**
			 * 写日志
			 */
			addLog(msgid, token, "DELETE");
			conn.commit();
		}catch (Exception e) {
			conn.rollback();
			/**
			 * 写日志
			 */
			addLog(msgid, token, "FAIL_DEL");
		}finally{
			conn.setAutoCommit(true);
			pstmt.close();
			pstmt2.close();
		}
		
	}
	
	
	
	/**
	 * 群消息下载时记录
	 * @param conn
	 * @param msgid
	 * @param token
	 * @throws SQLException 
	 * @throws NamingException 
	 * @throws InvalidDataException 
	 */
	private void addLog( String msgid, Token token,String operation ) throws NamingException, SQLException, InvalidDataException
	{
		String operator = token.getBusinessid();
    	String venderid = operator;
    	if( token.isVender) {
        	venderid = token.getBusinessid();
    	}
    	
		String sql_ins = "INSERT INTO vendermsg_log ( logid,msgid, venderid, operator, operation ) " 
			+ " VALUES ( vendermsg_log_id.nextval,?, ?, ?, ? ) ";
		
		PreparedStatement pstmt = conn.prepareStatement( sql_ins );
		pstmt.setString( 1, msgid );
		pstmt.setString( 2, venderid );
		pstmt.setString( 3, operator );
		pstmt.setString( 4, operation );
		pstmt.execute();
		pstmt.close();
	}
	
	/**
	 * 将日志导出
	 * @param conn
	 * @param msgid
	 * @return excel支持的xml格式
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public int getLogBook( String msgid,File file ) throws SQLException, InvalidDataException, IOException
	{
		String[] title = { "消息号","标题","下载日期","供应商号","供应商名称" };
			
		String sql_sel =
		" SELECT t.msgid, m.title, l.logdate, " +
		" l.venderid, v.vendername " +
		" FROM (SELECT msgid, venderid, MIN(logid) logid " +
		" FROM vendermsg_log  " +
		" WHERE msgid = ? " +
		" GROUP BY msgid, venderid) t " +
		" JOIN vendermsg_log l ON ( l.logid = t.logid ) " +
		" JOIN vendermsg m ON ( m.msgid = t.msgid )" +
		" JOIN vender v ON ( v.venderid = t.venderid ) " +
		" ORDER BY t.logid " ;
		
		PreparedStatement pstmt_sel = conn.prepareStatement( sql_sel );
		pstmt_sel.setString(1, msgid);
		ResultSet rs = pstmt_sel.executeQuery();
		
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "群消息日志" );
		
		rs.close();
		pstmt_sel.close();
		
		return  rows;
	}
	
	final private Connection conn;
	final private Token token;
	
	private Map parms;
	private Element elm_message = null;
	private String content = null;
}
