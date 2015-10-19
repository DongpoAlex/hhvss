package com.royalstone.vss.edi;

import java.sql.Connection;
import java.sql.SQLException;
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
 * @author 白剑 维护edi_vender表及查询
 */
public class EDIVender {
	final Connection	conn;
	final Token			token;
	final int			site;

	public EDIVender(Connection conn, Token token) {
		super();
		this.conn = conn;
		this.token = token;
		site = token.site.getSid();
	}

	public Element getVenderList(Map parms) {

		// 获取sql语句
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,"9000015000");
		String sql_where = cookFilter(parms).toString();
		String sql = sqlUnit.toString(sql_where);
		Element elm = SqlUtil.getRowSetElement(conn, sql, "rowset", 1, VSSConfig.getInstance().getRowsLimitSoft());
		return elm;
	}
	
	public void save(String venderid, String[] seqs) throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn.setAutoCommit(false);
			delVender(venderid);
			if(seqs!=null){
				addVender(venderid);
				for (int i = 0; i < seqs.length; i++) {
					String str = seqs[i];
					String[] ss = str.split(",");
					if(ss.length==2){
						Date startDate=df.parse(ss[1]);
						addItem(venderid,ss[0],startDate);
					}
				}
			}
			conn.commit();
		}
		catch (SQLException e) {
			try {
				conn.rollback();
			}
			catch (SQLException e1) {}
			throw e;
		}
	}

	/**
	 * @param venderid
	 * @param seqs
	 * @throws Exception
	 */
	private void addVender(String venderid) throws Exception {
		String sql = SqlMapLoader.getInstance().getSql(site, "9000015001").toString();
		SqlUtil.executePS(conn, sql, new String[] { venderid });
	}
	private void delVender(String venderid){
		String sql = SqlMapLoader.getInstance().getSql(site, "9000015004").toString();
		SqlUtil.executePS(conn, sql, new String[] { venderid });
		delItem(venderid,"1");
		delItem(venderid,"2");
		delItem(venderid,"3");
	}

	public void addItem(String venderid,String seq, Date startDate){
		String sqlitem = SqlMapLoader.getInstance().getSql(site, "9000015002").toString();
		if (seq.equals("1")) {
			SqlUtil.executePS(conn, sqlitem, new Object[] { venderid, seq, "Purchase", "", "ORDER",startDate });
		} else if (seq.equals("2")) {
			SqlUtil.executePS(conn, sqlitem, new Object[] { venderid, seq, "Receipt", "", "YS",startDate });
		} else if (seq.equals("3")) {
			SqlUtil.executePS(conn, sqlitem, new Object[] { venderid, seq, "Checking", "DZSQ", "DZJG",startDate });
		}
	}
	
	private void delItem(String venderid,String seq){
		String sql = SqlMapLoader.getInstance().getSql(site, "9000015003").toString();
		SqlUtil.executePS(conn, sql, new String[] { venderid, seq});
	}
	
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}
		return filter;
	}
}
