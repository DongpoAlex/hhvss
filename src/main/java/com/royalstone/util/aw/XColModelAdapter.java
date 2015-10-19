package com.royalstone.util.aw;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.IllegalDataException;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlUtil;

public class XColModelAdapter {
	/**
	 * 根据列定义，返回列定义对应的xml格式
	 * 
	 * @param colms
	 * @return
	 */
	static public Element getRowSetElement(List<ColModelDetail> colms) {
		Element elmColmodel = new Element("colmodel");
		int idx = 0;
		ArrayList<String> idxList = new ArrayList<String>();
		for (ColModelDetail colModel : colms) {
			Element elmCol = new Element("col");
			elmCol.setAttribute("seqno", colModel.getSeqno() + "");
			elmCol.setAttribute("field", colModel.getField());
			elmCol.setAttribute("name", colModel.getName());

			if (colModel.getCss() != null) {
				elmCol.setAttribute("css", colModel.getCss());
			}
			if (colModel.getRender() != null) {
				elmCol.setAttribute("render", colModel.getRender());
			}
			elmCol.setAttribute("sum", colModel.getSum() + "");
			if (colModel.getVtype() != null) {
				elmCol.setAttribute("vtype", colModel.getVtype());
			}
			if (colModel.getWidth() != null) {
				elmCol.setAttribute("width", colModel.getWidth());
				if(Integer.parseInt(colModel.getWidth())>0){
					idxList.add(String.valueOf(idx));
				}
			}
			elmColmodel.addContent(elmCol);
			idx++;
		}
		Element elmColIdx = new Element("colidx");
		elmColIdx.addContent(idxList.toString());
		elmColmodel.addContent(elmColIdx);
		return elmColmodel;
	}

	/**
	 * 此函数重写getRowSetElement 固定表示结果集的XML节点名字和表示结果集内一条记录的节点名字
	 * 整个结果集用一个XML节点表示,节点名为set_name. 该节点包含若干名为row_name 的子节点, 每个row_name节点表示一条记录.
	 * row_name 节点内又包含若干子节点, 以字段名为节点名,字段值为节点值. 如果 rs
	 * 为空集,则返回一个名字为set_name,但没有任何子节点的空节点. 返回节点包含一个名为 rows 的属性, 其值为rs中的记录数.
	 * 如果rs为空集,则属性rows 为0.
	 * 
	 * @param rs
	 *            查询数据库后生成的结构集
	 * @param first
	 *            表示当前页数
	 * @param loadcount
	 *            表示每页装载记录数
	 * @return 表示结果集的XML节点
	 * @throws SQLException
	 *             如果从rs取数据的过程中出错,则抛出此意外.
	 */
	static public Element getAwReportRowSetElement(ResultSet rs, ColModel cm, int first, int loadCount)
			throws SQLException {
		Element elm_set = new Element("rowset");
		// 取得列定义
		Element elmColModel = XColModelAdapter.getRowSetElement(cm.getCmDetailList());
		elm_set.addContent(elmColModel);

		ResultSetMetaData metadata = rs.getMetaData();

		int current = 0; // 当前行数
		int rows = 0;
		while (rs.next() && rows < loadCount) {
			current++;
			if (current < first)
				continue;
			rows++;
			Element elm_row = new Element("row");

			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				Element elm_col = new Element(metadata.getColumnName(i).toLowerCase());
				String str = SqlUtil.fromLocal(rs.getString(i));
				if (str != null) {
					str = str.trim();
					/**
					 * 对于 DATETIME 字段要作特殊处理.
					 */
					if (metadata.getColumnType(i) == Types.TIMESTAMP) {
						int p = str.lastIndexOf('.');
						if (p >= 0)
							str = str.substring(0, p);
					}
					// 去除 00:00:00.0
					if (metadata.getColumnType(i) == Types.DATE) {
						str = str.replace(" 00:00:00.0", "");
					}

					setElementText(elm_col, str);
				} else {
					elm_col.setText("");
				}

				elm_row.addContent(elm_col);
			}
			elm_set.addContent(elm_row);
		}

		return elm_set;
	}

	static private void setElementText(Element elm, String str) {
		if (elm != null && str != null)
			try {
				elm.setText(str);
			} catch (IllegalDataException e) {
				elm.addContent("(IllegalData)");
			}
	}

	static public Element getAwReportRowSetElement(Connection conn, int sid, String sql, String cmid, int first,
			int loadCount) {
		Element elm_set;
		try {
			ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
			ResultSet rs = SqlUtil.querySQL(conn, sql);

			elm_set = XColModelAdapter.getAwReportRowSetElement(rs, cm, first, loadCount);

			SqlUtil.close(rs);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		return elm_set;
	}
	
	static public Element getAwReportRowSetElement(Connection conn, int sid, String sql, String cmid, int first,
			int loadCount,Object ... objs ) {
		Element elm_set;
		try {
			ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
			ResultSet rs = SqlUtil.queryPS(conn, sql, objs);
			elm_set = XColModelAdapter.getAwReportRowSetElement(rs, cm, first, loadCount);
			SqlUtil.close(rs);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		return elm_set;
	}

	/**
	 * 得到报表视图定义信息
	 * 
	 * @param conn
	 * @param cmid
	 * @return
	 * @throws InvalidDataException
	 */
	public static Element getAwReportDef(Connection conn, int sid, String cmid) throws InvalidDataException {
		Element elm_set = new Element("rowset");
		ColModel cm = ColModelLoader.getInstance().getCM(sid, cmid);
		if (cm == null) {
			return elm_set;
		}
		elm_set.addContent(new Element("cmid").addContent(cm.getCmid() ));
		elm_set.addContent(new Element("moduleid").addContent(cm.getModuleid() + ""));
		elm_set.addContent(new Element("title").addContent(cm.getTitle()));
		elm_set.addContent(new Element("footer").addContent(cm.getFooter()));
		elm_set.addContent(new Element("note").addContent(cm.getNote()));
		elm_set.addContent(new Element("xslprint").addContent(cm.getXslPrint()));
		elm_set.addContent(new Element("xslview").addContent(cm.getXslView()));

		// 获取sql语句
		elm_set.addContent(new Element("smid").addContent(cm.getSmid()));
		elm_set.addContent(new Element("smidhead").addContent(cm.getHsmid()));
		elm_set.addContent(new Element("smidbody").addContent(cm.getBsmid()));

		// 列定义
		Element elmColModel = XColModelAdapter.getRowSetElement(cm.getCmDetailList());
		elm_set.addContent(elmColModel);

		// 查询条件定义
		Element elmSearch = new Element("search");
		List cmSearch = cm.getSearchList();
		for (Iterator i = cmSearch.iterator(); i.hasNext();) {
			ColSearch col = (ColSearch) i.next();
			Element elmIput = new Element("input");
			elmIput.setAttribute("seqno", col.getSeqno() + "");
			elmIput.setAttribute("field", col.getField());
			elmIput.setAttribute("name", col.getName());
			if (col.getCss() != null) {
				elmIput.setAttribute("css", col.getCss());
			}
			elmIput.setAttribute("vtype", col.getVtype() + "");
			elmIput.setAttribute("inputtype", col.getInputType() + "");
			elmIput.setAttribute("compare", col.getCompare() + "");
			elmIput.setAttribute("isnull", col.getIsnull() + "");
			if (col.getDefaultValue() != null) {
				elmIput.setAttribute("defaultvalue", col.getDefaultValue());
			}
			if (col.getWidth() != null) {
				elmIput.setAttribute("width", col.getWidth());
			}
			elmSearch.addContent(elmIput);
		}
		elm_set.addContent(elmSearch);
		return elm_set;
	}
}
