package com.royalstone.vss.catalogue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;

public class SearchChargeShopItem {

	public SearchChargeShopItem  (Connection conn, Map map ) throws SQLException, InvalidDataException, IOException{
		String[] ss = (String [] ) map.get( "sheetid" );
		Values val_sheetid = new Values ( ss );
		String sql="select ch.SheetID,ch.SeqNo,s.ChargeName,ch.ReckoningDate,"
			+ " ch.SettleMode,ch.InvoiceMode,(ch.ChargeAmt*-1) as ChargeAmt,ch.NoteRemark,ch.NoteNo"
			+ " from ChargeShopItem0 ch "
			+ " JOIN chargecode s ON (ch.chargecodeID=s.chargecodeID)"
			+ "where ch.sheetid ="+val_sheetid.toString4String();
//		System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row" );
		
		int rows = adapter.rows();
		if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "purchase" );
	}
	public Element toElement() { return elm_cat; }
	
	private Element elm_cat = null;
}
