/*
 * Created on 2004-12-03
 */
package com.royalstone.util;

/**
 * SecurityException 用于系统权限控制. 主要用于表达以下几类情况:
 * 1) 用户名或密码不正确.
 * 2) 用户要求进行的操作超越了系统允许的权限.
 * 3) 操作超时,系统已自动关闭session.
 * @author Mengluoyi
 *
 */
public class PermissionException extends RuntimeException {

	/**
	 * 
	 */
	public PermissionException() {
		super();
	}

	/**
	 * @param message	对于例外的文字描述. 主调程序可打印此字串,以向用户解释操作失败的原因.
	 */
	public PermissionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PermissionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message	对于例外的文字描述. 主调程序可打印此字串,以向用户解释操作失败的原因.
	 * @param cause
	 */
	public PermissionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	static public String LOGIN_PROMPT = "您尚未登陆，或已超时。请重新登录系统。";

	private static final long serialVersionUID = 20060719L;

}
