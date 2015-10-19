package com.royalstone.vss.main.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;

/**
 * @author baij
 *         主类，所有访问基于该类
 */
public abstract class MainService implements IMainService {
	protected final HttpServletRequest	request;
	protected final Connection			conn;
	protected final Token				token;
	protected final String				operation;
	protected final int					sid;

	public MainService(HttpServletRequest request, Connection conn, Token token) {
		super();
		this.request = request;
		this.conn = conn;
		this.token = token;
		operation = request.getParameter("operation");
		
		// 强制为总部sid 0
		this.sid = 0;

	}

	/*
	 * (non-Javadoc)
	 * @see com.royalstone.vss.main.base.IMainService#execute()
	 */
	public Element execute() throws Exception {
		try {
			Method method = getDeclaredMethod(this, operation);
			if (method == null) {
				throw new Exception(this.getClass().getName() + "服务方法没有指定：" + operation);
			}
			Element elm = (Element) method.invoke(this);
			return elm;
		} catch (InvocationTargetException e) {
			if (e.getCause() == null) {
				throw e;
			} else {
				throw new Exception(e.getCause().getMessage());
			}
		}
	}

	public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
		Method method = null;
		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {}
		}
		return null;
	}

	/**
	 * xml格式
	 * 
	 * @return
	 */
	public Element toElement() {
		return new Element("null");
	}

	public Element toHTML() {
		return new Element("HTML").addContent(toElement());
	}

	/**
	 * 强制增加venderid查询条件
	 * 
	 * @param parms
	 */
	protected void addVenderid(Map<String, String[]> parms) {
		if (token.isVender) {
			// 强制venderid参数
			parms.remove("venderid");
			parms.put("venderid", new String[] { token.getBusinessid() });
		}
	}

	protected Map<String, String[]> getParams() {
		Map<String, String[]> parms = new HashMap<String, String[]>(request.getParameterMap());
		addVenderid(parms);
		return parms;
	}

	final protected Document getParamDoc() throws JDOMException, IOException {
		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "UTF-8");
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(reader);
		reader.close();
		return doc;
	}
	
	/**
	 * 获取POST方法字符
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	final protected String getPOSTString() throws UnsupportedEncodingException, IOException{
		BufferedReader  reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		String lines;
        StringBuffer sb = new StringBuffer("");
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes());
            sb.append(lines);
        }
        return sb.toString();
	}


	final protected String getParamNotNull(String key) {
		String res = request.getParameter(key);
		if (res == null) {
			throw new InvalidDataException(key + " is null, Please invalid this value.");
		}
		return res;
	}

	public String excel(File file, String cmid) {
		return null;
	}
	
	
	/**
	 * 小工具，将jsonArray转换为<数组值，数字序号>
	 * @param jsNames
	 * @return
	 */
	protected HashMap<String, Integer> parseNames(JSONArray jsNames){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < jsNames.length(); i++) {
			map.put(jsNames.optString(i), i);
		}
		return map;
	}
}
