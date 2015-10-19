package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.fiscal.FiscalValue;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.Sheet;

/**
 * @author baij
 * 结算付款查询
 */
public class Paymentvoucher extends Sheet {

	private double payamt;

	public Paymentvoucher(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return  new SqlFilter(map).
		addFilter2String( "venderid", "a.venderid",true).
		addFilter2String( "sheetid", "a.sheetid",true).
		addFilter2String( "payshopid", "a.payshopid",true).
		addFilter2String( "buid", "a.buid",true).
		addFilter2String( "flag", "a.flag",true).
		addFilter2MinDate( "date_min", "a.planpaydate",true).
		addFilter2MaxDate( "date_max", "a.planpaydate",true);
	}
	
	protected void initInnerValue(String sheetid){
		String sql="select payshopid,payamt from paymentvoucher where sheetid=?";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, sheetid);
		if(list.size()>0){
			HashMap<String, String> map = list.get(0);
			this.payamt = Double.parseDouble(map.get("payamt")) ;
		}else{
			
		}
		this.title ="华润万家付款单";
	}
	
	public Element getHead(String sheetid) {
		SqlUnit unit4Head = SqlMapLoader.getInstance().getSql(sid,
				ColModelLoader.getInstance().getCM(sid, cmid).getHsmid());
		Element elm = SqlUtil.getRowSetElement(conn, unit4Head.toString(), sheetid, "head");
		elm.getChild("row").addContent(new Element("payamtToChinese").addContent(payamtToChinese()));
		return elm;
	}
	private String payamtToChinese(){
		String res = "NAC";
		if(this.payamt>0){
			FiscalValue f = new FiscalValue(this.payamt);
			res = f.toChinese();
		}
		return res;
	}
}