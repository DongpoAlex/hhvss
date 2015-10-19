package com.efutre.config;

public class DBConfig {
	public DBConfig(String connURL, String driver, String username, String password) {
		super();
		this.connURL = connURL;
		this.driver = driver;
		this.username = username;
		this.password = password;
	}
	private String connURL;
	private String driver;
	private String username;
	private String password;
	public String getConnURL() {
		return this.connURL;
	}
	public void setConnURL(String connURL) {
		this.connURL = connURL;
	}
	public String getDriver() {
		return this.driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
