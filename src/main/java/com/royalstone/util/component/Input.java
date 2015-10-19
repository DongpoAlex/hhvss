/*
 * Created on 2005-12-14
 *
 */
package com.royalstone.util.component;

import org.jdom.Element;

/**
 * @author meng
 *
 */
public class Input extends XComponent
{
	public Input()
	{
		super(null);
		this.elm_ctrl = new Element( "input" );
	}
	
}
