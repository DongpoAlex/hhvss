package com.royalstone.vss.main.base;

import java.io.File;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.daemon.Filter;
import com.royalstone.util.sql.SqlFilter;

public interface IMainService{
	
	/**
	 * //执行前台的请求
	 * @return
	 * @throws Exception
	 */
	public Element execute() throws Exception;
	
	/**
	 * 导出excel
	 * @param file
	 * @param cmid
	 * @return
	 */
	public String excel(File file, String cmid);
	
	/**
	 * //获得xml格式
	 * @return
	 */
	public Element toElement();
	/**
	 * 获取HTML格式
	 * @return
	 */
	public Element toHTML();
}
