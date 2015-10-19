package com.royalstone.vss.sheet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;

public interface ISheetService {
	/**
	 * 单据查找
	 * @param parms
	 * @return
	 */
	public Element search(Map<String, String[]> parms);
	
	/**
	 * 计算总行数
	 * @param filter
	 * @return
	 */
	public int getCount(Filter filter);
	/**
	 * 单据明细
	 * @param sheetid
	 * @return
	 */
	public Element veiw(String sheetid);
	
	/**
	 * 执行阅读
	 * @return
	 */
	public int doRead(String sheetid);
	
	/**
	 * 执行确认
	 * @return
	 */
	public int doConfirm(String sheetid);
	
	/**
	 * 查询条件
	 * @param parms
	 * @return
	 */
	public Filter cookFilter(Map<String, String[]> parms);
	
	/**
	 * 获得单据表头
	 * @param sheetid
	 * @return
	 */
	public Element getHead(String sheetid);
	/**
	 * 获得单据明细
	 * @param sheetid
	 * @return
	 */
	public Element getBody(String sheetid);
	/**
	 * 设置打印标题和logo信息
	 * @param sheetid
	 */
	public void setPrintInfo(String sheetid);
	/**
	 * 获得单据venderid
	 * @param sheetid
	 * @return
	 */
	public String getVenderid(String sheetid);
	
	/**
	 * 检查单据是否合法
	 * @param sheetid
	 * @param token
	 * @return
	 */
	public boolean checkVenderid(String sheetid,Token token);
	
	
	/**
	 * 执行自定义方法
	 * @return
	 * @throws Exception 
	 */
	public Element execute(String opt,HttpServletRequest request) throws Exception;

}
