/*
 * Created on 2005-12-28
 *
 */
package com.royalstone.util.excel;

import org.jdom.Element;

/**
 * Cell 对应于 Excel 文档内的一个单元格.
 * @author meng
 *
 */
public class Cell 
{

	/**
	 * 实例化一个Cell对象. 
	 * @param type	单元类型
	 * @param value	单元取值
	 */
	public Cell( String type, String value )
	{
		this.type = type;
		this.value = value;
	}
	
	/**
	 * 此方法把单元格数据转换为 EXCEL 规范的格式.
	 * @return
	 */
	public Element toElement()
	{
		Element elm = new Element( "Cell" );
		elm.setNamespace( Workbook.namespace );
		Element elm_data = new Element( "Data" );
		elm_data.setNamespace( Workbook.namespace );
		elm_data.setAttribute( "Type", type, Style.namespace );
		elm_data.addContent( value );
		elm.addContent( elm_data );
		return elm;
	}
	
	private String type;
	private String value;
	
	final public static String STRING 	= "String"; 
	final public static String NUMBER 	= "Number"; 
	final public static String DATETIME = "DateTime"; 
}
