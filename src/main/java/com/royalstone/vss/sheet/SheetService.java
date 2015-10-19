package com.royalstone.vss.sheet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelDAO;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.aw.XColModelAdapter;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 2013-08-09 修改：默认方法增加sql多元化，比如group by 语句拼写
 * 
 * @author jasonbob
 * 
 */
public abstract class SheetService implements ISheetService {

	public SheetService(Connection conn, Token token, String cmid,
			String sql4Search, String sql4Head, String sql4Body,
			String tableName, String catTableName, String title) {
		super();
		this.conn = conn;
		this.token = token;
		this.cmid = cmid;
		this.sql4Search = sql4Search;
		this.sql4Head = sql4Head;
		this.sql4Body = sql4Body;
		this.tableName = tableName;
		this.catTableName = catTableName;
		this.title = title;
	}
	
	/**
	 * 修改 XDB
	 * 2013-08-09
	 * @param conn
	 * @param token
	 * @param cmid
	 * @param sql4Search
	 * @param sql4Head
	 * @param sql4Body
	 * @param tableName
	 * @param catTableName
	 * @param title
	 * @param sqlGroupbyStr sql 子句
	 */
	public SheetService(Connection conn, Token token, String cmid,
			String sql4Search, String sql4Head, String sql4Body,
			String tableName, String catTableName, String title,String sqlGroupbyStr) {
		super();
		this.conn = conn;
		this.token = token;
		this.cmid = cmid;
		this.sql4Search = sql4Search;
		this.sql4Head = sql4Head;
		this.sql4Body = sql4Body;
		this.tableName = tableName;
		this.catTableName = catTableName;
		this.title = title;
		this.sqlGroupbyStr=sqlGroupbyStr;
	}
	public SheetService(Connection conn, Token token, String cmid,
			int moduleid, String smidHead, String smidBody, String tableName) {
		super();
		// 如果cmid是0,则主动从cmd表中取默认的cmid
		if (cmid == null || cmid.length() == 0) {
			ColModelDAO dao = new ColModelDAO(conn);
			cmid = dao.loadDefCmid(moduleid);
		}
		if (cmid == null || cmid.equals("0"))
			throw new InvalidDataException("当前模块显示定义不可用，请与管理员联系");

		this.conn = conn;
		this.token = token;
		this.cmid = cmid;
		this.tableName = tableName;

		int sid = token.site.getSid();
		String smidSearch = ColModelLoader.getInstance().getCM(sid, cmid)
				.getSmid();

		sql4Search = null;
		unit4Search = SqlMapLoader.getInstance().getSql(sid, smidSearch);
		sql4Head = SqlMapLoader.getInstance().getSql(sid, smidHead).toString();
		sql4Body = SqlMapLoader.getInstance().getSql(sid, smidBody).toString();

		catTableName = catTableName == null ? "cat_" + tableName : catTableName;
	}

	final Connection conn;
	final Token token;
	final String cmid;
	final String sql4Search;
	final String sql4Head;
	final String sql4Body;
	private String sqlGroupbyStr =" ";
	private SqlUnit unit4Search;

	final String tableName;
	protected String catTableName;

	protected String title;
	protected String logo;
	protected String majorName;

	public boolean checkVenderid(String sheetid, Token token) {
		if (token.isVender) {
			String venderid = getVenderid(sheetid);
			if (venderid == null || !venderid.equals(token.getBusinessid())) {
				throw new PermissionException("您的业务ID:" + token.getBusinessid()
						+ " 无权查看单据:" + sheetid);
			}
		}
		return true;
	}

	public String getVenderid(String sheetid) {
		String sql = " select venderid from " + tableName
				+ " a where a.sheetid=?";
		List<String> arr = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		String rel = "";
		if (arr.size() > 0) {
			rel = arr.get(0);
		}
		return rel;
	}

	public int getCount(Filter filter) {
		// 如果cat表为空则不管cat表
		String sql;
		if (catTableName == null || catTableName.length() == 0) {
			sql = "SELECT count(*) from " + tableName + " a WHERE "
					+ filter.toString();
		} else {
			sql = "SELECT count(*) from " + catTableName + " cat join "
					+ tableName + " a on cat.sheetid=a.sheetid WHERE "
					+ filter.toString();
		}
		String temp = SqlUtil.querySQL4SingleColumn(conn, sql).get(0);
		int rows = 0;
		if (temp != null) {
			rows = Integer.parseInt(temp);
		}
		return rows;
	}

	public Element search(Map<String, String[]> parms) {
		Filter filter = cookFilter(parms);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count == 0) {
			Element elm = new Element("rowset");
			elm.setAttribute("row_total", "0");
			return elm;
		}

		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql_where = cookFilter(parms).toString();
		
		//sql语句重写
		String sql = sql4Search + " WHERE " + sql_where+sqlGroupbyStr;
		
		
		Element elm_cat;
		// 当cmid为空时调用
		if ((this.cmid == null || this.cmid.length() == 0)
				&& sql4Search != null) {
			elm_cat = SqlUtil.getRowSetElement(conn, sql, "rowset");
		} else {
			// 当cmid>0时调用
			elm_cat = XColModelAdapter.getAwReportRowSetElement(conn,
					token.site.getSid(), unit4Search.toString(sql_where), cmid,
					1, VSSConfig.getInstance().getRowsLimitSoft());
		}
		elm_cat.setAttribute("row_total", "" + count);
		return elm_cat;
	}

	public Element getHead(String sheetid) {
		return SqlUtil.getRowSetElement(conn, sql4Head, sheetid, "head");
	}

	public Element getBody(String sheetid) {
		return SqlUtil.getRowSetElement(conn, sql4Body, sheetid, "body");
	}

	public Element veiw(String sheetid) {
		checkVenderid(sheetid, token);
		setPrintInfo(sheetid);
		Element elm_sheet = setSheetInfo(sheetid);
		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getBody(sheetid));
		return elm_sheet;
	}

	public Element setSheetInfo(String sheetid) {
		String xslView = "";
		String xslPrint = "";
		ColModel cm = ColModelLoader.getInstance().getCM(token.site.getSid(),
				cmid);
		if (cm != null) {
			// title = cm.getTitle();
			xslView = token.site.toXSLPatch(cm.getXslView());
			xslPrint = token.site.toXSLPatch(cm.getXslPrint());
		}
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("title", title);
		elm_sheet.setAttribute("logo", logo);
		elm_sheet.setAttribute("xsl", xslView);
		elm_sheet.setAttribute("xslprint", xslPrint);
		return elm_sheet;
	}

	public void setPrintInfo(String sheetid) {
		String company = null;
		String shopid = null;
		int controltype = 0;
		int majorid = 0;
		// controltype 门店控制符合，MR公司用
		// 取得门店和课类,抬头公司
		String sql_sel_shop = "select a.shopid,a." + majorName + ",s.controltype,s.shopname from "
		                      + tableName + " a " + " inner join shop s on s.shopid=a.shopid "
		                      + " where sheetid=?";		

		ResultSet rs = SqlUtil.queryPS(conn, sql_sel_shop, sheetid);
		try {
			if (rs.next()) {
				shopid = rs.getString("shopid") + "X";
				majorid = rs.getInt(majorName);
				controltype = rs.getInt("controltype");
				company = SqlUtil.fromLocal(rs.getString("shopname"));
				//this.logo = rs.getString("booklogofname");
			}

			SqlUtil.close(rs);

			// 如果是烟草课类，则修改打印抬头和logo
			/*if (majorid == VSSConfig.getInstance().getTobatoMajorid()
					&& !"".equals(shopid)) {*/
				String sql_sel_headshopid = "select s.shopname from shop s "
					+ " where s.shopid=? ";
				rs = SqlUtil.queryPS(conn, sql_sel_headshopid, shopid);
				if (rs.next()) {
					company = SqlUtil.fromLocal(rs.getString("shopname"));
					//this.logo = rs.getString("booklogofname");
				}
				SqlUtil.close(rs);
				// 民润店
			//} else if (controltype == 2) {
			//	company = "MR";
				this.logo = VSSConfig.getInstance().getPrintTobatoLogo();
		//	}

			this.title = company + this.title;
			this.logo = "../img/" + this.logo;
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public int doRead(String sheetid) {
		String sql = " update " + catTableName
				+ " set status=?,readtime=sysdate where sheetid=? and status=0";
		return SqlUtil.executePS(conn, sql, new Object[] { 1, sheetid });
	}

	public int doConfirm(String sheetid) {
		String sql = " update "
				+ catTableName
				+ " set status=?,confirmtime=sysdate where sheetid=? and status in (0,1)";
		return SqlUtil.executePS(conn, sql, new Object[] { 10, sheetid });
	}

	public Element execute(String operation, HttpServletRequest request)
			throws Exception {
		try {
			Method method = getDeclaredMethod(this, operation,
					HttpServletRequest.class);
			if (method == null) {
				throw new Exception(this.getClass().getName() + "无法访问："
						+ operation);
			}
			Element elm = (Element) method.invoke(this, request);
			return elm;
		} catch (InvocationTargetException e) {
			if (e.getCause() == null) {
				throw e;
			} else {
				throw new Exception(e.getCause().getMessage());
			}
		}
	}

	public static Method getDeclaredMethod(Object object, String methodName,
			Class<?>... parameterTypes) {
		Method method = null;
		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {
			}
		}
		return null;
	}
}
