package com.royalstone.vss.liquidation;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;

public class LiquidationLogReport {

	/**
	 * 导出对账申请日志
	 * @param conn
	 * @param parms
	 * @param file
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws IOException 
	 */
	public int cookExcelFile( Connection conn, Map parms, File file ) throws InvalidDataException, SQLException, IOException{
		
		String[] title = {"供应商","提交日期","对帐申请单号","付款单号","对帐人员","对帐日期","说明"};
		
		Filter filter = cookFilter(parms);
		String sql = ( filter.count() > 0 )?sql_sel+" WHERE "+filter.toString() : sql_sel;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "对账申请日志" );
		
		rs.close();
		stmt.close();
		return rows;
	}
	
	private Filter cookFilter( Map parms ) throws InvalidDataException{
		Filter filter = new Filter();;
		
		String[] ss;
		
		ss = (String[]) parms.get("editdate_min");
		if( ss != null && ss.length != 0){
			String date = ss[0];
			filter.add( " l.editdate >= " + ValueAdapter.std2mdy(date) );
		}
		ss = (String[]) parms.get("editdate_max");
		if( ss != null && ss.length != 0){
			String date = ss[0];
			filter.add( " l.editdate <= " + ValueAdapter.std2mdy(date) );
		}
		
		return filter;
	}
	
	private String sql_sel=" select l.venderid, l.editdate, ll.sheetid,ll.refsheetid,ll.operator,ll.operatedate,ll.note " +
			" from liquidationlog ll " +
			" inner join liquidation l on (l.sheetid = ll.sheetid ) ";
}
