/*
 * Created on 2004-10-27
 *
 */
package com.royalstone.util.daemon;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

/**	为了避免潜在的名称冲突. 临时表的名字统一由NameManager 管理.
 * 操作数据库的后台模块调用 NameManager.getTmpName() 取得临时表的名字.
 * 名字从0开始编号,到达最大值后清零,再从头开始.
 * 处理机制与操作系统中的进程ID相类似.
 * @author Mengluoyi
 *
 */
public class DbAdm
{
    /**	此函数返回一个"唯一"的名字. 此名字从0开始编号,到达最大值后清零,再从头开始.
     * @return	为临时表准备的"唯一"的名字.
     */
    public static String getTmpName()
    {
    	DecimalFormat df = new DecimalFormat( "00000000" );
        if( serlid > MAX_ID ) serlid = 0;
        String str = df.format(serlid);
        serlid++;
        return "tmp" + str;
    }
    
    public static void createTempTable(Connection conn, String sql){
//    	if(sql.toLowerCase().indexOf("on commit delete rows")==-1)
//    		sql +=" on commit delete rows ";
    	Statement stmt=null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();
		}
		catch (SQLException e) {
			//e.printStackTrace();
		} finally {
			if(stmt!=null )
				try {
					stmt.close();
				} catch (SQLException e1) {
					// do nothing.
				}
		}
    }
    
    public static void clearData(Connection conn, String table){
    	String sql =" delete from "+table;
    	Statement stmt=null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();
		}
		catch (SQLException e) {
			//e.printStackTrace();
		} finally {
			if(stmt!=null )
				try {
					stmt.close();
				} catch (SQLException e1) {
					// do nothing.
				}
		}
    }
    /**	此函数直接执行一个不带参数的SQL查询语句。
     * @param sql	待执行的SQL语句.
     * @return		true	成功<br>
     * 				false	执行失败.
     * @throws SQLException		执行过程中出现意外.
     */
    public static boolean executeDirect(Connection conn, String sql) throws SQLException
    {
    	boolean ok = false;
    	Statement stmt = conn.createStatement();
    	ok = stmt.execute(sql);
    	stmt.close();
    	return ok;
    }
    
    public static void dropTable( Connection conn, String tabname )
    {
    	Statement stmt=null;
    	try {
			stmt = conn.createStatement();
			String sql1 = " delete from "+tabname;
			//String sql2 = " DROP TABLE " + tabname;
			stmt.execute(sql1);
			//stmt.execute(sql2);
		} catch (SQLException e) {
			// do nothing.
		} finally {
			if(stmt!=null )
				try {
					stmt.close();
				} catch (SQLException e1) {
					// do nothing.
				}
		}
    	
    }
    
    /**
     * <code>MAX_ID</code>"唯一编号"的上限值. 编号到此值后,将清零,从头开始.
     */
    final static private int MAX_ID = 1000000;
    private static int serlid = 0;
}
