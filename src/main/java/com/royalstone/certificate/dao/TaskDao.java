package com.royalstone.certificate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.DbAdm;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

public class TaskDao {
	final private Connection	conn;
	final String				venderid;

	public TaskDao(Connection conn, String venderid) {
		super();
		this.conn = conn;
		this.venderid = venderid;
	}

	/**
	 * 取得各个状态证照数目
	 * @return
	 * @throws SQLException
	 * @throws SQLException
	 */
	public int getCertificateCount(int flag, String venderid) throws SQLException {
		int num = 0;
		Values val_vender = new Values(venderid);
		String sql = " select count(*) from certificate c "
				+ " join certificateitem i on i.sheetid=c.sheetid   where c.venderid IN ("
				+ val_vender.toString4String() + ") and i.flag=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, flag);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) num = rs.getInt(1);
		rs.close();
		pstmt.close();
		return num;
	}

	private void initData(String tableName) throws SQLException {

		String sql = " select distinct vr.venderid,vr.ctid,ct.ctname,ct.yearflag,ct.note,vr.ccid,nvl(cc.ccname,'') ccname,nvl(c.sheetid,'') sheetid,nvl(c.vendertype,'') vendertype,nvl(c.vendertypename,'') vendertypename,c.type "
				+ " from vendercategoryrelation vr "
				+ " join certificatetype ct on (ct.ctid=vr.ctid) "
				+ " left join certificatecategory cc on (cc.ccid=vr.ccid) "
				+ " left join certificate c on (c.venderid=vr.venderid and c.ccid=vr.ccid) "
				+ " left join certificateitem ci on (ci.sheetid=c.sheetid  )  "
				+ " where vr.venderid=? and vr.ctid not in ( "
				+ " select ci.ctid from vendercategoryrelation vr "
				+ " join certificate c on (c.venderid=vr.venderid and c.ccid=vr.ccid) "
				+ " join certificateitem ci on (ci.sheetid=c.sheetid and vr.ctid=ci.ctid )  ) into temp  "
				+ tableName;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, venderid);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 获得需要填写的证照列表
	 * 
	 * @return
	 * @return
	 * @throws SQLException
	 */
	public Element getTaskList() throws SQLException {
		// 查找需要录入
		String tableName = DbAdm.getTmpName();
		initData(tableName);
		Element elm_set = new Element("group");

		String sqlgroup = "select ccid,ccname from " + tableName + " group by ccid,ccname order by ccid";
		String sql = "select * from " + tableName + " where ccid=? order by ctid";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		PreparedStatement ps = conn.prepareStatement(sqlgroup);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int ccid = rs.getInt(1);
			String ccname = rs.getString(2);
			pstmt.setInt(1, ccid);
			ResultSet res = pstmt.executeQuery();
			XResultAdapter adapter = new XResultAdapter(res);
			Element elm_row = adapter.getRowSetElement("task", "row");
			elm_row.setAttribute("ccid", ccid + "");
			elm_row.setAttribute("ccname", SqlUtil.fromLocal(ccname.trim()));

			elm_set.addContent(elm_row);
			res.close();
		}

		rs.close();
		ps.close();
		DbAdm.dropTable(conn, tableName);
		return elm_set;
	}

	public Element getVenderCertificateList() throws SQLException{
		//判断是否有该供应商
		String sql0 = "select count(*) from vender where venderid=?";
		//基本证照
		String sql1 = "select ct.ctid,ct.ctname,ct.note,ct.yearflag from certificateType ct where flag=0";
		//品类
		String sql2 = "select cc.ccid,cc.ccname from certificateCategory cc ";
		
		//检验类
		String sql3 = "select ct.ctid,ct.ctname,ct.note,ct.yearflag from certificateType ct where flag=2";
		
		String sqlcount = "select count(*) from venderCategoryRelation where venderid=? and ccid>0";
		
		String sqlct = " select ct.ctid,ct.ctname,ct.note,ct.yearflag from certificateRelation cr " +
				" join certificateType ct on (ct.ctid=cr.ctid and ct.flag=1) " +
				" where cr.ccid=?";
		
		ResultSet rs = SqlUtil.queryPS(conn, sql0, venderid);
		if(rs.next() && rs.getInt(1)>0){
			
		}else{
			throw new InvalidDataException("没有这个供应商："+venderid);
		}
		SqlUtil.close(rs);
		
		
		rs = SqlUtil.queryPS(conn, sqlcount, venderid);
		//如果没有配置品类关系，则默认显示全部
		if(rs.next()){
			if(rs.getInt(1)>0){
				sql2 = " select distinct cc.ccid,cc.ccname from venderCategoryRelation vr" +
				" join certificateCategory cc on cc.ccid=vr.ccid " +
				" where vr.venderid='"+venderid+"'";
			}
		}
		SqlUtil.close(rs);
		
		
		Element elm = new Element("list");
		
		rs = SqlUtil.querySQL(conn, sql1);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_type1 = adapter.getRowSetElement("type1", "row");
		
		SqlUtil.close(rs);
		
		
		Element elm_type2 = new Element("type2");
		rs = SqlUtil.querySQL(conn, sql2);
		PreparedStatement ps = conn.prepareStatement(sqlct);
		while(rs.next()){
			int ccid = rs.getInt(1);
			String ccname = rs.getString(2);
			ps.setInt(1, ccid);
			ResultSet res = ps.executeQuery();
			adapter = new XResultAdapter(res);
			Element elm_row = adapter.getRowSetElement("cc", "row");
			elm_row.setAttribute("ccid", ccid + "");
			elm_row.setAttribute("ccname", SqlUtil.fromLocal(ccname.trim()));

			elm_type2.addContent(elm_row);
			res.close();
		}
		SqlUtil.close(rs);
		
		rs = SqlUtil.querySQL(conn, sql3);
		adapter = new XResultAdapter(rs);
		Element elm_type3 = adapter.getRowSetElement("type3", "row");
		SqlUtil.close(rs);
		
		elm.addContent(elm_type1);
		elm.addContent(elm_type2);
		elm.addContent(elm_type3);
		return elm;
	}
}