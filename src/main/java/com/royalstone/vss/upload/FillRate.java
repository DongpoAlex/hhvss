package com.royalstone.vss.upload;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.royalstone.util.sql.SqlUtil;
public class FillRate
{
	
	public FillRate ( Connection conn )
	{
		this.conn = conn;
	}
	
	public  String upload(Element elm) throws 
		 SQLException {
		StringBuffer sb = new StringBuffer();
		String venderid = "";
		String sheetid = "";
		String refsheetid = "";
		String recvsheetid = "";
		String majorid = "";
		String shopid = "";
		String desshopid = "";
		String goodsid = "";
		String barcode = "";
		String qty = "";
		String recvqty = "";
		String cost = "";
		String recvcost = "";
		String costrate = "";
		String recvdate = "";
		
		String sql_del = " delete from rp_fillrate where refsheetid=? and recvsheetid=? and goodsid=? ";
		String sql_ins = " insert into rp_fillrate(venderid, sheetid, refsheetid, recvsheetid, majorid, shopid, desshopid, goodsid, barcode, " +
				" qty, recvqty, cost, recvcost, costrate, recvdate) " +
			" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'YYYY-MM-DD'))";
		
		Element row_set = elm.getChild("row_set");
		List list = row_set.getChildren("row");
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element elm_row = (Element) it.next();
			venderid = elm_row.getChildText("venderid");
			sheetid = elm_row.getChildText("sheetid");
			refsheetid = elm_row.getChildText("refsheetid");
			recvsheetid = elm_row.getChildText("recvsheetid");
			majorid = elm_row.getChildText("majorid");
			shopid = elm_row.getChildText("shopid");
			desshopid = elm_row.getChildText("desshopid");
			goodsid = elm_row.getChildText("goodsid");
			barcode = elm_row.getChildText("barcode");
			qty = elm_row.getChildText("qty");
			recvqty = elm_row.getChildText("recvqty");
			cost = elm_row.getChildText("cost");
			recvcost = elm_row.getChildText("recvcost");
			costrate = elm_row.getChildText("costrate");
			recvdate = elm_row.getChildText("recvdate");
			SqlUtil.executePS(conn, sql_del, 
					new Object[]{refsheetid,recvsheetid,
							Integer.parseInt(goodsid)
					});
			
			int j = SqlUtil.executePS(conn, sql_ins, new Object[]{
					venderid,
					sheetid,
					refsheetid,
					recvsheetid,
					Integer.parseInt(majorid),
					shopid,
					desshopid,
					Integer.parseInt(goodsid),
					barcode,
					Float.parseFloat(qty),
					Float.parseFloat(recvqty),
					Float.parseFloat(cost),
					Float.parseFloat(recvcost),
					Float.parseFloat(costrate),
					recvdate
			});
			if (j != 1) {
				sb.append(recvsheetid + "," + goodsid + ";");
			}
		}
		
		return sb.toString();
	}
	
	final private Connection conn;
}