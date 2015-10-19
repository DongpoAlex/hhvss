package com.royalstone.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author baij
 *         公共方法类，全局公共方法在此定义
 */
public class Utils {
	/**
	 * 获取当前项目WEB-INF绝对路径
	 * 一般在读取配置文件时用
	 * 
	 * @return
	 */
	public static String getBaseIniPath() {
		URL sPath = Thread.currentThread().getContextClassLoader().getResource("");
		String path = "";
		if (sPath != null) {
			path = sPath.toString();
		} else {
			File file = new File(".");
			try {
				path = new StringBuilder().append(file.getCanonicalPath()).append(File.separator).toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		path = path.replaceAll("file:", "");
		path = path.replaceAll("classes/", "");
		path = path.replaceAll("%20", " ");

		return path;
	}

}
