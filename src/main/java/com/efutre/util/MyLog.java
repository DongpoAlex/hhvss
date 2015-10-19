package com.efutre.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.efutre.config.Config;


public class MyLog {
	static Log				log			= LogFactory.getLog("ACC");
	static String			charset		= "GBK";
	static SimpleDateFormat	formatter	= new SimpleDateFormat("yyyyMMdd");
	static SimpleDateFormat	formatter2	= new SimpleDateFormat("yyyyMMdd HH:mm:ss.S");

	static synchronized public void print(String str) {
		str = formatter2.format(new Date()) + " " + str;
		try {
			PrintStream pstrm = new PrintStream(System.out, true, charset);
			pstrm.println(str);
		} catch (UnsupportedEncodingException e) {
			System.out.println(str);
		}
	}

	static synchronized public void info(String str) {
		str = formatter2.format(new Date()) + " " + str;
		try {
			PrintStream pstrm = new PrintStream(System.out, true, charset);
			pstrm.println(str);
		} catch (UnsupportedEncodingException e) {
			System.out.println(str);
		}
		write(str);
	}

	static synchronized public void err(String str) {
		str = formatter2.format(new Date()) + " " + str;
		log.error(str);
		writeErr(str);
		info(str);
	}

	static synchronized public void debug(String str) {
		str = formatter2.format(new Date()) + " " + str;
		log.debug(str);
	}

	static private void write(String str) {
		String filePatch = Config.logPath + "/";
		String fileName = filePatch + formatter.format(new Date()) + ".log";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {}
		}
		FileHandle.appendLineStringToFile(str + "\r\n", fileName, "GBK");
	}

	static private void writeErr(String str) {
		String filePatch = Config.logPath + "/";
		String fileName = filePatch + "err." + formatter.format(new Date()) + ".log";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {}
		}
		FileHandle.appendLineStringToFile(str + "\r\n", fileName, "GBK");
	}
}
