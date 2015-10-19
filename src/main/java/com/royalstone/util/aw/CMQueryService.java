package com.royalstone.util.aw;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlMapUtil;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

public abstract class CMQueryService implements ICMQueryService {
	final protected Connection	conn;
	final protected Token		token;

	public CMQueryService(Connection conn, Token token) {
		super();
		this.conn = conn;
		this.token = token;
	}

	public Element load(String cmid, Map parms) {

		// 获取sql语句
		int site = token.site.getSid();
		String smid = ColModelLoader.getInstance().getCM(site, cmid).getSmid();
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, smid);

		String sql_where = cookFilter(parms).toString().trim();

		Element elm = null;
		int count;
		count = SqlMapUtil.getCount(conn, sqlUnit, sql_where);

		if (count > VSSConfig.getInstance().getRowsLimitHard()) { throw new InvalidDataException(
				"满足条件的记录数已超过系统处理上限,请重新设置查询条件."); }

		elm = XColModelAdapter.getAwReportRowSetElement(conn, token.site.getSid(), sqlUnit
				.toString(sql_where), cmid, 1, VSSConfig.getInstance().getRowsLimitSoft());
		elm.addContent(new Element("totalCount").addContent(count + ""));
		return elm;
	}

	public void excel(File file, String cmid, Map parms) {
		int site = token.site.getSid();
		ColModel cm = ColModelLoader.getInstance().getCM(site, cmid);
		String smid = cm.getSmid();
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, smid);
		
		String sql_where = cookFilter(parms).toString();
		try {
			int count = SqlMapUtil.getCount(conn, sqlUnit, sql_where);
			if (count > VSSConfig.getInstance().getExcelLimitSoft()) { throw new InvalidDataException(
					"满足条件的记录数已超过系统处理上限,请重新设置查询条件."); }

			ResultSet rs = SqlUtil.querySQL(conn, sqlUnit.toString(sql_where));
			com.royalstone.workbook.Workbook.writeToFile(file, rs, cm.getCmDetailList(), "导出.xls");
			SqlUtil.close(rs);
		}
		catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
