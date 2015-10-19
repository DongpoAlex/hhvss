package com.royalstone.vss.catalogue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * @author 白剑
 * 
 */
public class SearchChargesum {
	
	public SearchChargesum(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 根据过滤条件返回xml格式结果
	 * @param map 过滤条件
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public Element getChargeSum( Map map ) throws InvalidDataException, SQLException{

		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		
		int count = getCount(sql_where);
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		
		String sql_sel = " select " +
		" c.noteno, c.shopid, c.bookno, c.venderid, c.chargecodeid, " +
		" c.docdate, c.reckoningdate, c.chargeamt, c.chargerate, c.noteremark, " +
		" c.settlemode, c.invoicemode, c.flag, c.buyer, c.majorid, " +
		" s.shopname, b.bookname, cc.chargename, cg.categoryname " +
		" from chargesum0 c " +
		" inner join shop s on (s.shopid = c.shopid ) " +
		" inner join book b on (b.bookno = c.bookno )  " +
		" inner join chargecode cc on (cc.chargecodeid = c.chargecodeid ) " +
		" left  join category cg on (cg.categoryid = c.majorid) ";
		String sql = sql_sel+sql_where;

		Log.debug(this.getClass().getName(), sql);
		
		Statement stmt = conn.createStatement();
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		
		Element elm = parseRsToXMl( rs );
		
		rs.close();
		pstmt.close();
		stmt.close();
		
		return elm;
	}
	
	/**
	 * 返回结果集数据元中的列名数组
	 * @param meta
	 * @return
	 * @throws SQLException
	 */
	private String[] parseColumnName(ResultSetMetaData meta) throws SQLException{
		int columnCount = meta.getColumnCount();
		String[] columnName = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columnName[i] = meta.getColumnName(i+1);
		}
		
		return columnName;
	}
	

	
	/**
	 * 将结果集对象转换成xml对象输出
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Element parseRsToXMl( ResultSet rs ) throws SQLException{
		Element elm = new Element("sheet");
		ResultSetMetaData meta = rs.getMetaData();
		String[] columnName = parseColumnName(meta);
		String strTemp = "";
		while(rs.next()){
			Element elm_row = new Element("row");
			
			for (int i = 0; i < columnName.length; i++) {
				strTemp = rs.getString(columnName[i]);
				strTemp = strTemp==null?"":SqlUtil.fromLocal(strTemp);
				if( columnName[i].toLowerCase().equals("flag") ){
					strTemp = statusForFlag(Integer.parseInt(strTemp));
				}else if( columnName[i].toLowerCase().equals("settlemode") ){
					strTemp = statusForSettleMode(Integer.parseInt(strTemp));
				}else if( columnName[i].toLowerCase().equals("invoicemode") ){
					strTemp = statusInvoiceMode(Integer.parseInt(strTemp));
				}
				
				Element elm_temp = new Element( columnName[i].toLowerCase() );
				elm_temp.addContent(strTemp);
				elm_row.addContent(elm_temp);
			}
			
			elm.addContent(elm_row);
		}
		
		return elm;
	}
	
	
	/**
	 * 转换扣项状态信息为中文说明
	 * @param flag
	 * @return
	 */
	private String statusForFlag(int flag){
		String rel = "";
		switch (flag) {
		case 1:
			rel="待结算";
			break;
		case 2:
			rel="已收现";
			break;
		case 3:
			rel="已抽单待结算";
			break;
		case 4:
			rel="已付款确认";
			break;
		case 5:
			rel="已审定待付款确认";
			break;
		case 9:
			rel="已删除";
			break;
		default:
			rel="未定义";
			break;
		}
		
		return rel;
	}
	
	/**
	 * 转换扣项票扣标志状态信息为中文说明
	 * @param value
	 * @return
	 */
	private String statusForSettleMode(int value){
		if( value==0 ){
			return "交现";
		}else if( value ==1 ){
			return "帐扣";
		}else{
			return "未定义";
		}
	}
	
	/**
	 * 转换扣项帐扣标志状态信息为中文说明
	 * @param value
	 * @return
	 */
	private String statusInvoiceMode(int value){
		if( value==0 ){
			return "非票扣";
		}else if( value ==1 ){
			return "票扣";
		}else{
			return "未定义";
		}
	}
	
	/**
	 * 查询符合查询条件的数目
	 * @param sql_where 查询条件
	 * @return 符合查询条件的数目
	 * @throws SQLException
	 */
	private int getCount(String sql_where ) throws SQLException{
		int count =0 ;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_count + sql_where);
		if( rs.next() ) count = rs.getInt(1);
		rs.close();
		stmt.close();
		
		return count;
	}
	
	public int cookExcelFile(Map map, File file) throws SQLException, InvalidDataException, IOException{
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		
		int count = getCount(sql_where);
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		
		String sql_sel = " select " +
		" c.noteno, c.venderid, c.chargecodeid,cc.chargename,c.chargeamt, b.bookname, s.shopname, c.majorid, cg.categoryname, " +
		" c.docdate, c.reckoningdate, c.noteremark, c.settlemode, c.invoicemode, c.flag, c.buyer " +
		" from chargesum0 c " +
		" inner join shop s on (s.shopid = c.shopid ) " +
		" inner join book b on (b.bookno = c.bookno )  " +
		" inner join chargecode cc on (cc.chargecodeid = c.chargecodeid ) " +
		" left  join category cg on (cg.categoryid = c.majorid) ";
				
		String[] title = {"单据号","供应商","扣项代码","扣项名称","扣项金额","分公司","门店名","品类编码","品类名", 
				"制单日期","财务确认日期","扣项说明","帐扣属性","票扣属性","扣项状态","业务员"};
		
		Statement stmt = conn.createStatement();
		PreparedStatement pstmt = conn.prepareStatement( sql_sel+sql_where );
		ResultSet rs = pstmt.executeQuery();
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "扣项" );
		
		rs.close();
		pstmt.close();
		stmt.close();
		
		return rows;
	}
	/**
	 * 拼sql语句过滤条件
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 */
	private Filter cookFilter( Map map ) throws InvalidDataException{
		Filter filter = new Filter();
		String[] ss = null;

        ss = (String [] ) map.get( "venderid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " c.venderid = "+Values.toString4String(ss[0]));
        }
        
        ss = (String [] ) map.get( "bookno" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " c.bookno IN ("+new Values(ss).toString4String()+") ");
        }
        
        ss = (String [] ) map.get( "shopid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " c.shopid IN ("+new Values(ss).toString4String()+ ") ");
        }
        
        ss = (String [] ) map.get( "sdate_min" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " trunc(c.ReckoningDate) >= "+ValueAdapter.std2mdy( ss[0] ));
        }
        
        ss = (String [] ) map.get( "sdate_max" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " trunc(c.ReckoningDate) <= "+ValueAdapter.std2mdy( ss[0] ));
        }
        
        
        filter.add(" c.flag=1 ");
        
        return filter;
	}
	
	
	final private String sql_count = " select COUNT(*) from chargesum0 c ";
	
	final private Connection conn;
	
}
