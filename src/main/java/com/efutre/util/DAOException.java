package com.efutre.util;

public class DAOException extends RuntimeException {

	public DAOException(String e) {
		super(e);
	}

	public DAOException(Throwable e) {
		 super(e);
	}

}
