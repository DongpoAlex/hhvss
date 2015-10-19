/*
 * Created on 2005-12-28
 *
 */
package com.royalstone.util.excel;

import org.jdom.Element;

/**
 * @author meng
 *
 */
public class NumberFormat {

	public NumberFormat( String format )
	{
		elm_format = new Element( "NumberFormat" );
		elm_format.setNamespace( Workbook.namespace );
		elm_format.setAttribute( "Format", format, Style.namespace );
	}
	
	public Element toElement()
	{
		return elm_format;
	}
	
	private Element elm_format;
}
