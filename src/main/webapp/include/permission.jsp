<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.vss.*" errorPage="../errorpage/errorpage.jsp"%>

<%
	String uri = request.getRequestURI();
	String path = request.getContextPath();
	String modulePath = ".."+uri.substring(path.length(), uri.length());
	Module module = (Module) (VSSConfig.getInstance().getModulePathTable().get(modulePath));
	
	int moduleid = 0;
	if(module != null){
		moduleid = module.getModuleID();
		/* if(module.getRoleTypeID()==2){
			if(!token.isVender){
				throw new PermissionException( "该模块为供应商专用！" );
			}
		} */
	}
%>