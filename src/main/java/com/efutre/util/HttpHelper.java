package com.efutre.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.efutre.config.Config;
import com.efutre.config.HttpConfig;


public class HttpHelper {
	public static String get(HttpConfig cf) throws IOException {
		String res = "";
		GetMethod getMethod = new GetMethod(cf.uri);
		HttpClient client = new HttpClient();

		if (cf.proxyHost != null && cf.proxyHost.length() > 0) {
			// 设置代理
			HostConfiguration httpConfig = client.getHostConfiguration();
			httpConfig.setProxy(cf.proxyHost, cf.proxyPort);
		}
		

		// 禁止转向
		getMethod.setFollowRedirects(false);

		// 默认的恢复策略，在发生异常时候将自动重试3次
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			// 执行
			int statusCode = client.executeMethod(getMethod);
			// 判断返回状态
			if (statusCode != HttpStatus.SC_OK) { throw new HttpException("Http method failed: "
					+ getMethod.getStatusLine()); }
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			// 处理内容
			res = new String(responseBody, Config.recviceCharCode);
		}
		catch (HttpException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			getMethod.releaseConnection();
		}
		return res;
	}

	public static String post(HttpConfig cf,String body) throws IOException {
		String res = "";
		HttpClient client = new HttpClient();
		if (cf.proxyHost != null && cf.proxyHost.length() > 0) {
			// 设置代理
			HostConfiguration httpConfig = client.getHostConfiguration();
			httpConfig.setProxy(cf.proxyHost, cf.proxyPort);
		}
		

		PostMethod postMethod = new PostMethod(cf.uri);
		postMethod.setRequestBody(body);
		postMethod.setRequestHeader("Content-type", "text/xml;charset="+Config.recviceCharCode);
		// 禁止转向
		postMethod.setFollowRedirects(false);

		// 默认的恢复策略，在发生异常时候将自动重试3次
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			// 执行
			int statusCode = client.executeMethod(postMethod);
			// 判断返回状态
			if (statusCode != HttpStatus.SC_OK) { throw new HttpException("Http method failed: "
					+ postMethod.getStatusLine()); }
			// 读取内容
			// 处理内容
			//res = postMethod.getResponseBodyAsString();
			InputStream resStream = postMethod.getResponseBodyAsStream();   
			BufferedReader br = new BufferedReader(new InputStreamReader(resStream,Config.recviceCharCode));   
	        StringBuffer resBuffer = new StringBuffer();   
	        String resTemp = "";   
	        while((resTemp = br.readLine()) != null){   
	            resBuffer.append(resTemp+"\r\n");   
	        }   
	        res = resBuffer.toString();   
	        br.close();
		}
		catch (HttpException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			postMethod.releaseConnection();
		}
		return res;
	}
}
