package com.royalstone.vss.net;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

public class NetOrderTime {
	final Connection	conn;
	final Token			token;
	final int			site;
	
	public NetOrderTime(Connection conn, Token token) {
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
		
		ss = (String[]) map.get("starttime");
		if (ss != null && ss.length > 0) {
			filter.add(" starttime = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("endtime");
		if (ss != null && ss.length > 0) {
			filter.add(" endtime = " + Values.toString4String(ss[0]));
		}
		return filter;
	}
	
	
	public Element getOrderTime(Map parms){
		// 获取sql语句
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,"6000002000");
		String sql_where = cookFilter(parms).toString();
		String sql = sqlUnit.toString(sql_where);
		Element elm = SqlUtil.getRowSetElement(conn, sql, "netnetordertime", 1, VSSConfig.getInstance().getRowsLimitSoft());
		return elm;
	}
	
	public void del(String dccode,String logistics,String starttime,String endtime) throws SQLException{
		String sql = "delete from netordertime where dccode=? and starttime=? and endtime=? and logistics=?" ;
		SqlUtil.executePS(conn, sql, new Object[] {dccode,starttime,endtime,logistics});
	}
	
	public void add(String dccode,String logistics,String starttime,String endtime,String timejg,String maxsku,String maxxs,String maxsupply,String maxdzsupply,String maxyssupply,String note,String username) throws ParseException{
		String sql = " INSERT INTO NETORDERTIME(DCCODE,logistics,STARTTIME,ENDTIME,TIMEJG,MAXSKU,MAXXS,MAXSUPPLY,MAXDZSUPPLY,MAXYSSUPPLY,NOTE,INPUTER,INPUTDATE)" +
		" values(?,?,?,?,?,?,?,?,?,?,?,?,sysdate) ";
		SqlUtil.executePS(conn, sql, new Object[] {dccode,logistics,starttime,endtime,timejg,Integer.parseInt(maxsku),Integer.parseInt(maxxs),Integer.parseInt(maxsupply),Integer.parseInt(maxdzsupply),Integer.parseInt(maxyssupply),note,username});
	}
	public void upd(String dccode,String logistics,String starttime,String endtime,String timejg,String maxsku,String maxxs,String maxsupply,String maxdzsupply,String maxyssupply,String note,String username)throws ParseException{
		String sql = " UPDATE NETORDERTIME SET TIMEJG=?,MAXSKU=?,MAXXS=?,MAXSUPPLY=?,MAXDZSUPPLY=?,MAXYSSUPPLY=?,NOTE=?,UPPER=?,UPPDATE=sysdate WHERE DCCODE=? AND STARTTIME=? AND ENDTIME=? AND logistics=?";
		SqlUtil.executePS(conn, sql, timejg,Integer.parseInt(maxsku),Integer.parseInt(maxxs),Integer.parseInt(maxsupply),Integer.parseInt(maxdzsupply),Integer.parseInt(maxyssupply),note,username,dccode,starttime,endtime,logistics);
	}
}
