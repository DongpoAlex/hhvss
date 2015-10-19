/*
 * Created on 2005-12-28
 *
 */
package com.royalstone.util.excel;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author meng
 *
 */
public class Style {

	public Style( String id, String number_format )
	{
		elm = new Element( "Style" );
		elm.setNamespace( Workbook.namespace );
		elm.setAttribute( "ID", id, namespace );

		Element elm_format = new Element( "NumberFormat" );
		elm_format.setNamespace( Workbook.namespace );
		elm_format.setAttribute( "Format", number_format, Style.namespace );
		elm.addContent( elm_format );
	}
	
	public Element toElement()
	{
		return elm;
	}
	
	final public static Namespace namespace = Workbook.space_style;
	private Element elm;
}
