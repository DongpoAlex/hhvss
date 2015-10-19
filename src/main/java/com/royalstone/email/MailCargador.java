
package com.royalstone.email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 邮件搬运工，负责改变邮件状态标识，已达到改变邮件在不同类型邮箱里显示的目的
 * @author baibai
 *
 */
public class MailCargador{

	
	/**
	 * @param conn
	 * @param loginid
	 * @param mailid
	 */
	public MailCargador( Connection conn,int[] mailid ){
		this.conn=conn;
		this.mailid = mailid;
	}

	
	/**
	 * 更新收件状态
	 * @param mailid
	 * @throws SQLException
	 */
	public void updateReceiptMail(String receiptor,int status) throws SQLException{
		String sql = "update mail_receiptor set status =? where receiptor=? and mailid=?";		

		try{
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < mailid.length; i++) {
				pstmt.setInt(1, status);
				pstmt.setString(2, receiptor);
				pstmt.setInt(3, mailid[i]);
				pstmt.executeUpdate();
			}
			
			conn.commit();
			pstmt.close();
			
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
		}
		
	}


	/**
	 * 更新发件状态
	 * @param mailid
	 * @param status
	 * @throws SQLException
	 */
	public void updateSenderMail( int status) throws SQLException{
		String sql = "update mail_title set status =? where mailid=?";		

		try{
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < mailid.length; i++) {
				pstmt.setInt(1, status);
				pstmt.setInt(2, mailid[i]);
				pstmt.executeUpdate();
			}

			conn.commit();
			pstmt.close();
			
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 移动收件箱邮件倒回收站
	 */
	public void moveReceiptToRecycle(String receiptor) throws SQLException{
		updateReceiptMail(receiptor, STATUS_RECYCLE);
	}
	
	/**
	 * 移动发件箱邮件到回收站
	 */ 
	public void moveSendToRecycle() throws SQLException{
		updateSenderMail(STATUS_RECYCLE);	
	}
	

	/**
	 * 移动收到的邮件到删除箱
	 */
	public void moveReceiptToDel(String receiptor) throws SQLException{
		updateReceiptMail(receiptor, STATUS_DELETE);
	}
	
	/**
	 * 移动发出邮件到删除箱
	 */ 
	public void moveSendToDel() throws SQLException{
		updateSenderMail(STATUS_DELETE);	
	}
	

	
	/**
	 * 从回收站恢复发送的邮件
	 */
	public void recoverSend() throws SQLException{
		updateSenderMail(STATUS_DEFAULT);
	}
	
	/**
	 * 从回收站恢复草稿的邮件
	 */
	public void recoverDraft() throws SQLException{
		updateSenderMail(STATUS_DRAFT);
	}
	
	/**
	 * 从回收站恢复收到的邮件
	 */
	public void recoverReceipt(String receiptor) throws SQLException{
		updateReceiptMail(receiptor, STATUS_DEFAULT);
	}
	
	
	
	
    private Connection conn = null;
    
    int[] mailid;
	
    public static int STATUS_DRAFT = 0;
    
    public static int STATUS_DEFAULT = 1;
    
    public static int STATUS_RECYCLE = 2;
    
    public static int STATUS_DELETE = -100;
}
