package com.royalstone.vss.oi;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.common.Sheetid;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.fiscal.FiscalValue;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlMapUtil;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * @author 白剑
 * 处理OI单据的DAO类
 */
public class OIDao {
	public OIDao(Connection conn, Token token) {
		super();
		this.conn = conn;
		this.token = token;
		site = token.site.getSid();
	}
	final private Connection	conn;
	final private Token token;
	final int site;
	static final private String smid4search="3050001000";
	static final private String smid4head="3050001001";
	static final private String smid4body="3050001002";
	static final private String smid4vender="3050001003";
	static final private String sql4comf="3050001004";
	static final private String sql4delitem="3050001005";
	static final private String sql4del="3050001006";
	static final private String sql4addHead="3050001007";
	static final private String sql4addBody="3050001008";
	
	static final private String sql4major = "3050001009";
	public String getVenderid(String sheetid) throws SQLException{
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,smid4vender);
		ResultSet rs = SqlUtil.queryPS(conn, sqlUnit.toString(), sheetid);
		String rel="";
		if(rs.next()){
			rel = rs.getString("venderid");
		}
		SqlUtil.close(rs);
		return rel;
	}
	/**
	 * 送审单据
	 * @param sheetid
	 */
	public void comf(String sheetid){
		//先判断表体有没有数据
		List<String> list =  SqlUtil.queryPS4SingleColumn(conn, "select count(*) from coutchargechkitem where sheetid=?", sheetid);
		if(list.size()>0){
			String value = list.get(0);
			if("0".equals(value)){
				throw new InvalidDataException("明细数据为空，不允许直接提交！");
			}
		}
		
		int flag = 1;//更新状态为1
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,sql4comf);
		SqlUtil.executePS(conn, sqlUnit.toString(), new Object[]{flag,sheetid});
	}
	
	public void delete(String sheetid){
		SqlUtil.executePS(conn,SqlMapLoader.getInstance().getSql(site,sql4delitem).toString() , new Object[]{sheetid});
		SqlUtil.executePS(conn,SqlMapLoader.getInstance().getSql(site,sql4del).toString(), new Object[]{sheetid});
	}
	/**
	 * 查询单据列表
	 * @param parms
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public Element search(Map parms) throws InvalidDataException, SQLException{
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,smid4search);
		String sql_where = cookFilter(parms).toString();
		Element elm = null;
		int count = SqlMapUtil.getCount(conn, sqlUnit, sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) { throw new InvalidDataException(
				"满足条件的记录数已超过系统处理上限,请重新设置查询条件."); }
		
		String sql = sqlUnit.toString(sql_where);
		
		elm = SqlUtil.getRowSetElement(conn, sql, "rowset", 1, VSSConfig.getInstance().getRowsLimitSoft());
		elm.addContent(new Element("totalCount").addContent(count+""));
		return elm;
	}
	
	/**
	 * 显示明细
	 * @param sheetid
	 * @return
	 * @throws InvalidDataException
	 */
	public Element show(String sheetid) throws InvalidDataException{
		Element elm = new Element("sheet");
		elm.addContent(getHead(sheetid));
		elm.addContent(getBody(sheetid));
		elm.addContent(chinese(sheetid));
		return elm;
	}
	
	private Element getHead(String sheetid) throws InvalidDataException{
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,smid4head);
		return SqlUtil.getRowSetElement(conn, sqlUnit.toString(),new Object[]{sheetid}, "head");
	}
	
	private Element getBody(String sheetid) throws InvalidDataException{
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site,smid4body);
		return SqlUtil.getRowSetElement(conn, sqlUnit.toString(),new Object[]{sheetid}, "body");
	}
	
	private Element chinese(String sheetid){
		double v = 0;
		String sql = "select sum(chargevalue) from coutchargechkitem where sheetid=?";
		ResultSet rs = SqlUtil.queryPS(conn, sql, sheetid);
		try {
			if(rs.next())
				v = rs.getDouble(1);
		}
		catch (SQLException e) {
		}
		FiscalValue f = new FiscalValue(v);
		return new Element("chinese").addContent(f.toChinese());
	}
	
	/**
	 * 对于已有单据先删除后插入，对于新单直接插入
	 * @param elmHead
	 * @param elmBody
	 * @throws SQLException 
	 * @throws ParseException 
	 */
	public String update(Element elmHead,Element elmBody) throws SQLException, ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//判断是否新单，sheetid==null
		//sheetid,flag,venderid,lookupid,regionid,editer,editdate,majorid,chargename,note
		String sheetid = elmHead.getChildText("sheetid");
		int flag=0;
		String venderid = token.getBusinessid();
		String shopform = elmHead.getChildText("shopform");
		String majorid = elmHead.getChildText("majorid");
		String regionid = elmHead.getChildText("regionid");
		String depart = elmHead.getChildText("depart");
		depart="0";
		String chargecodeid = elmHead.getChildText("chargecodeid");
		String settlemode = elmHead.getChildText("settlemode");
		String tsdate = elmHead.getChildText("sdate");
		Date sdate = new Date(df.parse(tsdate).getTime());
		int stamp =1;
		String note = elmHead.getChildText("note");
		String vendertel = elmHead.getChildText("vendertel");
		String venderfax = elmHead.getChildText("venderfax");
		String venderaddr = elmHead.getChildText("venderaddr");
		String vendercon = elmHead.getChildText("vendercon");
		if(sheetid==null || sheetid.equals("")){
			sheetid = Sheetid.getSheetid(conn, 7005, "");
		}else{
			delete(sheetid);
		}
		//插入表头
		SqlUtil.executePS(conn, SqlMapLoader.getInstance().getSql(site,sql4addHead).toString(), new Object[]{sheetid,flag,venderid,shopform,majorid,
				regionid,depart,chargecodeid,settlemode,sdate,stamp,venderid,note,vendertel,
				venderfax,venderaddr,vendercon});
		//插入表体
		List list = elmBody.getChildren("row");
		
		for (Iterator it = list.iterator(); it.hasNext();) {
			Element elm = (Element) it.next();
			
			String sflag = elm.getChildText("_sflag");
			if("D".equalsIgnoreCase(sflag)){
				continue;
			}
			String shopid = elm.getChildText("shopid");
			String managecharge = elm.getChildText("managecharge");
			String traincharge = elm.getChildText("traincharge");
			String chargevalue = elm.getChildText("chargevalue");
			String qty = elm.getChildText("promqty");
			String promtype = elm.getChildText("promtype");
			String onboardtype = elm.getChildText("onboardtype");
			String shelfid = elm.getChildText("shelfid");
			String reason = "";
			String begindate = elm.getChildText("begindate");
			Date sps = new Date(df.parse(begindate).getTime());
			String enddate = elm.getChildText("enddate");
			Date spe = new Date(df.parse(enddate).getTime());
			
			double a = Double.parseDouble(managecharge);
			double b = Double.parseDouble(traincharge);
			double c = a + b;
			
			SqlUtil.executePS(conn, SqlMapLoader.getInstance().getSql(site,sql4addBody).toString(), new Object[]{sheetid,shopid,c,qty,shelfid,reason,sps,spe,managecharge,traincharge,promtype,onboardtype});
		}
		return sheetid;
	}
	
	
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("shopform");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" a.shopform = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("regionid");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" a.regionid = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("chargename");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" a.chargecodeid = " + Values.toString4String(ss[0]));
		}
		
		return filter;
	}
	
	public Element getMajorInfo(String majorid){
		return SqlUtil.getRowSetElement(conn, SqlMapLoader.getInstance().getSql(site,sql4major).toString(),new Object[]{majorid}, "majorInfo");
	}
	public Element getShopInfo(String shopid) {
		return SqlUtil.getRowSetElement(conn, "select * from shop where shopid=?",new Object[]{shopid}, "shopInfo");
	}
}
