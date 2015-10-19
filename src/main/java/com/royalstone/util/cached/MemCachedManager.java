package com.royalstone.util.cached;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import com.royalstone.util.Log;
import com.royalstone.util.Utils;

public class MemCachedManager {
	private static MemcachedClient			mclient;
	private static MemcachedClientBuilder	builder			= null;
	private static String					connectINI		= Utils.getBaseIniPath() + "memcached.ini";
	public static boolean					enableMem		= true;
	private static String					memcachedServer	= "localhost:11211";
	private static int						iPoolSize		= 10;

	private MemCachedManager() {};

	static private void init() {
		File configFile = new File(connectINI);
		if (configFile.exists()) {
			FileInputStream fin;
			try {
				fin = new FileInputStream(configFile);
				Properties prop = new Properties();
				prop.load(fin);
				fin.close();
				String s = prop.getProperty("server");
				if (s != null) {
					s = s.trim();
					s = s.replaceAll(";", " ");
					memcachedServer = s;
				}
				s = prop.getProperty("poolsize");
				iPoolSize = Integer.parseInt(s);
				enableMem = true;
			} catch (FileNotFoundException e) {
				Log.logger.error("MemCachedManager 配置文件：" + e.getMessage());
			} catch (IOException e) {
				Log.logger.error("MemCachedManager 配置文件：" + e.getMessage());
			} catch (Exception e) {
				Log.logger.error("MemCachedManager：" + e.getMessage());
			}
		} else {
			enableMem = false;
		}
	}

	/**
	 * 返回mem客户端单例
	 * 
	 * @return
	 */
	static public MemcachedClient getMClientInstance() {
		if (enableMem && builder == null && mclient == null) {
			init();
			if (enableMem) {
				builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedServer));
				builder.setCommandFactory(new BinaryCommandFactory());
				builder.setConnectionPoolSize(iPoolSize);
				builder.setConnectTimeout(1000);
				try {
					mclient = builder.build();
				} catch (IOException e) {
					enableMem = false;
					Log.logger.error("MemCachedManager：" + e.getMessage());
				}
			}
		}
		return mclient;
	}

	/**
	 * 从缓存获取值
	 * 
	 * @param key
	 * @return
	 */
	static public Object get(String key) {
		Object obj = null;
		if (enableMem) {
			MemcachedClient mc = getMClientInstance();
			if (mc != null) {
				try {
					obj = mc.get(key);
				} catch (TimeoutException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				} catch (InterruptedException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				} catch (MemcachedException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				}
			}
		}
		return obj;
	}

	/**
	 * 设置对象到缓存
	 * 
	 * @param key
	 * @param object
	 *            对象
	 * @param timeout
	 *            自动销毁时间秒
	 * @return
	 */
	static public boolean set(String key, Object object, int timeOut) {
		boolean res = false;
		if (enableMem) {
			MemcachedClient mc = getMClientInstance();
			if (mc != null) {
				try {
					mc.set(key, timeOut, object);
					res = true;
				} catch (TimeoutException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				} catch (InterruptedException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				} catch (MemcachedException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				}
			}
		}

		return res;
	}

	/**
	 * 从缓存中删除指定对象
	 * 
	 * @param key
	 * @return
	 */
	static public boolean del(String key) {
		boolean res = false;
		if (enableMem) {
			MemcachedClient mc = getMClientInstance();
			if (mc != null) {
				try {
					mc.delete(key);
					res = true;
				} catch (TimeoutException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				} catch (InterruptedException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				} catch (MemcachedException e) {
					Log.logger.error("MemCachedManager get：" + e.getMessage());
				}
			}
		}
		return res;
	}

}
