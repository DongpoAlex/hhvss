package com.royalstone.vss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

/**
 * @author BaiJian
 */
public abstract class Config {

	// 站点列表
	protected Hashtable<Integer, Site> siteTable;
	
	//模块列表
	protected Hashtable<Integer,Module> moduleTable;

	protected Hashtable<String,Module> modulePathTable;

	// 记录集最大处理数
	protected int rowsLimitHard = 10000;
	// 记录最大输出数
	protected int rowsLimitSoft = 1000;
	// EXCEL最大输出数
	protected int excelLimitSoft = 3000;

	protected String printTobatoTitle = "";
	protected String printTobatoLogo = "../img/tobato_logo.jpg";
	protected int tobatoMajorid = 28;
	protected int medicaMajorid = 90;

	// 系统名称.将出现在登录界面
	protected String sysName = "";

	// 系统标题.将出现在web页面的标题
	protected String sysTitle = "供应商服务系统";

	// 系统英文名称
	protected String sysNameEng = "vss";

	// 公司名称
	protected String comName = "";
	
	public static String printSQL = "false"; // 将SQL语句打印在控制台
	public static String logSQL = "false"; // 将SQL语句记录在文件
	public static String outSQL = "false"; // 将SQL语句随XML一同输出
	
	//memcached 服务地址，如果为空则不启用memcached
	public static String memcachedServers =null;
	
	public static int sessionTimeOutS = 1800;//30分钟
	public static int sessionTimeOutM = sessionTimeOutS/60;//20分钟
	
	public static final boolean	isDebuging	= false;
	public static DateFormat	dfs			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	public static DateFormat	df			= new SimpleDateFormat("yyyy-MM-dd");
	public static String		logPath		= System.getProperty("LOGPATH", "./");
	

	public int getRowsLimitHard() {
		return this.rowsLimitHard;
	}

	public int getRowsLimitSoft() {
		return this.rowsLimitSoft;
	}

	public int getExcelLimitSoft() {
		return this.excelLimitSoft;
	}

	public String getPrintTobatoTitle() {
		return this.printTobatoTitle;
	}

	public int getTobatoMajorid() {
		return this.tobatoMajorid;
	}

	public String getSysName() {
		return this.sysName;
	}

	public String getSysTitle() {
		return this.sysTitle;
	}

	public String getSysNameEng() {
		return this.sysNameEng;
	}

	public String getComName() {
		return this.comName;
	}

	public String getPrintTobatoLogo() {
		return this.printTobatoLogo;
	}

	public Hashtable<Integer, Site> getSiteTable() {
		return this.siteTable;
	}

	public int getMedicaMajorid() {
		return this.medicaMajorid;
	}

	public void setMedicaMajorid(int medicaMajorid) {
		this.medicaMajorid = medicaMajorid;
	}
	
	public Hashtable<Integer, Module> getModuleTable() {
		return this.moduleTable;
	}

	public Hashtable<String, Module> getModulePathTable() {
		return this.modulePathTable;
	}
}
