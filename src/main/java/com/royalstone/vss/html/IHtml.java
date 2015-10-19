package com.royalstone.vss.html;

import org.jdom.Element;

public interface IHtml {
	/**
	 * 输出html
	 * @return
	 */
	public String toHTML();
	
	public Element toElement();
}
