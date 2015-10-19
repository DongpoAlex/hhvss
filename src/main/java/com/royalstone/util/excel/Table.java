/*
 * Created on 2005-12-28
 *
 */
package com.royalstone.util.excel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author meng
 *
 */
public class Table {

	
	public Table ( String[][] values )
	{
		elm.setNamespace( Workbook.namespace );
		if( values != null && values.length >0 ) {
			for( int i=0; i<values.length; i++ ) addRow( values[i] );
		}
	}

	public Table ( ResultSet rs ) throws SQLException, InvalidDataException
	{
		elm.setNamespace( Workbook.namespace );
		ResultSetMetaData metadata = rs.getMetaData();
		tableschema = new TableSchema ( metadata );
		addColumnInfo();

		addResultSet( rs );
	}
	
	public Table ( ResultSet rs, String[] title ) throws SQLException, InvalidDataException
	{
		elm.setNamespace( Workbook.namespace );
		ResultSetMetaData metadata = rs.getMetaData();
		tableschema = new TableSchema ( metadata );
		addColumnInfo();
		addRow( title );
		addResultSet( rs );
	}
	
	private void addResultSet( ResultSet rs ) throws SQLException, InvalidDataException
	{
		while( rs.next() ){
			Element elm_row = new Element( "Row" );
			elm_row.setNamespace( Workbook.namespace );
			elm.setNamespace( Workbook.namespace );
			elm.addContent( elm_row );
			
			for( int i=1; i <= tableschema.getColumnCount(); i++ ){
				String str = rs.getString(i);
				str = (str==null)? "" : SqlUtil.fromLocal( str ).trim();
				String type = tableschema.getTypename(i);
				if( str.length()==0 ) type = "String";
				
				//TODO 兼容格式
				type = "String";
				
				Cell cell = new Cell( type, str );
				elm_row.addContent( cell.toElement() );
			}
		}
		
	}
	
	private void addColumnInfo () throws InvalidDataException
	{
		for( int i=1; i<= tableschema.getColumnCount(); i++ ){
			Element elm_col = new Element( "Column" );
			elm_col.setNamespace( Workbook.namespace );
			elm_col.setAttribute( "Index", "" + i, Style.namespace );
			elm_col.setAttribute( "StyleID", tableschema.getStyleid(i), Style.namespace );
			
			elm.addContent( elm_col );
		}
	}
	
	
	/**
	 * 添加一条记录
	 * @param arr_str
	 */
	private void addRow( String[] arr_str )
	{
		Element elm_row = new Element( "Row" );
		elm_row.setNamespace( Workbook.namespace );
		elm.addContent( elm_row );
		if( arr_str != null ) for( int i=0; i<arr_str.length; i++ ) {
			String s = ( arr_str[i] == null ) ? "" : arr_str[i];
			Cell cell = new Cell( "String", s );
			elm_row.addContent( cell.toElement() );
		}
	}
	
	public Element toElement()
	{
		return elm;
	}
	
	private Element elm = new Element( "Table" );
	private TableSchema tableschema;
}
