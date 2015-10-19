package com.royalstone.email;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½Ô±ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ê¼ï¿½
 * @author baibai
 *
 */
public class MailBrowser {
	public MailBrowser(Connection conn){
		this.conn = conn;
	}
	

	public Element scanMailList(String receiptor, int type, String minSendDate, String maxSendDate, String sender, String receiver, String minReceiveDate, String maxReceiveDate) throws SQLException {
		Element elm_list = new Element( "BrowseMail" );
			
		String sqlParms = "";
    		PreparedStatement pstmt = null;
    		if(minSendDate!=null && minSendDate.length()>0){
    			sqlParms += " and t.sendtime >"+ValueAdapter.std2mdy(minSendDate);
    		}
    		if(maxSendDate!=null && maxSendDate.length()>0){
    			sqlParms += " and t.sendtime <"+ValueAdapter.std2mdy(maxSendDate);
    		}
    		
    		if(sender!=null && sender.length()>0){
    			sqlParms += " and t.sender ="+ValueAdapter.toString4String(sender);
    		}
    		
    		if(receiver!=null && receiver.length()>0){
    			sqlParms += " and t.receiptor01 ="+ValueAdapter.toString4String(receiver);
    		}
    		
    		if(minReceiveDate!=null && minReceiveDate.length()>0){
    			sqlParms += " and (r.recetime) >="+ValueAdapter.std2mdy(minReceiveDate);
    		}
    		
    		if(maxReceiveDate!=null && maxReceiveDate.length()>0){
    			sqlParms += " and (r.recetime) <="+ValueAdapter.std2mdy(maxReceiveDate);
    		}
    		
    		if (type==RECE_MAIL){
    			//System.out.println(SQL_SEL_RECE_MAIL+sqlParms+" ORDER BY r.status,t.sendtime desc");
				pstmt= this.conn.prepareStatement( SQL_SEL_RECE_MAIL+sqlParms+" ORDER BY r.status,t.sendtime desc" );
				pstmt.setString(1, receiptor);
    		} else if(type==SEND_MAIL){
    			//System.out.println(SQL_SEL_SEND_MAIL+sqlParms+" ORDER BY t.sendtime desc");
				pstmt= this.conn.prepareStatement( SQL_SEL_SEND_MAIL+sqlParms+" ORDER BY t.sendtime desc" );
				pstmt.setString(1, receiptor);
    		}  else if(type==DRAFT_MAIL){
				pstmt= this.conn.prepareStatement( SQL_SEL_DRAFT_MAIL+sqlParms );
				pstmt.setString(1, receiptor);
    		} else if(type==RECY_MAIL) {
				pstmt= this.conn.prepareStatement( SQL_SEL_RECY_MAIL+sqlParms );
				pstmt.setString(1, receiptor);
				pstmt.setString(2, receiptor);
    		} else if(type==DEL_MAIL){
				pstmt= this.conn.prepareStatement( SQL_SEL_DEL_MAIL+sqlParms );
				pstmt.setString(1, receiptor);
				pstmt.setString(2, receiptor);
    		} else if(type==ALL_RECV_MAIL){
    			pstmt= this.conn.prepareStatement( SQL_ALL_RECY_MAIL+sqlParms );
				pstmt.setString(1, receiptor);
    		} else if(type==ALL_SEND_MAIL){
    			pstmt= this.conn.prepareStatement( SQL_ALL_SEND_MAIL+sqlParms );
				pstmt.setString(1, receiptor);
    		}else{
    			return elm_list;
    		}
    			
			ResultSet rs=pstmt.executeQuery();
			XResultAdapter adapter = new XResultAdapter( rs );
			elm_list = adapter.getRowSetElement( "browseMail", "mail" );
			
			pstmt.close();
			rs.close();
	    
    	return elm_list;
	}
	
	
	/**
	 * ï¿½ï¿½ï¿½ï¿½typeÈ¡ï¿½ï¿½ï¿½ï¿½Ó¦ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ê¼ï¿½mailidï¿½ï¿½ï¿½ï¿½
	 * @param receiptor
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public int[] scanMailId( String receiptor, int type ) throws SQLException{
		int[] arr_mid = null;
		PreparedStatement pstmt = null;
		if (type==RECE_MAIL){
			pstmt= this.conn.prepareStatement( SQL_SEL_RECE_MAIL+" ORDER BY r.status,t.sendtime desc", ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY );
			pstmt.setString(1, receiptor);
		} else if(type==SEND_MAIL){
			pstmt= this.conn.prepareStatement( SQL_SEL_SEND_MAIL+" ORDER BY t.sendtime desc", ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY );
			pstmt.setString(1, receiptor);
		}  else if(type==DRAFT_MAIL){
			pstmt= this.conn.prepareStatement( SQL_SEL_DRAFT_MAIL, ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY );
			pstmt.setString(1, receiptor);
		} else if(type==RECY_MAIL) {
			pstmt= this.conn.prepareStatement( SQL_SEL_RECY_MAIL, ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY );
			pstmt.setString(1, receiptor);
			pstmt.setString(2, receiptor);
		} else if(type==DEL_MAIL){
			pstmt= this.conn.prepareStatement( SQL_SEL_DEL_MAIL, ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY );
			pstmt.setString(1, receiptor);
			pstmt.setString(2, receiptor);
		} else if(type==NEW_MAIL){
			pstmt= this.conn.prepareStatement( SQL_SEL_NEW_MAIL, ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY );
			pstmt.setString(1, receiptor);
		}else{
			return arr_mid;
		}
		
		ResultSet rs=pstmt.executeQuery();
		rs.last();
		int rsCount = rs.getRow();
		arr_mid = new int[rsCount];
		rs.first();
		for(int i=0; i<rsCount; i++,rs.next() ){
			arr_mid[i] = rs.getInt(1);
		}
		pstmt.close();
		rs.close();
		
		return arr_mid;
	}
	
	/**
	 * È¡ï¿½ï¿½ï¿½Ê¼ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½â£¬ï¿½ï¿½ï¿½Ý¡ï¿½
	 * @param mailid
	 * @return
	 * @throws SQLException
	 */
	public Element getMail(int mailid) throws SQLException{
		Element elm_info = new Element( "readMail" );
		
		PreparedStatement pstmt = conn.prepareStatement(SQL_SEL_INFO_MAIL);
		pstmt.setInt(1, mailid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adp = new XResultAdapter( rs );
		if(rs.next())
			elm_info=adp.getElement4CurrentRow("readMail");
		
		rs.close();
		pstmt.close();
		
		String content = SqlUtil.fromLocal( getMailBody(mailid).toString() );
		elm_info.addContent( new Element( "mailBody" ).addContent( content ) );
		
		return elm_info;
	}
	
	
	/**
	 * È¡ï¿½ï¿½ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	 * @param mailid
	 * @return
	 * @throws SQLException
	 */
	public StringBuffer getMailBody(int mailid) throws SQLException{
		StringBuffer strBuf = new StringBuffer();
		PreparedStatement pstmt = conn.prepareStatement(SQL_SEL_CONTENT_MAIL);
		pstmt.setInt(1, mailid);
		ResultSet rs = pstmt.executeQuery();
		while( rs.next() ){
			String temp = rs.getString(1);
			strBuf.append(temp);
		}
		rs.close();
		pstmt.close();
		
		return strBuf;
	}
	

	
	//ï¿½Õ¼ï¿½ï¿½ï¿½
	static private String SQL_SEL_RECE_MAIL = " SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, (r.status) as status, r.recetime, r.readtime, (r.ifback) ifback, r.backid, r.backtime FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND r.status IN (0,1) AND  r.receiptor=? ) and rownum<=2000  ";
	//ï¿½ï¿½ï¿½Ê¼ï¿½
	static private String SQL_SEL_NEW_MAIL = " SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02,  (t.fileid) as fileid, (r.status) as status, r.recetime, r.readtime, (r.ifback) ifback, r.backid, r.backtime FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND r.status=0 AND  r.receiptor=? )";
	//ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	static private String SQL_SEL_SEND_MAIL = " SELECT  t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, r.readtime, (r.ifback) ifback, r.backid, r.backtime FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND seqno=0 ) WHERE t.status=1 AND t.sender=? and rownum<=2000 ";
	
	//ï¿½Ý¸ï¿½
	static private String SQL_SEL_DRAFT_MAIL = " SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid FROM mail_title t  WHERE status=0 AND t.sender=?";
	
	//ï¿½ï¿½ï¿½ï¿½Õ¾
	static private String SQL_SEL_RECY_MAIL  = " SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, (r.status) as status, 'send' as type, 'S' as type_cn,  r.readtime, (r.ifback) ifback, r.backid, r.backtime " +
			" FROM mail_title t " +
			" LEFT JOIN mail_receiptor r ON ( r.mailid=t.mailid AND seqno=0 ) " +
			" WHERE t.status=2 AND sender=?" +
			" UNION all " +
			" SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, (r.status) as status, 'receipt' as type, 'R' as type_cn,  r.readtime, (r.ifback) ifback, r.backid, r.backtime " +
			" FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND r.status=2 AND  r.receiptor=? )";
	
	//É¾ï¿½ï¿½ï¿½ï¿½ï¿½Ê¼ï¿½
	static private String SQL_SEL_DEL_MAIL   = " SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, (r.status) as status, 'send' as type, 'S' as type_cn, r.readtime,  (r.ifback) ifback, r.backid, r.backtime " +
			" FROM mail_title t " +
			" LEFT JOIN mail_receiptor r ON ( r.mailid=t.mailid AND seqno=0 ) " +
			" WHERE t.status=-100 AND sender=?" +
			" UNION all " +
			" SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, (r.status) as status, 'receipt' as type, 'R' as type_cn,  r.readtime, (r.ifback) ifback, r.backid, r.backtime " +
			" FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND r.status=-100 AND  r.receiptor=? )";

	//ï¿½ï¿½ï¿½ï¿½ï¿½Õµï¿½ï¿½ï¿½ï¿½Ê¼ï¿½
	static private String SQL_ALL_RECY_MAIL = "SELECT t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, (r.status) as status, r.recetime, r.readtime, (r.ifback) ifback, r.backid, r.backtime FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND  r.receiptor=? )";
	
	//ï¿½ï¿½ï¿½Ð·ï¿½ï¿½Íµï¿½ï¿½Ê¼ï¿½
	static private String SQL_ALL_SEND_MAIL = "SELECT  t.mailid, t.sender, t.title, t.sendtime, t.receiptor01, t.receiptor02, (t.fileid) as fileid, r.readtime, (r.ifback) ifback, r.backid, r.backtime FROM mail_title t INNER JOIN mail_receiptor r ON ( r.mailid=t.mailid AND seqno=0 ) WHERE t.sender=?";
	
	//ï¿½Ê¼ï¿½ï¿½ï¿½Ï¢
	static private String SQL_SEL_INFO_MAIL = " select m.mailid, m.sender, m.title, m.sendtime, m.receiptor01, m.receiptor02, m.ifwriteback, m.fileid, f.filename from mail_title m left join fileinfo f on (f.fileid=m.fileid) where m.mailid=? " ;
	
	//ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½ï¿½
	static private String SQL_SEL_CONTENT_MAIL = " select content from mail_body where mailid=? order by seqno ";
	
	final private Connection conn;
	public static int NEW_MAIL   = 100;
	public static int RECE_MAIL  = 101;
	public static int SEND_MAIL  = 102;
	public static int DRAFT_MAIL = 103;
	public static int RECY_MAIL  = 104;
	public static int DEL_MAIL   = 105;
	public static int ALL_RECV_MAIL   = 201;
	public static int ALL_SEND_MAIL   = 202;
}
