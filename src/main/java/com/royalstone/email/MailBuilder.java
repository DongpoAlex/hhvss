package com.royalstone.email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.util.sql.SqlUtil;

/**
 * 邮件建造者，负责新建邮件
 * @author baibai
 *
 */
public class MailBuilder {
	/**
	 * 构造函数
	 * @param conn
	 * @param sender 发件人
	 * @param receiptor 收件人
	 * @param cc　抄送人
	 * @param title　邮件标题
	 * @param content　邮件内容
	 * @param fileid　是否有附件
	 */
	public MailBuilder(Connection conn, String sender, String receiptor, 
			String cc, String title, String content, int fileid, String[] arrRece ) {		
		this.conn = conn;
		this.sender=sender;
		this.receiptor=receiptor;
		this.cc=cc;
		this.title=SqlUtil.toLocal(title);
		this.content=SqlUtil.toLocal(content);
		this.fileid = fileid;
		this.arrRece = arrRece;
	}
	
	/**
	 * 保存新邮件
	 * @throws Exception 
	 */
	public int saveMail(int status) throws Exception{
		
		try {
			conn.setAutoCommit(false);
			this.newMailID = getMaxMailID();
			
			saveTitle(status);

			saveReceiptor( arrRece );
			
			saveContent();
			
			conn.commit();
			return newMailID;
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
		}
	}
	
	/**
	 * 保存邮件标题
	 * @param status 邮件状态指定，1正常发送， 0保存为草稿
	 * @throws SQLException
	 */
	private void saveTitle(int status) throws SQLException{
		PreparedStatement pstmt = conn.prepareStatement(SQL_SAVE_TITLE);
		pstmt.setInt(1, this.newMailID);
		pstmt.setString(2, this.sender);
		pstmt.setInt(3, status);
		pstmt.setString(4, this.title);
		pstmt.setString(5, this.receiptor);
		pstmt.setString(6, this.cc);
		pstmt.setInt(7, this.fileid);
		pstmt.execute();
		pstmt.close();
		
	}
	/**
	 * 保存邮件正文内容
	 * @throws SQLException
	 */
private void saveContent() throws SQLException{
		
		int iPart = this.content.length() / 250;
		if ((this.content.length() % 250)>0) iPart = iPart + 1;
		String[] bodyDetail = new String[iPart];		
		String tmpbody = this.content;
		
		for ( int i=0; i<iPart; i++){
			if (tmpbody.length()<250) 
				bodyDetail[i] = tmpbody;
			else {
				bodyDetail[i] = tmpbody.substring(0,250);
				tmpbody = tmpbody.substring(250);
			}
		}
		
		PreparedStatement pstmt = conn.prepareStatement(SQL_SAVE_CONTENT);
		for (int j=0; j<bodyDetail.length; j++){
			int delrow = j+1;
			pstmt.setInt(1, this.newMailID);
			pstmt.setInt(2, delrow);
			pstmt.setString(3, bodyDetail[j]);
			pstmt.execute();
		}
		
		pstmt.close();
	}


	/**
	 * 保存收件人信息
	 * @param receiptor 收件人
	 * @param isMain 是否主收件人 0次要(抄送), 1主要(非抄送)
	 * @throws SQLException
	 */
	private void saveReceiptor(String[] arrReceiptor) throws SQLException{
		PreparedStatement pstmt = conn.prepareStatement(SQL_SAVE_RECEIPTOR);
		for (int i = 0; i < arrReceiptor.length; i++) {
			if( arrReceiptor[i].length()>0 ){
				pstmt.setInt(1, newMailID);
				pstmt.setString(2, arrReceiptor[i]);
				pstmt.setInt(3, 0);
				pstmt.setInt(4, seqno);
				pstmt.executeUpdate();
				seqno++;
			}
		}
		pstmt.close();
	}
	

	/**
	 * 获得新邮件ID
	 * @return
	 * @throws SQLException
	 */
	private int getMaxMailID() throws SQLException{
		int id = 0;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( SQL_SEL_NEW_MAILID );
		if(rs.next()){
			id= rs.getInt(1) + 1;
		}
		rs.close();
		stmt.close();
		return id;
	}
	
    private int newMailID = 0;					//邮件号
    private String sender = "";				//发件人
    private String receiptor = "";		//收件人
    private String cc 		= "";		//抄送
    private String title = "";				//主题
    private String content;					//内容
    private int fileid = 0;					//是否有附件
    private String[] arrRece;				//收件人列表
    
    private Connection conn = null;
    private int seqno = 0;
	
	//保存邮件标题
	private String SQL_SAVE_TITLE = "insert into mail_title( mailid,sender,status,title,receiptor01,receiptor02,fileid ) values(?,?,?,?,?,?,?)";
	
	//保存邮件内容
	private String SQL_SAVE_CONTENT ="insert into mail_body( mailid, seqno, content ) values(?,?,?)";
	
	//保存邮件收件人
	private String SQL_SAVE_RECEIPTOR = "insert into mail_receiptor( mailid,receiptor,status,seqno ) values(?,?,?,?)";
	
	//查询最大邮件ID
	private String SQL_SEL_NEW_MAILID = "select max(mailid) from mail_title";
	
}
