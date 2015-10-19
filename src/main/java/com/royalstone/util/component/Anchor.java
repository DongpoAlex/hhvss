/*
 * Created on 2005-12-2
 *
 */
package com.royalstone.util.component;

import org.jdom.Element;

/**
 * @author meng
 *
 */
public class Anchor extends XComponent
{

	public Anchor(String href, String label )
	{
		super(null);
		elm_ctrl = new Element( "a" );
		elm_ctrl.setAttribute( "href", href );
		elm_ctrl.addContent( label );
	}
	
	public Element toElement()
	{
		return this.elm_ctrl;
	}
	
}
