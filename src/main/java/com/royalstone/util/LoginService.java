package com.royalstone.util;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

public class LoginService {
	public String getService(String username,String password){
		String strValue = "-1";
		try {
			String url = "http://192.168.10.8/webservice1.asmx";
			//String url = "http://113.140.27.50:81/webservice1.asmx";
			
			String namespace = "http://tempuri.org/";  
			String methodName = "Login";  
			String soapActionURI = "http://tempuri.org/Login"; 
			

			Service service = new Service();  
			Call call = (Call) service.createCall();  
			call.setUseSOAPAction(true);
			call.setTargetEndpointAddress(url);  
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
			call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
			call.setSOAPActionURI(soapActionURI);
			//call.setOperationName(methodName);
			call.setOperationName(new QName(namespace, methodName));  
			

			call.addParameter(new QName(namespace, "username"), XMLType.XSD_STRING,ParameterMode.IN);  
			call.addParameter(new QName(namespace, "password"), XMLType.XSD_STRING,ParameterMode.IN);  
			call.setReturnType(XMLType.XSD_STRING);  

			String[] str = new String[2];  
			str[0] = username;  
			str[1] = password;  
			
			Object obj = call.invoke(str);
			strValue = "{'SID':'"+obj.toString()+"','username':'"+username+"'}";
			System.out.println(strValue);
			return strValue;
		} catch (Exception e) {
			System.err.println(e.toString());
			return strValue;
		}

	}
}
