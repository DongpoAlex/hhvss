package com.royalstone.vss.main.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import com.royalstone.vss.VSSConfig;

public class ClassReg {
	private static ClassReg	instance	= null;
	private Hashtable<String, String> classMap = new Hashtable<String, String>();
	private ClassReg() {}
	
	static public ClassReg getInstance(){
		if (instance == null) {
			instance = new ClassReg();
			String fpath =ClassReg.class.getResource("/classreg.properties").getPath();
			try {
				Properties prop = new Properties();
				FileInputStream instr = new FileInputStream(fpath);
				prop.load(instr);
				Set<Object> keySet = prop.keySet();
				for (Object key : keySet) {
					String value = (String)prop.get(key);
					if(value!=null && value.length()>0){
						instance.put((String)key, value);
						System.out.println("注册服务："+key+" => "+value);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public String get(String className){
		return classMap.get(className);
	}
	
	public void put(String className,String classPath){
		classMap.put(className, classPath);
	}
}
