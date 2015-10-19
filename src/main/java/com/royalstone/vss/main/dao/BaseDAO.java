package com.royalstone.vss.main.dao;

import java.sql.Connection;

public abstract class BaseDAO {
	final private Connection conn;

	public BaseDAO(Connection conn) {
		super();
		this.conn = conn;
	}

}
