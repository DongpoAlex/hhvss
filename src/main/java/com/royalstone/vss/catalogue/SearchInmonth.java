package com.royalstone.vss.catalogue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;
public class SearchInmonth
{
	final static private String sql_sel =  " SELECT monthid,venderid,vendername,goodsid,goodsname,qty,taxcost,cost " ;
	
	final static private String sql_join = " FROM rp_in_month ";
	
	public SearchInmonth ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		this.map = map;

	}
	
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) " + sql_join + sql_where ;

		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
		int rows = rs.getInt( 1 );

		rs.close();
		pstmt.close();
		return rows;
	}
	

	private Filter cookFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		String[] ss = null;
		
		String[] venderid0 = null;
		venderid0 = (String [] ) map.get( "venderid" );
		String venderid = venderid0[0].trim();
		filter.add( "venderid in ('" + venderid +"' )" );
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "yearmonth" );
		if(  ss != null && ss.length >0) {
			Values val_sheetid = new Values ( ss );
			filter.add( "monthid IN (" + val_sheetid.toString() + ") " );
		}
		
		ss = (String [] ) map.get( "goodsid" );
		if( ss != null && ss.length >0 ) {
			Values val_vender = new Values ( ss );
			filter.add( "goodsid IN (" + val_vender.toString() + ") " );
		}
		
		ss = (String [] ) map.get( "goodsname" );
		if( ss != null && ss.length >0 ) {
			Values val_editor = new Values ( ss );
			filter.add( val_editor.toString4like("goodsname"));
		}
		
		
		ss = ( String [] ) map.get( "flag" );
		if( ss != null && ss.length >0 ) {
			String flag = ss[0];
			filter.add( "flag = " + flag  );
		}
		
		return filter;
	}
	
	public Element toElement() throws InvalidDataException, SQLException {
		Filter filter = cookFilter( map );
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where +" ORDER BY monthid,goodsid DESC";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_cat = adapter.getRowSetElement( "catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft() );
		rs.close();
		pstmt.close();
		int rows = adapter.rows();
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "inmonth" );
		return elm_cat;
		
	}
	
	
	public void toBook(File fdown) throws InvalidDataException, IOException, RowsExceededException, WriteException, SQLException{
		
		String[] title= {"年月","供应商编号","供应商名称 ","商品编号","商品名称",
			"数量","含税进货单价","进货单价"};
		Filter filter = cookFilter( map );
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where +" ORDER BY monthid,goodsid DESC";
	
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(fdown, rs, title, "inmonth");
		
		rs.close();
		pstmt.close();
	}
	public  String uploadCharge(Element elm) throws 
		 SQLException {
	//	MailHelper mh = new MailHelper();
		StringBuffer sb = new StringBuffer();
		String monthid = "";
		String venderid = "";
		String vendername = "";
		String goodsid = "";
		String goodsname = "";
		String qty = "";
		String taxcost = "";
		String cost = "";
		
		String sql_del = " delete from rp_in_month where monthid=? and venderid=? and goodsid=? ";
		String sql_ins = " insert into rp_in_month(monthid,venderid,vendername,goodsid,goodsname,qty,taxcost,cost) " +
			" values(?,?,?,?,?,?,?,?)";
		
		Element row_set = elm.getChild("row_set");
		List list = row_set.getChildren("row");
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element elm_row = (Element) it.next();
			monthid = elm_row.getChildText("monthid");
			venderid = elm_row.getChildText("venderid");
			vendername = elm_row.getChildText("vendername");
			if(vendername == null || vendername.length()==0 ) vendername= " ";
			goodsid = elm_row.getChildText("goodsid");
			goodsname = elm_row.getChildText("goodsname");
			if(goodsname == null || goodsname.length()==0 ) goodsname= " ";
			qty = elm_row.getChildText("qty");
			taxcost = elm_row.getChildText("taxcost");
			cost = elm_row.getChildText("cost");
				if (monthid!=null && venderid !=null &&goodsid!=null ) {
					SqlUtil.executePS(conn, sql_del, 
							new Object[]{Integer.parseInt(monthid),
									venderid,Integer.parseInt(goodsid)
							});
					
					int j = SqlUtil.executePS(conn, sql_ins, 
							new Object[]{Integer.parseInt(monthid),
							venderid,
							vendername,
							Integer.parseInt(goodsid),
							goodsname,
							Integer.parseInt(qty),
							Float.parseFloat(taxcost),
							Float.parseFloat(cost)
					});
					if (j != 1) {
						sb.append(monthid + "," + venderid + " , "+goodsid+":");
					}
			} 
		}
		
		return sb.toString();
	}
	
	final private Connection conn;
	final private Map map;
}