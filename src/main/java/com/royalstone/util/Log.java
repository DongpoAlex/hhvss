package com.royalstone.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.Config;

/**
 * @author baij
 *         全局日志记录
 */
public class Log {
	
	/**
	 * dom4j日志
	 */
	public static Logger	logger	= Logger.getLogger(Utils.class);
	
	
	//以下是自定义日志
	/**
	 * 写事件日志
	 */
	public static synchronized boolean event(String module, String message) {
		return (logToFile("Event", "", module, message));
	}

	/**
	 * 写错误日志
	 */
	public static synchronized boolean error(String code, String module, String message) {
		return (logToFile("Error", code, module, message));
	}

	/**
	 * 写调试日志
	 */
	public static void debug(String module, String message) {
		if (Config.isDebuging) {
			logToFile("Debug", "", module, message);
		}
	}

	/**
	 * 写日志到数据库
	 */
	public static void db(Connection conn, String module, String message) {
		String oper = "vss";
		try {
			oper = InetAddress.getLocalHost().getHostAddress().toString();
			logToDB(conn, module, oper, message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static synchronized boolean print(String module, String message) {
		try {
			String str = Config.dfs.format(new Date()) + " " + "<" + module + "> " + message + "\n";
			System.out.print(str);
			return (true);
		} catch (Throwable ex) {
			ex.printStackTrace();
			return (false);
		}
	}

	public static synchronized void print(String message) {
		System.out.println(Config.dfs.format(new Date()) + " " + message);
	}

	public static boolean logToFile(String fileHead, String code, String module, String message) {
		Date d = new Date();
		String s_sysdate = Config.df.format(d);
		File file = new File(Config.logPath + "/" + fileHead + s_sysdate + ".log");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			if (!file.canWrite()) {
				return false;
			}
			FileOutputStream rwFile = new FileOutputStream(file.getCanonicalPath(), true);
			try {
				if (!code.equals("")) {
					code = "<" + code + "> ";
				}
				String str = Config.dfs.format(d) + " " + code + "<" + module + "> " + message + "\n";
				System.out.print(str);
				rwFile.write(str.getBytes());
				return true;
			} finally {
				rwFile.close();
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static void logToDB(Connection conn, String module, String oper, String message) {
		String sql = "INSERT INTO log_work values(log_work_ID.nextval,5,?,?,sysdate,?)";
		try {
			SqlUtil.executePS(conn, sql, module,oper,message);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
