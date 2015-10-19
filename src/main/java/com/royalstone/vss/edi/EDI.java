package com.royalstone.vss.edi;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.util.Log;
import com.royalstone.util.Secret;

public class EDI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Config file not defined.");
			System.exit(1);
		}

		String config_file = args[0];

		File fconfig = new File(config_file);
		if (!fconfig.exists()) {
			System.err.println("File not exists, please check your file path:" + config_file);
			return;
		}

		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(config_file);

			Element elmRoot = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = elmRoot.getChildren();
			
			for (Element elmTemp : list) {
				String username = elmTemp.getChildTextTrim("username");
				String password = elmTemp.getChildTextTrim("password");
				password = Secret.decrypt(password);
				//System.out.println("username:"+username+";  password:"+password);
				String driverClassName = elmTemp.getChildTextTrim("driverClassName");
				String url = elmTemp.getChildTextTrim("url");
				int interval = Integer.parseInt(elmTemp.getChildTextTrim("interval"));
				String name = elmTemp.getChildTextTrim("name");
				
				Hashtable<String, String> connTable = new Hashtable<String, String>();
				connTable.put("username", username);
				connTable.put("password", password);
				connTable.put("driverClassName", driverClassName);
				connTable.put("url", url);
				Worker worker = new Worker(connTable, interval, name);
				worker.start();
			}
			
		}
		catch (Exception e) {
			Log.event("EDI.Main", e.getMessage());
			e.printStackTrace();
		}
	}

	
}
