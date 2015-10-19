package com.royalstone.vss.sheet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;

public class SheetServiceFactory {
	public static ISheetService factory(String className,Connection conn,Token token,String cmid) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,InvalidDataException{
		String uri = "com.royalstone.vss.sheet.";
		String url = uri+className;
		Class clazz =  Class.forName(url);
		Constructor cus = clazz.getConstructor(Connection.class,Token.class,String.class);
		return (ISheetService)cus.newInstance(conn,token,cmid);
	}
}