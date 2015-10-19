package com.royalstone.listener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.royalstone.security.Token;

/**
 * Application Lifecycle Listener implementation class OnlineUserListener
 * 
 */
public class OnlineUserListener implements HttpSessionBindingListener,Serializable {
	
	private Token token;
	
	public OnlineUserListener(Token token){
		this.token = token;
	}

	/**
	 * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();

		// 把用户名放入在线列表
		Map onlineUserMap = (Map) application.getAttribute("onlineUserMap");
		// 第一次使用前，需要初始化
		if (onlineUserMap == null) {
			onlineUserMap = new Hashtable();
			application.setAttribute("onlineUserMap", onlineUserMap);
		}
		onlineUserMap.put(session.getId(), session);
		System.out.println(SimpleDateFormat.getDateTimeInstance().format(new Date())+"用户："+token.username+"登陆。");
	}

	/**
	 * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();

		// 从在线列表中删除用户名
		Map onlineUserMap = (Map) application.getAttribute("onlineUserMap");
		if(onlineUserMap ==null)
			return;
		onlineUserMap.remove(session.getId());
		System.out.println(SimpleDateFormat.getDateTimeInstance().format(new Date())+"用户："+token.username+"注销。");
	}
}
