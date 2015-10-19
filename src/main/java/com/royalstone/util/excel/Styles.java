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
public class Styles {

	public Styles()
	{
		elm = new Element( "Styles" );
		elm.setNamespace( Workbook.namespace );
		addStyle( "String", "@" );
		addStyle( "DateTime", "yyyy/m/d;@" );
		addStyle( "Number", "#,##0.00_ " );
		addStyle( "Integer", "0_ " );
	}
	
	public void addStyle( Style style )
	{
		elm.addContent( style.toElement() );
	}
	
	private void addStyle( String id, String formatstr )
	{
		Style style = new Style( id, formatstr );
		elm.addContent( style.toElement() );
	}
	
	public Element toElement() 
	{ 
		return elm; 
	}
	
	private Element elm;
}
