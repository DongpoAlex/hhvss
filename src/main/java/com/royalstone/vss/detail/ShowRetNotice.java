package com.royalstone.vss.detail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

public class ShowRetNotice 
{
	private String title;
	private String logo;
	final private String sheetid;
	final private Connection conn;
	
	public ShowRetNotice( Connection conn, String sheetid ) throws SQLException, InvalidDataException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;
	}
	
	private Element getHead() throws SQLException, InvalidDataException
	{
		String sql =  "SELECT 	re.sheetid, re.refsheetid, "
			+" re.venderid, v.vendername, v.faxno venderfax, v.telno vendertel, "
			+" re.shopid, sh.shopname, sh.address shopaddr, sh.telno shoptel, sh.shopstatus," 
			+" p.paytypeid, p.paytypename, re.retdate, "
			+" re.flag, re.editor, re.editdate, "
			+" re.operator, re.checker, re.checkdate, "
			+" re.placeid, pe.placename, re.majorid, c.categoryname majorname, " +
			" re.note, cat.status, cat.warnstatus,cat.releasedate,re.rettype, " +
			" re.askshopid,ss.shopname as askshopname,sh.controltype "
			+" FROM retnotice re "
			+" JOIN vender v ON ( v.venderid = re.venderid ) "
			+" JOIN shop sh ON ( re.shopid = sh.shopid ) "
			+" LEFT JOIN shop ss ON ( re.askshopid = ss.shopid ) "
			+" JOIN paytype p ON ( p.paytypeid = re.paytypeid ) "
			+" LEFT JOIN place pe ON ( pe.shopid = re.askshopid AND pe.placeid = re.placeid ) "
			+" left join category c ON ( categoryid = re.majorid ) " 
			+" JOIN cat_retnotice cat ON ( cat.sheetid = re.sheetid ) "
			+" WHERE re.sheetid= ?  " ;
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "head");
	}
	
	private Element getBody( ) throws SQLException, InvalidDataException
	{
		String sql = " SELECT i.sheetid, i.goodsid, g.barcode, g.goodsname, g.spec, g.unitname, " +
				" i.vldqty, i.retqty, i.cost, (i.cost*i.vldqty) sumcost, i.reason, " +
				" i.categoryid, c.categoryname " +
				" FROM retnoticeitem i " +
				" JOIN goods g ON ( g.goodsid = i.goodsid ) " +
				" left join category c ON ( c.categoryid = i.categoryid ) " +
				" WHERE i.sheetid = ? " ;
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "body");
	}
	
	public String getVenderId() throws SQLException{
		String sql=" select venderid from retnotice where sheetid=?";
		return SqlUtil.queryPS4SingleColumn(conn, sql, sheetid).get(0);
	}
	
	/**
	 * 取打印的烟草公司名称
	 * @return
	 * @throws SQLException 
	 */
	private void setPrintInfo() throws SQLException{
		String company=null;
		String shopid=null;
		int controltype=0; 
		int majorid=0;
		//controltype 门店控制符合，MR公司用
		//取得门店和课类,抬头公司
/*		String sql_sel_shop = "select a.shopid,a.majorid sgroupid,s.controltype,b.booktitle,b.booklogofname from retnotice a " +
		" inner join shop s on s.shopid=a.shopid " +
		" inner join book b on b.bookno=s.bookno " +
		" where sheetid=? and rownum=1 ";*/
		//2013-08-09
		String sql_sel_shop = "select a.shopid,a.majorid sgroupid,s.controltype,s.shopname from retnotice a " +
		" inner join shop s on s.shopid=a.shopid " +
		" where sheetid=? and rownum=1 ";
		ResultSet rs = SqlUtil.queryPS(conn, sql_sel_shop, sheetid);
		if( rs.next() ){
			shopid = rs.getString("shopid")+"X";
			majorid = rs.getInt("sgroupid");
			controltype = rs.getInt("controltype");
			company = SqlUtil.toLocal(rs.getString("shopname"));
			//this.logo = rs.getString("booklogofname");
		}
		SqlUtil.close(rs);
			
		//如果是烟草课类，则修改打印抬头和logo
		//if( majorid == VSSConfig.getInstance().getTobatoMajorid() && !shopid.equals("") ){
			/*String sql_sel_headshopid = "select b.booktitle,b.booklogofname from shop s " +
					" inner join book b on (b.bookno=s.bookno and s.shopstatus=1) " +
					" where s.shopid=? ";*/
			String sql_sel_headshopid = "select s.shopname from shop s " +
			" where s.shopid=? ";
			rs = SqlUtil.queryPS(conn, sql_sel_headshopid, sheetid);
			if( rs.next() ){
				company = SqlUtil.toLocal(rs.getString("shopname"));
				//this.logo = rs.getString("booklogofname");
			}
			SqlUtil.close(rs);
		//宝鸡商场超市
		//}else if(controltype==2){
			//String Z_sql="";
			//company="MR";
			//this.logo = VSSConfig.getInstance().getPrintTobatoLogo();
		//}
		
		this.title=company+"退货通知单";		
		this.logo="../img/"+this.logo;
	}
	
	public Element toElement() throws InvalidDataException, SQLException { 
		setPrintInfo();
		Element elm_sheet = new Element( "sheet" );
		elm_sheet.setAttribute( "title", title );
		elm_sheet.setAttribute( "logo", logo );
		
		elm_sheet.addContent( this.getHead() );
		elm_sheet.addContent( this.getBody());
		return elm_sheet; 
	}
	

}
