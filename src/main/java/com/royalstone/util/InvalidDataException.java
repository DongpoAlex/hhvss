/*
 * Created on 2004-6-24
 */
package com.royalstone.util;

/**
 * @author Mengluoyi
 *
 */
public class InvalidDataException extends RuntimeException {

	/**
	 * 
	 */
	public InvalidDataException() {
		super();
	}

	/**
	 * @param message	对于例外的文字描述. 主调程序可打印此字串,以向用户解释操作失败的原因.
	 */
	public InvalidDataException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidDataException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message	对于例外的文字描述. 主调程序可打印此字串,以向用户解释操作失败的原因.
	 * @param cause
	 */
	public InvalidDataException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 20060719L;

}
