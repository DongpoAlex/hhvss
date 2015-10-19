package com.royalstone.vss.html;

import java.util.HashMap;
import java.util.Set;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class Html implements IHtml {
	public Html(String tagName) {
		super();
		this.tagName = tagName;
		this.text = "";
		element = new Element(tagName);
	}

	public Html(String tagName, String text) {
		super();
		this.tagName = tagName;
		this.text = text;
		element = new Element(tagName).addContent(text);
	}

	public Html(String tagName, String text, HashMap<String, String> attributeMap) {
		super();
		this.tagName = tagName;
		this.text = text;
		this.attributeMap = attributeMap;
		element = new Element(tagName).addContent(text);
		addAttributeMap(attributeMap);
	}

	public Html(Element element) {
		super();
		this.element = element;
		tagName = element.getName();
		text = element.getText();
	}

	final protected String				tagName;
	final protected String				text;
	final protected Element				element;
	protected HashMap<String, String>	attributeMap = new HashMap<String, String>(0);

	public HashMap<String, String> getAttributeMap() {
		return this.attributeMap;
	}

	public void addAttributeMap(HashMap<String, String> attributeMap) {
		this.attributeMap = attributeMap;
		if (attributeMap != null && attributeMap.size() > 0) {
			Set<String> set = attributeMap.keySet();
			for (String name : set) {
				element.setAttribute(name, attributeMap.get(name));
			}
		}
	}
	
	public void addChild(Html child){
		this.element.addContent(child.toElement());
	}

	public void addAttribute(String name, String value) {
		element.setAttribute(name, value);
		attributeMap.put(name, value);
	}
	
	public String getAttributeValue(String name) {
		return attributeMap.get(name);
	}

	public String getTagName() {
		return this.tagName;
	}

	public String getText() {
		return this.text;
	}

	public Element toElement() {
		return this.element;
	}

	public String toHTML() {
		XMLOutputter outputter = new XMLOutputter("	", true );
		return outputter.outputString(element);
	}

}
