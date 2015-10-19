/*
 * Created on 2005-12-28
 *
 */
package com.royalstone.util.excel;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.royalstone.util.InvalidDataException;

/**
 * @author meng
 *
 */
public class TableSchema {

	
	/**
	 * Constructor for TableSchema.
	 * @param metadata
	 * @throws SQLException
	 */
	public TableSchema ( ResultSetMetaData metadata ) throws SQLException
	{
		schema = new ColSchema[ metadata.getColumnCount()+1 ];
		for( int i=1; i< schema.length; i++ ) {
			String name = metadata.getColumnName(i);
			String type  = "String";
			String style = "String";
			
			switch( metadata.getColumnType(i) ){

			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				type  = "DateTime";
				style = "DateTime";
				break;

			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.REAL:
				type  = "Number";
				style = "Number";
				break;

			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.TINYINT:
				type  = "Number";
				style = "Integer";
				break;

			default:
				type  = "String";
				style = "String";
			}

			schema[i] = new ColSchema( type, name, style );
		}
	}
	
	/**
	 * @param i	字段的编号(从1开始)
	 * @return	字段名称(通过为数据库中表的字段名称)
	 * @throws InvalidDataException
	 */
	public String getTypename( int i ) throws InvalidDataException
	{
		if( i >= schema.length ) throw new InvalidDataException( "Invalid data: " + i );
		return schema[i].type;
	}
	
	/**
	 * @param i	字段的编号(从1开始)
	 * @return	字段的类型名( String, Number, Integer, DateTime )
	 * @throws InvalidDataException
	 */
	public String getStyleid( int i ) throws InvalidDataException
	{
		if( i >= schema.length ) throw new InvalidDataException( "Invalid data: " + i );
		return schema[i].style;
	}
	
	/**
	 * @return	字段数
	 */
	public int getColumnCount()
	{
		return schema.length-1;
	}
	
	/**
	 * <code>schema</code>	存放Table 内各列的类型信息. 
	 * 其中, schema[0]无意义, 有效的列定义为schema[1]~schema[schema.length-1].
	 */
	final public ColSchema[] schema;
}

/**
 * Column definition.
 */
class ColSchema 
{
	
	public ColSchema( String coltype, String colname, String styleid )
	{
		this.type = coltype;
		this.name = colname;
		this.style = styleid;
	}
	
	/**
	 * <code>type</code>	Column type: String, Number, DateTime.
	 * NOTE: Integer is not supported by Excel.
	 * 
	 */
	final public String type;
	
	/**
	 * <code>name</code>	Column name. Normally, it's name of DB Table.
	 */
	final public String name;
	
	/**
	 * <code>style</code>	Display style used in Excel file: String, Number, Integer, DateTime.
	 * NOTE: Columns with type integer in table are displayed with type "Integer" 
	 * while represented as type "Number" in Excel file.
	 * NOTE: 如果某字段在数据库中类型为INTEGER, 在EXCEL文件中的类型应为Number, 而Style 可以设置为Integer. 
	 * 这样, 用Excel文件打开后, 该字段以整形数表示.
	 */
	final public String style;
}

