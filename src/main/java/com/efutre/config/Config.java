package com.efutre.config;

public class Config {

	static final public String logPath = System.getProperty("user.dir")+"/logs/";
	static final public String tempVoucherPath = System.getProperty("user.dir")+"/tmp/";
	static final public String errVoucherPath = System.getProperty("user.dir")+"/err/";
	static final public String successVoucherPath = System.getProperty("user.dir")+"/success/";
	
	static final public String sendCharCode = "UTF-8";
	static final public String recviceCharCode = "UTF-8";
	static final public int sleeptime = 1;
}
