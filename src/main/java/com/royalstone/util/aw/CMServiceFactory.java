package com.royalstone.util.aw;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import com.royalstone.security.Token;

public class CMServiceFactory {
	public static ICMQueryService factory(String className,Connection conn,Token token) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		String uri = "com.royalstone.vss.report.cm.";
		String url = uri+className;
		Class clazz =  Class.forName(url);
		Constructor cus = clazz.getConstructor(Connection.class,Token.class);
		return (ICMQueryService)cus.newInstance(conn,token);
	}
}
