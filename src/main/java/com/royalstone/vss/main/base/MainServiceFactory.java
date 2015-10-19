package com.royalstone.vss.main.base;

import java.lang.reflect.Constructor;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import com.royalstone.security.Token;

public class MainServiceFactory {
	public static IMainService factory(String className, HttpServletRequest request, Connection conn, Token token)
			throws Exception {
		String uri = "com.royalstone.vss.main.";
		String url = uri + className;
		url = ClassReg.getInstance().get(className);
		if(url==null){
			throw new ClassNotFoundException("服务"+className+"尚未注册");
		}
		Class clazz;
		try {
			clazz = Class.forName(url);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("对象"+className+"尚未定义");
		}
		Constructor<IMainService> cus;

		cus = clazz.getConstructor(HttpServletRequest.class, Connection.class, Token.class);

		return cus.newInstance(request, conn, token);
	}
}