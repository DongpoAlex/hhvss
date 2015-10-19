package com.royalstone.vss.net;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
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

/**
 * 大宗供应商
 * @author lxm
 */
public class NetLargeVender {
	final Connection	conn;
	final Token			token;
	final int			site;
	
	public NetLargeVender(Connection conn, Token token) {
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
			filter.add(" a.dccode = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("vendcode");
		if (ss != null && ss.length > 0) {
			filter.add(" a.vendcode = " + Values.toString4String(ss[0]));
		}
		return filter;
	}
	
	public Element getVenderList(Map parms){
		
		// 获取sql语句
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,"6000003000");
		String sql_where = cookFilter(parms).toString();
		String sql = sqlUnit.toString(sql_where);
		Element elm = SqlUtil.getRowSetElement(conn, sql, "netlargevender", 1, VSSConfig.getInstance().getRowsLimitSoft());
		return elm;
	}
	
	public void save(String dccode,String vendcode,String vendtype,String note,String username) throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sql = " INSERT INTO NETLARGERVENDER(DCCODE,VENDCODE,VENDTYPE,OPERATERTYPE,ISVALID,NOTE,INPUTER,INPUTDATE)" +
		" values(?,?,?,?,?,?,?,?) ";
		String isvalid="Y";
		String opertetype="0";
		String currenttime = df.format(new Date());   
		Date inputdate = df.parse(currenttime);    
//		System.out.println("note"+note);
		SqlUtil.executePS(conn, sql, new Object[] {dccode,vendcode,vendtype,opertetype,isvalid,note,username,inputdate});
	}
	
	public void del(String dccode,String vendcode,String username){
		String sql = " UPDATE NETLARGERVENDER SET ISVALID='N',OPERATERTYPE='1',UPPER=?,UPPDATE=? WHERE DCCODE=? AND VENDCODE=? " ;
		SqlUtil.executePS(conn, sql,username,new Date(),dccode,vendcode);
	}
	
	public void upd(String dccode,String vendcode,String vendtype,String note,String username){
		String sql = " UPDATE NETLARGERVENDER SET ISVALID='Y',OPERATERTYPE='2',NOTE=?,VENDTYPE=?,UPPER=?,UPPDATE=? WHERE DCCODE=? AND VENDCODE=? " ;
		SqlUtil.executePS(conn, sql,note,vendtype,username,new Date(),dccode,vendcode);
	}
}
