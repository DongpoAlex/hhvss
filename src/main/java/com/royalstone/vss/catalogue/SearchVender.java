package com.royalstone.vss.catalogue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

public class SearchVender {
	private int count=0 ;
	final static private String sql_sel =  " SELECT "
		+" v.venderid,v.vendername,v.shortname,v.address,v.zipcode,"
		+" v.website,v.email,v.telno,v.faxno,Status4Vender(v.status) as status,v.touchtime,v.toucher"
		+" from vender v";
	
	public SearchVender (Connection conn, Map map ,String type) throws SQLException{
		Filter filter = cookFilter( map );
		this.conn = conn;
		count= this.getCount( filter );
	}
	public SearchVender ( Connection conn, Map map ) throws SQLException, InvalidDataException{
		Filter filter = cookFilter( map );
		this.conn = conn;
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )?" where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft(): " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft()+" and " + filter.toString() ;
		String sql = sql_sel +sql_where;
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft() );
		int rows = adapter.rows();
		if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
		elm_cat.setAttribute( "rows", "" + rows );
	}
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) from vender v " + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
		int rows = rs.getInt( 1 );

		return rows;
	}
	
	private Filter cookFilter( Map map )
	{
		Filter filter = new Filter();
		String[] ss = null,ss1=null;
		ss = (String [] ) map.get( "venderid_start" );
		ss1 = (String [] ) map.get( "venderid_end" );
		if( ss != null && ss.length >0 && ss1 != null && ss1.length>0) {
			Values val_goodsid_start = new Values ( ss );
			Values val_goodsid_end = new Values ( ss1 );
			filter.add("v.venderid > (" + val_goodsid_start.toString4String() + ") " );
			filter.add("v.venderid < (" + val_goodsid_end.toString4String() + ") " );
		}
		if(ss == null && ss1 != null){
			Values val_goodsid = new Values ( ss1 );
			filter.add("v.venderid = (" + val_goodsid.toString4String() + ") " );
		}
		if(ss1 == null && ss != null){
			Values val_goodsid = new Values ( ss );
			filter.add("v.venderid = (" + val_goodsid.toString4String() + ") " );
		}
		ss = (String [] ) map.get( "vendername" );
		if( ss != null && ss.length >0 ) {
			filter.add("v.vendername like '%" + ss[0] + "%'" );
		}
		ss = (String [] ) map.get( "address" );
		if( ss != null && ss.length >0 ) {
			filter.add("v.address like '%" + ss[0] + "%'" );
		}
		ss = (String [] ) map.get( "telno" );
		if( ss != null && ss.length >0 ) {
			Values val_telno = new Values ( ss );
			filter.add("v.telno IN ("+ val_telno.toString4String() + ") " );
		}
		ss = (String [] ) map.get( "accno" );
		if( ss != null && ss.length >0 ) {
			Values val_accno = new Values ( ss );
			filter.add("b.accno IN ("+ val_accno.toString4String() + ") " );
		}
		return filter;
	}
	public Element toElement() { return elm_cat; }
	public int toCount() { return count; }
	private Element elm_cat = null;
	final private Connection conn;
}
