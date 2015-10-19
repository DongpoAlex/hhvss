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

/**
 * 
 * 此模块用于查询退货单目录.
 * @author meng
 *
 */
public class SearchDeduction
{
	final static int ROWS_LIMIT_HARD = 10000;
	final static int ROWS_LIMIT_SOFT = 1000;
	
	final static private String sql_sel =  " SELECT  monthid,shopid,venderid,vendername,editor,goodsid,goodsname,orderqty," +
			" receiptqty,nodeliverqty,nodelivercostvalue,round(receiptrate*100,0)||'%' as receiptrate ," +
			" punishvalue1,sheetid,ordershopid,cost " ;
	
	final static private String sql_join = " FROM EcVenderDeduction ";
	
	public SearchDeduction ( Connection conn, Map map ) throws SQLException, InvalidDataException
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
		if( count > ROWS_LIMIT_HARD ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where +" ORDER BY monthid,goodsid DESC";

		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_cat = adapter.getRowSetElement( "catalogue", "row", 1, ROWS_LIMIT_SOFT );
		rs.close();
		pstmt.close();
		int rows = adapter.rows();
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "retnotice" );
		return elm_cat;
		
	}
	
	
	public void toBook(File fdown) throws InvalidDataException, IOException, RowsExceededException, WriteException, SQLException{
		
		String[] title= {"年月","区域","供应商编号","供应商名称 ","库控员","商品编号","商品名称",
			"订货数量","验收数量","未到货数量","未到货金额","单品到货满足率","扣款金额","订单单号","订货门店","当前进货单价"};
		Filter filter = cookFilter( map );
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > ROWS_LIMIT_HARD ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where +" ORDER BY monthid,goodsid DESC";
	
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(fdown, rs, title, "retnotice");
		
		rs.close();
		pstmt.close();
	}
	public  String uploadCharge(Element elm) throws 
		 SQLException {
	//	MailHelper mh = new MailHelper();
		StringBuffer sb = new StringBuffer();
		String monthid = "";
		String shopid = "";
		String venderid = "";
		String vendername = "";
		String editor = "";
		String goodsid = "";
		String goodsname = "";
		String orderqty = "";
		String receiptqty = "";
		String nodeliverqty = "";
		String nodelivercostvalue = "";
		String receiptrate = "";
		String punishvalue1 = "";
		String sheetid = "";
		String ordershopid = "";
		String cost = "";
		
		String sql_del = " delete from EcVenderDeduction where monthid=? and ordershopid=? and venderid=? and goodsid=? and sheetid=? ";
		String sql_ins = " insert into EcVenderDeduction(monthid,shopid,editor,venderid,vendername,goodsid,goodsname,orderqty," +
			" receiptqty,nodeliverqty,nodelivercostvalue,receiptrate ,punishvalue1,sheetid,ordershopid,cost) " +
			" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Element row_set = elm.getChild("row_set");
		List list = row_set.getChildren("row");
		Iterator it = list.iterator();
		
		while (it.hasNext()) {
			Element elm_row = (Element) it.next();
			monthid = elm_row.getAttributeValue("monthid");
			shopid = elm_row.getAttributeValue("shopid");
			venderid = elm_row.getAttributeValue("venderid");
			vendername = elm_row.getAttributeValue("vendername");
			if(vendername == null || vendername.length()==0 ) vendername= " ";
			editor = elm_row.getAttributeValue("editor");
			if(editor == null || editor.length()==0 ) editor= " ";
			goodsid = elm_row.getAttributeValue("goodsid");
			goodsname = elm_row.getAttributeValue("goodsname");
			if(goodsname == null || goodsname.length()==0 ) goodsname= " ";
			orderqty = elm_row.getAttributeValue("orderqty");
			receiptqty = elm_row.getAttributeValue("receiptqty");
			nodeliverqty = elm_row.getAttributeValue("nodeliverqty");
			nodelivercostvalue = elm_row.getAttributeValue("nodelivercostvalue");
			receiptrate = elm_row.getAttributeValue("receiptrate");
	//		receiptrate =receiptrate.replace("%","");
			punishvalue1 = elm_row.getAttributeValue("punishvalue1");
			sheetid = elm_row.getAttributeValue("sheetid");
			ordershopid = elm_row.getAttributeValue("ordershopid");
			cost = elm_row.getAttributeValue("cost");
			
				if (monthid!=null && venderid !=null &&goodsid!=null ) {
					SqlUtil.executePS(conn, sql_del, 
							new Object[]{Integer.parseInt(monthid),
									shopid.trim(),venderid,Integer.parseInt(goodsid),
									sheetid.trim()
							});
					
					int j = SqlUtil.executePS(conn, sql_ins, 
							new Object[]{Integer.parseInt(monthid),
							shopid.trim(),
							editor,venderid,
							vendername,
							Integer.parseInt(goodsid),
							goodsname,
							Float.parseFloat(orderqty),
							Float.parseFloat(receiptqty),
							Float.parseFloat(nodeliverqty),
							Float.parseFloat(nodelivercostvalue),
							Float.parseFloat(receiptrate)/100,
							Float.parseFloat(punishvalue1),
							sheetid.trim(),ordershopid,Float.parseFloat(cost)
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