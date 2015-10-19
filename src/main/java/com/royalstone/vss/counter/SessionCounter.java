package com.royalstone.vss.counter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionCounter implements HttpSessionListener
{
	private int totalSessionCount = 0;
	private int currentSessionCount = 0;
	private int maxSessionCount = 0;
	private ServletContext context = null;
	
	public void sessionCreated( HttpSessionEvent event )
	{
		totalSessionCount++;
		currentSessionCount++;
		if (currentSessionCount > totalSessionCount)
		{
			maxSessionCount = currentSessionCount;
		
		}
		if(context == null)
		{
			storeInServletContext(event);
		}
	}
	
	public void sessionDestroyed( HttpSessionEvent event )
	{
		currentSessionCount--;
	}
	
	public int getTotalSessionCount()
	{
		return (totalSessionCount);
	}
	
	public int getCurrentSessionCount()
	{
		return (currentSessionCount);
	}
	
	public int getMaxSessionCount()
	{
		return (maxSessionCount);
	}
	
	private void storeInServletContext(HttpSessionEvent event)
	{
		HttpSession session = event.getSession();
		context =session.getServletContext();
		context.setAttribute( "sessionCounter", this );
	}



	
}