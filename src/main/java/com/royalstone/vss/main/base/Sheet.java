package com.royalstone.vss.main.base;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.aw.CMService;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.aw.XColModelAdapter;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlMapUtil;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * @author baij
 *         所有单据类和列表类继承
 */
public abstract class Sheet extends MainService implements ISheet {
	protected final String	cmid;
	protected String		catTableName;
	protected String		title;
	protected String		logo;
	protected String		xslView		= null;
	protected String		xslPrint	= null;

	public Sheet(HttpServletRequest request, Connection conn, Token token)  {
		super(request, conn, token);
		this.cmid = request.getParameter("cmid");
		title = token.site.getTitle();
		logo = token.site.getLogo();
	}

	/**
	 * 检查单据供应商列是否与当前token供应商编码一直
	 * 
	 * @param sheetid
	 * @param token
	 * @return
	 */
	protected boolean checkVenderid(String sheetid, Token token) {
		if (token.isVender) {
			String venderid = getVenderid(sheetid);
			if (venderid == null) {
				throw new DAOException("单据：" + sheetid + "不存在，可能已删除");
			}
			if (!venderid.equals(token.getBusinessid())) {
				throw new PermissionException("您的业务ID:" + token.getBusinessid() + " 无权查看单据:" + sheetid);
			}
		}
		return true;
	}

	/*
	 * 初始化cm模型
	 * @see com.royalstone.vss.main.base.ISheet#cminit()
	 */
	public Element cminit() {
		String cmid = request.getParameter("cmid");
		CMService service = new CMService(conn, token, cmid);
		try {
			return service.initHtml();
		} catch (Exception e) {
			throw new RuntimeException("初始化失败:" + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.royalstone.vss.main.base.ISheet#doConfirm()
	 */
	public Element doConfirm() {
		String sheetid = getParamNotNull("sheetid");
		doConfirm(sheetid);
		return new Element("OK");
	}

	protected int doConfirm(String sheetid) {
		if (catTableName == null) {
			throw new InvalidDataException("需指定cat记录表.");
		}
		String sql = " update " + catTableName
				+ " set status=?,confirmtime=sysdate where sheetid=? and status in (0,1)";
		return SqlUtil.executePS(conn, sql, new Object[] { 10, sheetid });
	}

	/*
	 * (non-Javadoc)
	 * @see com.royalstone.vss.main.base.ISheet#doRead()
	 */
	public Element doRead() {
		String sheetid = getParamNotNull("sheetid");
		doRead(sheetid);
		return new Element("OK");
	}

	protected int doRead(String sheetid) {
		if (catTableName == null) {
			throw new InvalidDataException("需指定cat记录表.");
		}
		String sql = " update " + catTableName + " set status=?,readtime=sysdate where sheetid=? and status=0";
		return SqlUtil.executePS(conn, sql, new Object[] { 1, sheetid });
	}

	/* (non-Javadoc)
	 * @see com.royalstone.vss.main.base.MainService#excel(java.io.File, java.lang.String)
	 */
	public String excel(File file, String cmid) {
		ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
		String smid = cm.getSmid();
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(sid, smid);
		String sql_where = cookFilter(getParams()).toString();
		try {
			int count = SqlMapUtil.getCount(conn, sqlUnit, sql_where);
			if (count > VSSConfig.getInstance().getExcelLimitSoft()) {
				throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");
			}

			ResultSet rs = SqlUtil.querySQL(conn, sqlUnit.toString(sql_where));
			com.royalstone.workbook.Workbook.writeToFile(file, rs, cm.getCmDetailList(), "导出.xls");
			SqlUtil.close(rs);
			return cm.getTitle();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * 获取表体信息，仅适合一主一从关系，如果是多表体，需重载show 方法
	 * 
	 * @param sheetid
	 * @return
	 */
	protected Element getBody(String sheetid) {
		SqlUnit unit4Body = SqlMapLoader.getInstance().getSql(sid,
				ColModelLoader.getInstance().getCM(sid, cmid).getBsmid());
		return SqlUtil.getRowSetElement(conn, unit4Body.toString(), sheetid, "body");
	}

	/**
	 * 获取查询结果集总数。
	 * 内部计算 select count(*) from table
	 * 
	 * @param filter
	 * @return
	 */
	protected int getCount(SqlFilter filter) {
		ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
		if (cm == null) {
			throw new DAOException("尚未定义显示模型");
		}
		SqlUnit unit4Search = SqlMapLoader.getInstance().getSql(sid, cm.getSmid());
		return SqlMapUtil.getCount(conn, unit4Search, filter.toString());
	}

	/**
	 * 取单据表头
	 * 
	 * @param sheetid
	 * @return
	 */
	protected Element getHead(String sheetid) {
		SqlUnit unit4Head = SqlMapLoader.getInstance().getSql(sid,
				ColModelLoader.getInstance().getCM(sid, cmid).getHsmid());
		return SqlUtil.getRowSetElement(conn, unit4Head.toString(), sheetid, "head");
	}

	/**
	 * 获取单据对应venderid列数据
	 * 
	 * @param sheetid
	 * @return
	 */
	protected String getVenderid(String sheetid) {
		SqlUnit unit4Search = SqlMapLoader.getInstance().getSql(sid,
				ColModelLoader.getInstance().getCM(sid, cmid).getSmid());
		String sqlFrom = unit4Search.getSqlFrom();
		// 取from后面第一个表名
		sqlFrom = sqlFrom.replaceFirst("FROM ", "");
		String[] ss = sqlFrom.split(" ");
		for (int i = 0; i < ss.length; i++) {
			if (i > 0 && !ss[i].equals(" ")) {
				sqlFrom = ss[i];
				break;
			}
		}
		return getVenderid(sheetid, sqlFrom);
	}

	/**
	 * 获取单据对应venderid列数据
	 * 
	 * @param sheetid
	 * @param tableName
	 * @return 当数据返回行数为0时，返回null
	 */
	protected String getVenderid(String sheetid, String tableName) {
		String sql = " select venderid from " + tableName + " where sheetid=?";
		List<String> arr = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		String rel = null;
		if (arr.size() > 0) {
			rel = arr.get(0);
		}
		return rel;
	}

	/**
	 * 设置内部变量数值
	 * setSheetInfo 方法调用，如果需要指定相关信息，需重载该方法
	 * 
	 * @param sheetid
	 */
	protected void initInnerValue(String sheetid) {

	}

	/*
	 * (non-Javadoc)
	 * @see com.royalstone.vss.main.base.ISheet#print()
	 */
	public Element print() {
		return show();
	}

	/*
	 * (non-Javadoc)
	 * @see com.royalstone.vss.main.base.ISheet#search()
	 */
	public Element search() {
		Map<String, String[]> parms = getParams();
		SqlFilter filter = cookFilter(parms);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);

		if (count == 0) {
			// throw new InvalidDataException("没有记录.");
		}

		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql_where = cookFilter(parms).toString();
		SqlUnit unit4Search = SqlMapLoader.getInstance().getSql(sid,
				ColModelLoader.getInstance().getCM(sid, cmid).getSmid());
		Element elm_cat = XColModelAdapter.getAwReportRowSetElement(conn, sid, unit4Search.toString(sql_where), cmid,
				1, VSSConfig.getInstance().getRowsLimitSoft());
		elm_cat.setAttribute("row_total", "" + count);
		return elm_cat;
	}

	/**
	 * 设置单据格式,LOGO,标题等信息
	 * 
	 * @param sheetid
	 * @return
	 */
	protected Element setSheetInfo(String sheetid) {
		initInnerValue(sheetid);
		ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
		if (cm != null) {
			if (xslView == null)
				xslView = "../xsl/" + cm.getXslView();
			if (xslPrint == null)
				xslPrint = "../xsl/" + cm.getXslPrint();
		}
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("title", title);
		elm_sheet.setAttribute("logo", logo);
		elm_sheet.setAttribute("xsl", xslView);
		elm_sheet.setAttribute("xslprint", xslPrint);
		return elm_sheet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.royalstone.vss.main.base.ISheet#show()
	 */
	public Element show() {
		String sheetid = request.getParameter("sheetid");
		checkVenderid(sheetid, token);
		Element elm_sheet = setSheetInfo(sheetid);
		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getBody(sheetid));
		return elm_sheet;
	}
}
