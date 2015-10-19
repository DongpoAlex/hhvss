package com.royalstone.vss.counter;
import java.io.*;
import java.util.*; 
import javax.servlet.http.*; 


public class SessionListener implements HttpSessionBindingListener 
{ 

	public String privateinfo=""; 
	private String logstring=""; 
	private int count=0;

	public SessionListener(String info){ 
		this.privateinfo=info; 
	} 

	public int getcount(){ 
		return count; 
	} 

	public void valueBound(HttpSessionBindingEvent event) 
	{ 
		count++; 
		if (privateinfo.equals("count")) 
		{ 
			return; 
		} 
		try{ 
			Calendar calendar=new GregorianCalendar(); 
			System.out.println("login:"+privateinfo+" time:"+calendar.getTime()); 
			logstring="\nlogin:"+privateinfo+" time:"+calendar.getTime()+"\n"; 
			for(int i=1;i<1000;i++){ 
				File file=new File("yeeyoo.log"+i); 
				if(!(file.exists())) 
				file.createNewFile(); 
				if(file.length()>1048576)
					continue; 
				FileOutputStream foo=new FileOutputStream("yeeyoo.log"+i,true);
				foo.write(logstring.getBytes(),0,logstring.length()); 
				foo.close(); 
				break;
			} 
		}catch(FileNotFoundException e){} 
		catch(IOException e){} 
	} 

	public void valueUnbound(HttpSessionBindingEvent event) 
	{ 
		count--; 
		if (privateinfo.equals("count")) 
		{ 
			return; 
		} 
		try{ 
			Calendar calendar=new GregorianCalendar(); 
			System.out.println("logout:"+privateinfo+" time:"+calendar.getTime()); 
			logstring="\nlogout:"+privateinfo+" time:"+calendar.getTime()+"\n"; 
			for(int i=1;i<1000;i++){ 
			File file=new File("yeeyoo.log"+i); 
			if(!(file.exists())) 
			file.createNewFile();
			if(file.length()>1048576) 
			continue; 
			FileOutputStream foo=new FileOutputStream("yeeyoo.log"+i,true);
			foo.write(logstring.getBytes(),0,logstring.length()); 
			foo.close(); 
			break;
		} 
		}catch(FileNotFoundException e){} 
		catch(IOException e){} 
	}


}
