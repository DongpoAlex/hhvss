package com.royalstone.util.aw;

import java.io.File;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.daemon.Filter;

public interface ICMQueryService {
	public Filter cookFilter(Map map);
	public Element load(String cmid, Map parms);
	public void excel(File file, String cmid, Map parms);
}
