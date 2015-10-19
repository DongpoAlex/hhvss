package com.royalstone.vss.main.base;

import java.io.File;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.sql.SqlFilter;

/**
 * @author baij
 * 单据类、列表报表类接口
 */
public interface ISheet extends IMainService {
	public Element cminit();
	
	/**
	 * 单据查找
	 * @param parms
	 * @return
	 */
	public Element search();
	
	/**
	 * 查看单据明细
	 * @param sheetid
	 * @return
	 */
	public Element show();
	
	/**
	 * 打印单据明细
	 * @param sheetid
	 * @return
	 */
	public Element print();
	/**
	 * 执行阅读
	 * @return
	 */
	public Element doRead();
	
	/**
	 * 执行确认
	 * @return
	 */
	public Element doConfirm();
	
	
	/**
	 * //处理前台查询条件
	 * @param map
	 * @return
	 */
	public SqlFilter cookFilter(Map<String,String[]> map);

}
