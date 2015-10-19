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
public class Worksheet {

	public Worksheet( String name )
	{
		elm.setNamespace( Workbook.namespace );
		elm.setAttribute( "Name", name, Workbook.space_style );
	}
	
	public void addTable( Table table )
	{
		elm.addContent( table.toElement() );
	}
	
	public Element toElement()
	{
		return elm;
	}
	
	private Element elm = new Element( "Worksheet" );
}
