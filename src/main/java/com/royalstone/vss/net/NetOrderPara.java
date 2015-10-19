package com.royalstone.vss.net;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;

public class NetOrderPara {

	final Connection	conn;
	final Token			token;
	final int			site;
	
	public NetOrderPara(Connection conn, Token token) {
		super();
		this.conn = conn;
		this.token = token;
		site = token.site.getSid();
	}
	
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;
		ss = (String[]) map.get("dccode");
		if (ss != null && ss.length > 0) {
			filter.add(" dccode = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("logistics");
		if (ss != null && ss.length > 0) {
			filter.add(" logistics = " + Values.toString4String(ss[0]));
		}
		return filter;
	}
	
	public Element getOrderParam(String dccode){
		String sql_where="";
		if(dccode!=null){
			sql_where = " and dccode ='"+dccode+"'";
		}
		String sql = " select dccode,isyesps,ordertime,orderkfts,stoporderdate,orderlastdate,ordernote,note,upper,uppdate,inputer,inputdate,dzsku,dzpqty from netorderpara where 1=1 "+ sql_where;
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netnetorderparam");
		return elm_cat;
	}
	
	public Element getOrderParamLastdate(String dccode) throws SQLException {
		String sql = "  select orderlastdate from netorderpara WHERE dccode='"+ dccode+"'" ;
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netparamlastdate");
		return elm_cat;
	}
	
	
	public void add(String dccode,String isyesps,String ordertime,String orderkfts,String stoporderdate,String orderlastdate,String ordernote,String username, String dzsku, String dzpqty) throws SQLException, ParseException{

		String sql_ins = " INSERT INTO NETORDERPARA (DCCODE,ISYESPS,ORDERTIME,ORDERKFTS,STOPORDERDATE,ORDERLASTDATE,DZSKU,DZPQTY,ORDERNOTE,INPUTER,INPUTDATE) "
				+ " VALUES ( ?,?,?,?,?,?,?,?,?,?,sysdate ) ";
			SqlUtil.executePS(conn, sql_ins, new Object[] {dccode,isyesps,ordertime,Integer.parseInt(orderkfts),stoporderdate,orderlastdate,dzsku,dzpqty,ordernote,username});
	}
	public void del(String dccode) throws SQLException{
		String sql = "delete from NETORDERPARA where DCCODE=? " ;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, dccode);
		pstmt.executeUpdate();
		pstmt.close();
	}
	public void upd(String dccode,String isyesps,String ordertime,String orderkfts,String stoporderdate,String orderlastdate,String ordernote,String username, String dzsku, String dzpqty) throws SQLException{
		String sql = " UPDATE NETORDERPARA SET ISYESPS=?,ORDERTIME=?,ORDERKFTS=?,STOPORDERDATE=?,ORDERLASTDATE=?,ORDERNOTE=?,UPPER=?,dzsku=?,dzpqty=?,UPPDATE=sysdate WHERE DCCODE=? ";
		SqlUtil.executePS(conn, sql, new Object[] {isyesps,ordertime,Integer.parseInt(orderkfts),stoporderdate,orderlastdate,ordernote,username,dzsku,dzpqty,dccode});
	}
	
	
	/**
	 * 参数日期设置
	 * 
	 * @return
	 */
	
	public Element getOrderParamDate(String dccode,String logistics){
		String dccode_where="";
		String logistics_where="";
		if(dccode != null){
			dccode_where = " and dccode ='"+dccode+"'";
		}
		if(logistics!=null){
			logistics_where = " and logistics='"+logistics+"'";
		}
		String sql = " select dccode, logistics, monday, tuesday, wednesday, thursday, friday, saturday, sunday, note,upper, uppdate, inputer, inputdate from netorderparadate where 1=1 "+ dccode_where+logistics_where ;
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netorderparamdate");
		return elm_cat;
	}
	
	public void deldate(String dccode,String logistics)throws SQLException{
		String sql = "delete from netorderparadate where DCCODE=? and logistics=? " ;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, dccode);
		pstmt.setString(2, logistics);
		pstmt.executeUpdate();
		pstmt.close();
	}
	public void upddate(String dccode,String logistics,String monday,String tuesday,String wednesday,String thursday,String friday,String saturday,String sunday,String note,String username){
		String sql = " UPDATE netorderparadate SET monday=?,tuesday=?,wednesday=?,thursday=?,friday=?,saturday=?,sunday=?,note=?,upper=?,uppdate=sysdate WHERE DCCODE=? and logistics=? ";
		SqlUtil.executePS(conn, sql, new Object[] {monday,tuesday,wednesday,thursday,friday,saturday,sunday,note,username,dccode,logistics});
	}
	
	public void adddate(String dccode,String logistics,String monday,String tuesday,String wednesday,String thursday,String friday,String saturday,String sunday,String note,String username){
		String sql_ins = " insert into netorderparadate(dccode,logistics,monday,tuesday,wednesday,thursday,friday,saturday,sunday,note,inputer,inputdate) "
			+ " VALUES ( ?,?,?,?,?,?,?,?,?,?,?,sysdate ) ";
		SqlUtil.executePS(conn, sql_ins, new Object[] {dccode,logistics,monday,tuesday,wednesday,thursday,friday,saturday,sunday,note,username});
	}
}
