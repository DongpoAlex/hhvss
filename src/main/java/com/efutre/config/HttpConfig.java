package com.efutre.config;

public class HttpConfig {
	public HttpConfig(String serverNO,String serverName,String uri, String proxyHost, int proxyPort, String sender,int interval) {
		super();
		this.serverName = serverName;
		this.serverNO = serverNO;
		this.uri = uri;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.sender = sender;
		this.interval = interval;
	}
	final public String serverNO;
	final public String serverName;
	final public String uri;
	final public String proxyHost;
	final public int proxyPort;
	final public String sender;
	final public int interval;
}
