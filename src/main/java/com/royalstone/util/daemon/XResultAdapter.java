/*
 * Created on 2006-07-18
 *
 */
package com.royalstone.util.daemon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.jdom.Element;
import org.jdom.IllegalDataException;

import com.royalstone.util.sql.SqlUtil;

/**
 * 使用XResultAdapter 可以方便地访问数据库,并把结果集转换为XML节点.
 * @author Mengluoyi
 *
 */
public class XResultAdapter
{

    public XResultAdapter( ResultSet rs )
	{
		this.rs = rs;
	}
    /**	此函数从结果集 rs 中取所有记录, 并把取得的信息包装成一个XML节点.
     * 整个结果集用一个XML节点表示,节点名为set_name.
     * 该节点包含若干名为row_name 的子节点, 每个row_name节点表示一条记录.
     * row_name 节点内又包含若干子节点, 以字段名为节点名,字段值为节点值.
     * 如果 rs 为空集,则返回一个名字为set_name,但没有任何子节点的空节点.
     * 返回节点包含一个名为 rows 的属性, 其值为rs中的记录数. 如果rs为空集,则属性rows 为0.
     * @param set_name	表示结果集的XML节点名字
     * @param row_name	表示结果集内一条记录的节点名字
     * @return			表示结果集的XML节点
     * @throws SQLException	如果从rs取数据的过程中出错,则抛出此意外.
     */
    public Element getRowSetElement( String set_name, String row_name) throws SQLException{
        Element elm_set = new Element(set_name);
        ResultSetMetaData metadata = rs.getMetaData();
        while (rs.next()) {
            Element elm_row = new Element(row_name);


        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            Element elm_col = new Element(metadata.getColumnName(i).toLowerCase());
            String str = rs.getString(i);
            if (str != null){
                str = SqlUtil.fromLocal( str.trim() );
                /**
                 * 对于 DATETIME 字段要作特殊处理.
                 */
                if ( metadata.getColumnType(i) == Types.TIMESTAMP || metadata.getColumnType(i) == Types.DATE) {
                    int p = str.lastIndexOf( '.' );
                    if( p>=0 ) str = str.substring( 0, p );
                    str = str.replace(" 00:00:00", "");
                }
                setElementText( elm_col, str );
            } else
                elm_col.setText("");

            elm_row.addContent(elm_col);
        }

            elm_set.addContent(elm_row);
            rows++;

        }
        return elm_set;
    }

    /**	此函数从结果集 rs 中取所有记录, 并把取得的信息包装成一个XML节点.R5用于分页。
     * 整个结果集用一个XML节点表示,节点名为set_name.
     * 该节点包含若干名为row_name 的子节点, 每个row_name节点表示一条记录.
     * row_name 节点内又包含若干子节点, 以字段名为节点名,字段值为节点值.
     * 如果 rs 为空集,则返回一个名字为set_name,但没有任何子节点的空节点.
     * 返回节点包含一个名为 rows 的属性, 其值为rs中的记录数. 如果rs为空集,则属性rows 为0.
     * @param set_name	表示结果集的XML节点名字
     * @param row_name	表示结果集内一条记录的节点名字
     * @param first	    表示当前页数
     * @param loadCount	表示每页装载记录数
     * @return			表示结果集的XML节点
     * @throws SQLException	如果从rs取数据的过程中出错,则抛出此意外.
     */
    public Element getRowSetElement( String set_name, String row_name, int first, int loadCount )
    throws SQLException
    {
		Element elm_set = new Element(set_name);
		ResultSetMetaData metadata = rs.getMetaData();

		int current = 0; //当前行数
		rows = 0;
		while ( rs.next() && rows < loadCount ) {
			current++;
			if (current < first) continue;
			rows++;
			Element elm_row = new Element(row_name);

			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				Element elm_col = new Element(metadata.getColumnName(i).toLowerCase());
				String str = rs.getString(i);

				if (str != null) {
	                str = SqlUtil.fromLocal( str.trim() );
	                /**
                     * 对于 DATETIME 字段要作特殊处理.
                     */
                    if ( metadata.getColumnType(i) == Types.TIMESTAMP || metadata.getColumnType(i) == Types.DATE) {
                    	int p = str.lastIndexOf( '.' );
                    	if( p>=0 ) str = str.substring( 0, p );
                    	str = str.replace(" 00:00:00", "");
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

    /**	此函数从结果集rs 中当前记录, 并转换构造成XML节点.
	 * 如果 rs 是一个空集,则返回一个名为 elm_name 的空节点.
	 * 如果 rs 非空, 则从rs 中取当前记录, 并转换成名为elm_name 的 XML节点.
	 * elm_name 节点包含若干子节点,节点名为结果集中各字段的名字,节点值为相应的字段值.
	 * @param elm_name	表示结果集的XML节点名字
     * @return			表示结果集的XML节点
     * @throws SQLException	如果从rs取数据时出错,则抛出此意外.
     */
    public Element getElement4CurrentRow( String elm_name ) throws SQLException
	{
	    Element elm_row = new Element( elm_name );
        ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            Element elm_col = new Element(metadata.getColumnName(i).toLowerCase());
            String str = rs.getString(i);

            if (str != null){
                str = SqlUtil.fromLocal( str.trim() );
                /**
                 * 对于 DATETIME 字段要作特殊处理.
                 */
                if ( metadata.getColumnType(i) == Types.TIMESTAMP || metadata.getColumnType(i) == Types.DATE) {
                	int p = str.lastIndexOf( '.' );
                	if( p>=0 ) str = str.substring( 0, p );
                	str = str.replace(" 00:00:00", "");
                }
                setElementText( elm_col, str );
            } else
                elm_col.setText("");

            elm_row.addContent(elm_col);
        }
        rows = 1;

	    return elm_row;
	}


    /**	此函数将XML节点elm 的值置为字串str. 如果str 内包含非法字符,则elm的值将被置成字串: "(IllegalData)" .
     * @param elm	XML节点
     * @param str	XML节点的值
     */
    private void setElementText( Element elm, String str )
    {
        if( elm != null && str != null )
        try{
            elm.setText( str );
        }catch ( IllegalDataException e){
            elm.addContent( "(IllegalData)" );
        }
    }

    public int rows()
    {
    	return rows;
    }

    final private ResultSet rs;
    private int rows = 0;
}
