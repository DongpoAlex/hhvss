/*
 * Created on 2005-7-26
 *
 */
package com.royalstone.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 * 文件管理，包括上传和下载文件 
 * 上传时，利用包：sun.misc.BASE64Encoder来把一个二进制数据流转换成可视的字符串
 * 下载时，利用包：sun.misc.BASE64Decoder来把可视的字符串转回成原来的二制数据流
 * 转换原理：二进制中的256个字符大部分是不可显视的，直接转成字符串时就会有问题，只能用“?”来代替。
 * BASE64表示64个基本的字符：26个大写字母 ＋ 26个小字母 ＋ 0～9共10个数字 + "+、/"2个符号
 * 正向转换时：每3个字节转成4个字节，不足补“=”。反向转换时：每4个字节转成3个字节
 * @author liuyk
 */
public class FileManager {
//
//	public static void main(String[] args) throws SQLException, IOException{
//		Connection conn = new DataSource().open("dbvss");
//		int fileid = 46;
//
//		FileInputStream input = new FileInputStream( "C:\\work\\updata\\20050726\\报表分布及重要报表详解.doc" );
//		inputFileBody( conn, 998, input );
//		input.close();
//		
//		inputFileInfo(conn,"中国");
//	}
//	
	
	public static String getFileName( Connection con, int fileid ) throws SQLException
	{
		String filename = "UNKNOWN";
		String sql4filename = "select filename from fileinfo where fileid=?";
		PreparedStatement pstmt = con.prepareStatement(sql4filename);
		pstmt.setInt(1,fileid);
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			filename = rs.getString("filename");			
		}
		
		rs.close();
		pstmt.close();
		
		return filename;
	}

	public static int inputFileInfo(Connection con, String filename) throws SQLException
	{
		int fileid = getNewFileid(con);
		
		String sql4Insertfileinfo = "insert into fileinfo(fileid, filename) values(?, ?)";
		PreparedStatement pstmt = con.prepareStatement(sql4Insertfileinfo);
		pstmt.setInt(1,fileid);
		pstmt.setString(2,filename);
		pstmt.execute();
		pstmt.close();
		
		return fileid;
	}
	
	public static int getNewFileid(Connection con) throws SQLException
	{
		int fileid=0;
		
		con.setAutoCommit(true);
		String sql4Selfileinfo = "select max(fileid) fileid from fileinfo ";
		PreparedStatement pstmt = con.prepareStatement(sql4Selfileinfo);
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			fileid = rs.getInt("fileid");			
		}
		rs.close();
		pstmt.close();
		
		return fileid+1;
	}

	public static void outputFileBody( Connection con, int fileid, OutputStream ostream ) throws SQLException, IOException
	{
		String sql4body = "select seqno, data from filebody where fileid=? order by seqno";
		PreparedStatement pstmt = con.prepareStatement(sql4body);
		pstmt.setInt(1,fileid);
		ResultSet rs = pstmt.executeQuery();

		/**
		 * 从数据库中逐行取出此文件的数据,解码后写入stream.
		 */	
		BASE64Decoder decoder = new BASE64Decoder(); 
		while (rs.next()){
			String data = rs.getString( "data" ).trim();
//			int len = rs.getInt( 3 );
//			System.out.println("write len: " + len);
			
			byte[] b = decoder.decodeBuffer(data);
			ostream.write(b);
		}
		rs.close();
		pstmt.close();
		ostream.flush();
	}
	
	public static void inputFileBody( Connection con, int fileid, InputStream instream ) throws SQLException, IOException
	{
		String sql4Insertfilebody = "insert into filebody(fileid,seqno,data) values(?,?,?)";
		PreparedStatement pstmt = con.prepareStatement(sql4Insertfilebody);
		int i=1;
		
		//183转成base64后刚好是250字节。
		//受base64的"由3个字节转成4个字节"的规则影响，定义的字节数组的下标一定要为3的倍数。
		byte[] b = new byte[ 183 ];
		byte[] bb;

		try {
			con.setAutoCommit(false);
			int len = instream.read(b, 0, b.length);
			while( len >0 )
			{				
				if( len == b.length ) bb = b;
				else {
					bb = new byte[len];
					System.arraycopy( b, 0, bb, 0, len );
				}
				String data = new BASE64Encoder().encode( bb );
				pstmt.setInt(1,fileid);
				pstmt.setInt(2,i);
				pstmt.setString(3,data);
				pstmt.execute();
				i++;
				len = instream.read(b, 0, b.length);
			}
			con.commit();
		} catch (SQLException e_sql) {
			e_sql.printStackTrace();
			con.rollback();
			throw e_sql;
		} finally {
			pstmt.close();
		}
	}
}
