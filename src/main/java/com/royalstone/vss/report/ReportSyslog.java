package com.royalstone.vss.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;


/**
 * 此模块用于查询系统操作日志
 * @author meng
 */
public class ReportSyslog
{

	private Map map;
	/**
	 * Constructor
	 * @param conn
	 * @throws SQLException 
	 */
	public ReportSyslog( Connection conn, Map map )
	{
		this.conn = conn;
		this.map = map;
	}
	
	public Element toElement() throws SQLException, InvalidDataException
	{
		fetchLog( map );
		return elm_report;
	}
	

	/**
	 * 根据前台提供的过滤条件, 在数据库中查询满足条件的日志, 并以XML形式返回.
	 * @param map
	 * @return
	 * @throws InvalidDataException 
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 */
	private void fetchLog( Map map ) throws SQLException, InvalidDataException
	{
		Filter filter = getFilter( map );
		int count = getCount( filter );
		if (  count > VSSConfig.getInstance().getRowsLimitHard() ) throw new IllegalArgumentException("查询结果超过系统处理限制，请重新选择过滤条件");
		String sql = " SELECT l.logid, l.logdate, l.logtime, l.client, " +
				" l.loginid, l.userid, l.username, l.moduleid, l.modulename, l.userlevel, l.errcode, l.note " +
				" FROM syslog l" ;
		String[] strs = ( String[] ) map.get( "moduleid" );
		if( strs != null && strs.length >0 ) {			
			int moduleid = Integer.parseInt( strs[0]);
			if(moduleid==103){
				Filter filters = new Filter();				
					String[] str = ( String[] ) map.get( "logdate_min" );
					if( str != null && str.length >0 ) {
						String date = str[0];
						filters.add( " trunc(l.logdate) >= " + ValueAdapter.std2mdy(date) );
					}
				
					str = ( String[] ) map.get( "logdate_max" );
					if( str != null && str.length >0 ) {
						String date = str[0];
						filters.add( " trunc(l.logdate) <= " + ValueAdapter.std2mdy(date) );
					}
					
					String[] strNumber = ( String[] ) map.get( "opt_number" );
					if( strNumber == null && strNumber.length ==0 ) {
						strNumber=new String[]{"80"};			
					}
				
				sql="SELECT  D.CONTROLTYPE, COUNT(C.LOGINID) loginid, '"+strNumber[0]+"' DJE, COUNT(C.LOGINID) * "+strNumber[0]+" SUNMJE FROM "
						+ "(SELECT l.LOGINID FROM SYSLOG l WHERE userlevel=100 and l.MODULEID = 102  AND "+filters.toString()+" GROUP BY LOGINID) C, "
						+ "(SELECT A.VENDERID, B.CONTROLTYPE FROM GOODSSHOP A, SHOP B WHERE A.SHOPID = B.SHOPID AND CONTROLTYPE>1 GROUP BY A.VENDERID, B.CONTROLTYPE) D "
						+ "WHERE C.LOGINID = D.VENDERID GROUP BY D.CONTROLTYPE";
				
			}
		}else{	

			if( filter.count()>0 ) sql += " WHERE " + filter.toString();
			sql += " ORDER BY l.logid ";
		}
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		
		/**
		 * 查询返回的记录数可能很多. 仅取最前面的10000行.
		 */
		elm_report = adapter.getRowSetElement( "report", "row", 1, VSSConfig.getInstance().getRowsLimitHard() );
		
		int rows = adapter.rows();
		elm_report.setAttribute("count", "" + count);
		elm_report.setAttribute( "rows", "" + rows );
		elm_report.setAttribute( "name", "syslog" );
		rs.close();
		pstmt.close();
		return;
	}
	

	/**
	 * 取得符合条件的记录数
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	private int getCount( Filter filter ) throws SQLException
	{
		String sql = " SELECT count(*) FROM syslog l" ;
		if( filter.count()>0) sql += " WHERE " + filter.toString();
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( !rs.next() ) throw new SQLException( "getCount failed!", "", -1 );
		int count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}
	
	/**
	 * @param map
	 * @return
	 * @throws InvalidDataException 
	 */
	private Filter getFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		
		{
			String[] strs = ( String[] ) map.get( "level" );
			if( strs != null && strs.length >0 ) {
				filter.add( " l.userlevel IN ( " + ( new Values( strs ) ).toString() + ") " );
			}
		}
		
		{
			String[] strs = ( String[] ) map.get( "moduleid" );
			if( strs != null && strs.length >0 ) {
				String moduleid = strs[0];
				if( moduleid != null ) filter.add( " l.moduleid IN (" + ( new Values( strs ) ).toString4String() + ") " );
			}
		}
				
		{
			String[] strs = ( String[] ) map.get( "loginid" );
			if( strs != null && strs.length >0 ) {
				filter.add( " l.loginid IN (" + ( new Values( strs ) ).toString4String() + ") " );
			}
		}
		
		{
			String[] strs = ( String[] ) map.get( "logdate_min" );
			if( strs != null && strs.length >0 ) {
				String date = strs[0];
				filter.add( " trunc(l.logdate) >= " + ValueAdapter.std2mdy(date) );
			}
		}
		
		{
			String[] strs = ( String[] ) map.get( "logdate_max" );
			if( strs != null && strs.length >0 ) {
				String date = strs[0];
				filter.add( " trunc(l.logdate) <= " + ValueAdapter.std2mdy(date) );
			}
		}
			
		return filter;
	}

	public Element getStat( String filtrate) throws SQLException, InvalidDataException{
		Filter filter = getFilter( map );
		
		String sql_stat = " select l.moduleid,count("+filtrate+" loginid) count,m.modulename " +
				" from syslog l " +
				" left join module_list m on (m.moduleid = l.moduleid) ";
		if( filter.count()>0 ) sql_stat += " WHERE " + filter.toString() ;
		sql_stat += " group by l.moduleid,m.modulename ";

		PreparedStatement pstmt = conn.prepareStatement( sql_stat );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		
		/**
		 * 查询返回的记录数可能很多. 仅取最前面的10000行.
		 */
		elm_report = adapter.getRowSetElement( "report", "row", 1, VSSConfig.getInstance().getRowsLimitHard() );
		
		int rows = adapter.rows();
		elm_report.setAttribute( "rows", "" + rows );
		elm_report.setAttribute( "name", "syslog_stat" );
		rs.close();
		pstmt.close();
		return elm_report;
	}
	
	final private Connection conn ;
	private Element elm_report;
}
