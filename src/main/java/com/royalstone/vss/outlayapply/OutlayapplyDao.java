package com.royalstone.vss.outlayapply;

import java.sql.Connection;

/**
 * OI费用申请单
 * 
 * @author baijian
 * 
 */
public class OutlayapplyDao {
	final Connection	conn;

	public OutlayapplyDao(Connection conn) {
		super();
		this.conn = conn;
	}

	public void addHead(Outlayapply o) {
		String sql="insert into ";
	}

	public void addItem(OutlayapplyItem i) {
		
	}
}
