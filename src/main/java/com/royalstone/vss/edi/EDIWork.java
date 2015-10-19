/**
 * 
 */
package com.royalstone.vss.edi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author BaiJian EDI导出
 */
public class EDIWork {

	final private String		venderid;
	final private Connection	conn;
	final private ArrayList<Item> list;

	public EDIWork(String venderid,ArrayList<Item> list, Connection conn) {
		super();
		this.venderid = venderid;
		this.list = list;
		this.conn = conn;
	}

	public int work() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		for (Iterator<Item> i = list.iterator(); i.hasNext();) {
			Item item = (Item) i.next();
			String className = item.topic;
			
			String uri = "com.royalstone.vss.edi.";
			String url = uri+className;
			Class clazz =  Class.forName(url);
			Constructor cus = clazz.getConstructor(Item.class,String.class,Connection.class);
			IEdiSheet service = (IEdiSheet)cus.newInstance(item,venderid,conn);
			service.work();
		}
		return 0;
	}

}
