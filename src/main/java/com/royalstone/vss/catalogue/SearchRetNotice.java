package com.royalstone.vss.catalogue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 
 * 此模块用于查询退货单目录.
 * @author meng
 *
 */
public class SearchRetNotice 
{
	final static private String sql_sel =  " SELECT re.sheetid, status4ret( c.status ) status, TO_CHAR(c.readtime,'YYYY-MM-DD HH24:MI:SS') readtime, " +
			" re.venderid, v.vendername, re.shopid, s.shopname, " +
			" re.paytypeid, p.paytypename, re.retdate, re.flag, " +
			" re.editor, re.editdate, re.operator, re.checker, re.checkdate,re.askshopid ,ss.shopname as askshopname, re.rettype " ;
	
	final static private String sql_join = " FROM  cat_retnotice c " +
			" JOIN retnotice re ON ( c.sheetid = re.sheetid ) " +
			" JOIN vender v ON (re.venderid=v.venderid) " +
			" JOIN paytype p ON (re.paytypeid=p.paytypeid) " +
			" JOIN shop s ON (re.shopid=s.shopid) " +
			" LEFT JOIN shop ss ON (re.askshopid=ss.shopid) " +
			"  " ;
	
	public SearchRetNotice ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		this.map = map;

	}
	
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) " + sql_join + sql_where ;
		String temp = SqlUtil.querySQL4SingleColumn(conn, sql).get(0);
		int rows = 0;
		if(temp!=null){
			rows = Integer.parseInt(temp);
		}
		
		return rows;
	}
	

	private Filter cookFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		String[] ss = null;
		
		ss = (String [] ) map.get( "venderid" );
		if( ss != null && ss.length >0 ) {
			Values val_vender = new Values ( ss );
			filter.add( "c.venderid IN (" + val_vender.toString4String() + ") " );
		}
		
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "sheetid" );
		if( ss != null && ss.length >0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "c.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		

		
		ss = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_house = new Values ( ss );
			filter.add( "re.shopid IN (" + val_house.toString4String() + ") " );
		}
		
		ss = (String [] ) map.get( "editor" );
		if( ss != null && ss.length >0 ) {
			Values val_editor = new Values ( ss );
			filter.add( "re.editor IN (" + val_editor.toString4String() + ") " );
		}
		
		ss = (String [] ) map.get( "checker" );
		if( ss != null && ss.length >0 ) {
			Values val_checker = new Values ( ss );
			filter.add( "re.checker IN (" + val_checker.toString4String() + ") " );
		}
		
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(re.editdate) >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(re.editdate) <= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(re.checkdate) >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(re.checkdate) <= " + ValueAdapter.std2mdy(date)  );
		}

		ss = ( String [] ) map.get( "retdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(re.retdate) >= " +ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "retdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(re.retdate) <= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "status" );
		if( ss != null && ss.length >0 ) {
			Values v = new Values ( ss );
			filter.add( "c.status IN ( " + v.toString() + " ) "  );
		}
		
		ss = ( String [] ) map.get( "warnstatus" );
		if( ss != null && ss.length >0 ) {
			Values v = new Values ( ss );
			filter.add( "c.warnstatus IN ( " + v.toString() + " ) "  );
		}
		
		ss = ( String [] ) map.get( "flag" );
		if( ss != null && ss.length >0 ) {
			String flag = ss[0];
			filter.add( "re.flag = " + flag  );
		}
		
		return filter;
	}
	
	public Element toElement() throws InvalidDataException, SQLException {
		Filter filter = cookFilter( map );
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where +" ORDER BY c.sheetid DESC";
	
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "rowset");
		elm_cat.setAttribute("row_total", "" + count);
		return elm_cat;
		
	}
	
	public void cookExcelFile(File file) throws SQLException, InvalidDataException, IOException{
		String[] title= {"单据号","状态","操作时间","供应商编码","供应商名称","门店编码",
				"门店名称","支付模式编码","支付模式","单据日期","标志","编辑人","编辑日期","业务员","审核人","审核日期"};
			Filter filter = cookFilter( map );
			
			if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
			
			int count = this.getCount( filter );
			if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
			
			String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
			String sql = sql_sel + sql_join + sql_where +" ORDER BY c.sheetid DESC";
		
			ResultSet rs = SqlUtil.querySQL(conn, sql);
			SimpleExcelAdapter excel = new SimpleExcelAdapter();
			excel.cookExcelFile(file, rs, title, "退货通知单");
			SqlUtil.close(rs);
	}
	
	final private Connection conn;
	final private Map map;
}