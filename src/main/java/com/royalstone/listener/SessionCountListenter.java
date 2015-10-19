package com.royalstone.listener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionCountListenter implements HttpSessionListener{

	private int	currentSessionCount	= 0;//当前在线人数
	private int	maxSessionCount		= 0;//最大在线人数
	public void sessionCreated(HttpSessionEvent event) {
		currentSessionCount++;
		if (currentSessionCount > maxSessionCount) {
			maxSessionCount = currentSessionCount;
		}

		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();
		Object obj = application.getAttribute("sessionCounter");
		if (obj == null) {
			application.setAttribute("sessionCounter", this);
		}
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		currentSessionCount--;
	}
	public int getCurrentSessionCount() {
		return (currentSessionCount);
	}

	public int getMaxSessionCount() {
		return (maxSessionCount);
	}

}
