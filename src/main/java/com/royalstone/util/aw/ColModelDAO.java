package com.royalstone.util.aw;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

public class ColModelDAO {
	final private Connection conn;

	public ColModelDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	public String makeCMID(int moduleid) throws SQLException {
		String res = "";
		String sql = " select max(cmid) from cmdefinition where moduleid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, moduleid);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			long tmp = rs.getLong(1);
			if (tmp == 0) {
				res = String.valueOf(moduleid * 1000L);
			} else {
				res = String.valueOf(tmp + 1);
			}
		} else {
			res = String.valueOf(moduleid * 1000L);
		}
		rs.close();
		ps.close();
		return res;
	}

	public String loadDefCmid(int moduleid) {
		String sql = " select nvl(min(cmid),0) from cmdefinition where moduleid=?";
		String s = SqlUtil.queryPS4SingleColumn(conn, sql, moduleid).get(0);
		return s;
	}

	public Element getCMByModuleid(int moduleid) {
		try {
			String sql = " select * from cmdefinition where moduleid=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, moduleid);
			ResultSet rs = ps.executeQuery();

			XResultAdapter adapter = new XResultAdapter(rs);
			Element elm;

			elm = adapter.getRowSetElement("cmdefinition", "row");

			rs.close();
			ps.close();
			return elm;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addHead(ColModel cm) throws SQLException {
		String sql = "insert into cmdefinition(cmid,moduleid,smid,hsmid,bsmid,note,title,footer,xslview,xslprint) "
				+ " values (?,?,?,?,?,?,?,?,?,?)";
		Object[] objs = { cm.getCmid(), cm.getModuleid(), cm.getSmid(),
				cm.getHsmid(), cm.getBsmid(), cm.getNote(), cm.getTitle(),
				cm.getFooter(), cm.getXslView(), cm.getXslPrint() };
		SqlUtil.executePS(conn, sql, objs);
	}

	public void delHead(String cmid) throws SQLException {
		String sql = "delete from cmdefinition where cmid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, cmid);
		ps.executeUpdate();
		ps.close();
	}

	public void addCMDetail(List<ColModelDetail> list) throws SQLException {
		for (Iterator<ColModelDetail> it = list.iterator(); it.hasNext();) {
			ColModelDetail cmd = it.next();
			addCMDetail(cmd);
		}
	}

	public void addCMDetail(ColModelDetail cmd) throws SQLException {
		String sql = "insert into cmdefinitiondetail(cmid,seqno,field,name,valuetype,width,sum,css,render,note) "
				+ " values (?,?,?,?,?,?,?,?,?,?)";
		Object[] objs = { cmd.getCmid(), cmd.getSeqno(), cmd.getField(),
				cmd.getName(), cmd.getVtype(), cmd.getWidth(), cmd.getSum(),
				cmd.getCss(), cmd.getRender(), cmd.getNote() };
		SqlUtil.executePS(conn, sql, objs);
	}

	public void delAllCMDetail(String cmid) throws SQLException {
		String sql = "delete from cmdefinitiondetail where cmid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, cmid);
		ps.executeUpdate();
		ps.close();
	}

	public void delCMDetail(ColModelDetail cmd) throws SQLException {
		String sql = "delete from cmdefinitiondetail where cmid=? and seqno=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, cmd.getCmid());
		ps.setInt(2, cmd.getSeqno());
		ps.executeUpdate();
		ps.close();
	}

	public List<ColModel> loadColModel() throws SQLException {
		List<ColModel> arrList = new ArrayList<ColModel>();
		String sql = " select * from cmdefinition";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			ColModel cm = new ColModel();
			String cmid = rs.getString("cmid");
			long moduleid = rs.getLong("moduleid");
			String smid = rs.getString("smid");
			String hsmid = rs.getString("hsmid");
			String bsmid = rs.getString("bsmid");
			String title = rs.getString("title");
			String footer = rs.getString("footer");
			String note = rs.getString("note");
			String xslPrint = rs.getString("xslprint");
			String xslView = rs.getString("xslview");
			cm.setCmid(cmid);
			cm.setModuleid(moduleid);
			cm.setTitle(SqlUtil.fromLocal(title));
			cm.setFooter(SqlUtil.fromLocal(footer));
			cm.setNote(SqlUtil.fromLocal(note));
			cm.setSmid(smid);
			cm.setHsmid(hsmid);
			cm.setBsmid(bsmid);
			cm.setXslPrint(xslPrint);
			cm.setXslView(xslView);
			cm.setCmDetailList(cookColModelDetail(cmid));
			cm.setSearchList(cookColModelSearch(cmid));
			arrList.add(cm);
		}
		rs.close();
		st.close();

		return arrList;
	}

	private List<ColModelDetail> cookColModelDetail(String cmid)
			throws SQLException {
		List<ColModelDetail> list = new ArrayList<ColModelDetail>();
		PreparedStatement ps = conn
				.prepareStatement("select * from cmdefinitiondetail where cmid=? order by seqno");
		ps.setString(1, cmid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ColModelDetail cmd = new ColModelDetail();
			cmd.setCmid(rs.getString("cmid"));
			cmd.setSeqno(rs.getInt("seqno"));
			cmd.setField(rs.getString("field"));
			cmd.setName(SqlUtil.fromLocal(rs.getString("name")));
			cmd.setVtype(rs.getString("valuetype"));
			cmd.setWidth(rs.getString("width"));
			cmd.setSum(rs.getInt("sum"));
			cmd.setCss(rs.getString("css"));
			cmd.setRender(rs.getString("render"));
			list.add(cmd);
		}
		rs.close();
		ps.close();
		return list;
	}

	private List<ColModelSearch> cookColModelSearch(String cmid)
			throws SQLException {
		List<ColModelSearch> list = new ArrayList<ColModelSearch>();
		PreparedStatement ps = conn
				.prepareStatement("select * from cmdefinitionsearch where cmid=? order by seqno");
		ps.setString(1, cmid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ColModelSearch cs = new ColModelSearch();
			cs.setCmid(rs.getInt("cmid"));
			cs.setSeqno(rs.getInt("seqno"));
			cs.setTableName(rs.getString("tablename"));
			cs.setField(rs.getString("field"));
			cs.setName(SqlUtil.fromLocal(rs.getString("name")));
			cs.setVtype(rs.getInt("vtype"));
			cs.setInputType(rs.getInt("inputtype"));
			cs.setIsnull(rs.getInt("isnull"));
			cs.setCompare(rs.getInt("compare"));
			cs.setDefaultValue(SqlUtil.fromLocal(rs.getString("defaultvalue")));
			cs.setWidth(rs.getString("width"));
			cs.setCss(rs.getString("css"));
			cs.setOptionSql(rs.getString("optionsql"));
			list.add(cs);
		}
		rs.close();
		ps.close();
		return list;
	}
}
