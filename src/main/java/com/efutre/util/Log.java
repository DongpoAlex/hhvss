package com.efutre.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	public static final boolean	underDebug	= System.getProperty("DEBUG", "FALSE").equalsIgnoreCase("TRUE");

	public static String		logdir		= System.getProperty("LOGDIR", "./");

	private static String		charset		= "GBK";
	private static String		log_prefix	= "";
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	private static DateFormat dfday = new SimpleDateFormat("yyyy-MM-dd");

	public static synchronized boolean inform(String tag_magor, String tag_minor, String msg1, String msg2) {
		return logToFile('I', log_prefix, tag_magor, tag_minor, msg1, msg2);
	}

	public static synchronized boolean inform(String module, String message) {
		print('I', module, message);
		return logToFile('I', log_prefix, module, "", message);
	}

	public static synchronized boolean event(String module, String message) {
		print('I', module, message);
		return logToFile('I', log_prefix, module, "", message);
	}

	public static synchronized boolean error(String module, String code, String message) {
		if (PublicDefine.iferrorfile)
			logToFile('E', "Error", module, code, message);
		return logToFile('E', log_prefix, module, code, message);
	}

	public static synchronized void debug(String module, String message) {
		if (underDebug) {
			print('G', module, message);
			logToFile('G', log_prefix, module, "", message);
		}
	}

	public static synchronized boolean print(String module, String message) {
		return print('#', module, message);
	}

	public static synchronized boolean print(char event_level, String module, String message) {
		PrintStream pstrm = System.out;
		try {
			pstrm = new PrintStream(System.out, true, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			String str = df.format(new Date()) + event_level + " " + "<" + module + "> " + message;
			pstrm.println(str);
			return true;
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static synchronized void print(String message) {
		System.out.println(message);
	}

	private static boolean logToFile(char event_level, String fileHead, String module, String code, String message) {
		File file = new File(logdir + File.separatorChar + fileHead + dfday.format(new Date()) + ".log");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			if (!file.canWrite()) {
				return false;
			}

			FileOutputStream rwFile = new FileOutputStream(file.getCanonicalPath(), true);
			PrintStream pstrm = new PrintStream(rwFile, true, charset);
			try {
				String str = df.format(new Date()).toString()
						+ event_level
						+ " "
						+ "<"
						+ module
						+ "> "
						+ ((code == null) || (code.length() == 0) ? "" : new StringBuilder(" [").append(code).append(
								"]").toString()) + "{" + message + "}";

				pstrm.println(str);
				return true;
			} finally {
				rwFile.close();
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private static boolean logToFile(char event_level, String fileHead, String tag_magor, String tag_minor,
			String msg1, String msg2) {

		File file = new File(logdir + File.separatorChar + fileHead + dfday.format(new Date()) + ".log");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			if (!file.canWrite()) {
				return false;
			}

			FileOutputStream rwFile = new FileOutputStream(file.getCanonicalPath(), true);
			PrintStream pstrm = new PrintStream(rwFile, true, charset);
			try {
				String str = df.format(new Date()).toString()
						+ event_level
						+ " "
						+ "<"
						+ tag_magor
						+ ">"
						+ " "
						+ ((tag_minor == null) || (tag_minor.length() == 0) ? "" : new StringBuilder("<").append(
								tag_minor).append("> ").toString()) + msg1;
				if ((msg2 != null) && (msg2.length() > 0))
					str = str + "\t" + msg2;

				pstrm.println(str);
				print(str);
				return true;
			} finally {
				rwFile.close();
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static void setCharset(String charset_name) {
		charset = charset_name;
	}

	public static void setLogPrefix(String prefix4log) {
		log_prefix = prefix4log;
	}
}