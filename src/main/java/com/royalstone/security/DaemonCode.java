/*
 *
 */
package com.royalstone.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.royalstone.util.daemon.XDaemon;
import com.royalstone.vss.VSSConfig;

/**
 * @author baij
 * 验证码
 */
public class DaemonCode extends XDaemon {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2132793271930648528L;

	public void init() {
		//系统初始化入口，因为启动会首先获取验证码
		VSSConfig.getInstance();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		Code.sendCode(response, request);
	}
}
