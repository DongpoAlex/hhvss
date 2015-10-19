package com.royalstone.certificate.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SQLTool {

	static public String cookUpdateSQL(String tableName, Object obj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		String filed=null;
		String parms=null;
		Class classType = obj.getClass();
		Field[] fields = classType.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			String stringLetter = fieldName.substring(0, 1).toUpperCase();
			// 获得相应属性的getXXX名称
			String getName = "get" + stringLetter + fieldName.substring(1);
			// 获取相应的方法
			Method getMethod = classType.getMethod(getName, new Class[] {});
			// 调用源对象的getXXX（）方法
			Object value = getMethod.invoke(obj, new Object[] {});

			if(value!=null){
				if(filed==null){
					filed=fieldName;
					parms="?";
				}else{
					filed+=","+fieldName;
					parms=",?";
				}
			}
		}
		
		String sql="update "+tableName+" set ("+filed+")=("+parms+")";
		
		return sql;
	}
	
}
