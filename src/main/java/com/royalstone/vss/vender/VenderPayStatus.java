package com.royalstone.vss.vender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VenderPayStatus {
	
	static public boolean hasFrozen(Connection conn, String venderid, String bookno) throws SQLException{
		boolean res = false;
		
		String sql = " select payflag from  venderpaystatus where venderid=? and bookno=? ";
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ps.setString(2, bookno);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()){
			int tmp = rs.getInt(1);
			if(tmp == 1){
				res = true;
			}
		}
		rs.close();
		ps.close();
		
		return res;
	} 
}
