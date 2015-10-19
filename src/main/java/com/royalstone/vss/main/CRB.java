package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.aw.XColModelAdapter;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlMapUtil;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;
import com.royalstone.vss.main.base.Sheet;

/**
 * @author baij
 *         供应商融资金额
 */
public class CRB extends Sheet {

	public CRB(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return null;
	}

	public Element search() {

		String venderid = token.getBusinessid();
		String buid = this.getParamNotNull("buid");

		int count = getCount(venderid, buid);

		SqlUnit unit4Search = SqlMapLoader.getInstance().getSql(sid,
				ColModelLoader.getInstance().getCM(sid, cmid).getSmid());
		Element elm_cat = XColModelAdapter.getAwReportRowSetElement(conn, sid, unit4Search.toString(), cmid, 1,
				VSSConfig.getInstance().getRowsLimitSoft(), venderid, buid);
		elm_cat.setAttribute("row_total", "" + count);
		return elm_cat;
	}

	private int getCount(String venderid, String buid) {
		ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
		if (cm == null) {
			throw new DAOException("尚未定义显示模型");
		}
		SqlUnit unit4Search = SqlMapLoader.getInstance().getSql(sid, cm.getSmid());
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, unit4Search.toString(), venderid, buid);
		return list.size();
	}
}
