package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.MainService;

/**
 * @author baij
 * 供应商账余信息
 */
public class VenderPayBalance extends MainService{

	public VenderPayBalance(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return null;
	}
	
	/**
	 * 是否账套冻结
	 * @param conn
	 * @param buid
	 * @param payshopid
	 * @param venderid
	 * @return true 表示冻结
	 */
	static public boolean isFreacc(Connection conn,String buid,String payshopid,String venderid){
		boolean res = false;
		String sql = "select isfreacc from venderpaybalance where buid=? and payshopid=? and venderid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, new Object[]{buid,payshopid,venderid});
		if(list.size()==0){
			
		}else{
			res = isFreacc(list.get(0));
		}
		return res;
	}
	/**
	 * 是否账套冻结
	 * @param isfreacc
	 * @return true 表示冻结
	 */
	static public boolean isFreacc(String isfreacc){
		return isfreacc==null?false:"Y".equals(isfreacc)?true:false;
	}
	
	public Element getFreaccPayshop(){
		String venderid;
		if(token.isVender){
			venderid = token.getBusinessid();
		}else{
			venderid = getParamNotNull("venderid");
		}
		
		String sql="select a.venderid,b.vendername,a.payshopid,c.payshopname,decode(a.isfreacc,'N','正常','冻结') payflagname,a.isfreacc payflag " +
				" from venderpaybalance a " +
				" join vender b on a.venderid=b.venderid and a.buid=b.buid " +
				" join payshop c on a.payshopid=c.payshopid " +
				" where a.isfreacc='Y' and a.venderid=?";
		return SqlUtil.getRowSetElement(conn, sql, venderid, "rowset");
	}

}
