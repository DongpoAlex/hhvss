﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.sql.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.admin.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9010501;
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new TokenException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new TokenException("您尚未登录,或已超时.");

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);

	String module_id = request.getParameter("moduleid");
	if (module_id != null && module_id.length() > 0) {
		int i = Integer.parseInt(module_id);
		module_id = "" + i;
	} else {
		module_id = "-1";
	}
%>


<%
	Connection conn = XDaemon.openDataSource(token.site.getDbSrcName());
	ModuleAdm adm = new ModuleAdm(conn);
	Module module = adm.getModule(Integer.parseInt(module_id));
	XDaemon.closeDataSource(conn);
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang="zh-cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/system.css" type="text/css" />

<xml id="island4req" />
<xml id="island4reply" />
<xml id="island4result" />
<xml id="format4moduleperm" src="format4moduleperm.xsl"></xml>

<script src="../js/ajax.js">
	
</script>
<script src="../js/XErr.js">
	
</script>
<script src="Authority.js">
	
</script>

<script>
	var moduleid_original =<%=module_id%>;

	// 全局对象, 记录模块权限.
	var authority_list = new AuthorityList();

	// 用户修改权限时调用此函数, 修改JS对象 authority_list.
	function clickbox(ctrl) {
		var value = (ctrl.checked) ? "1" : "0";
		var lst = authority_list.permission_list;
		for (var i = 0; i < lst.length; i++) {
			var au = lst[i];
			if (au.roleid == ctrl.roleid && au.moduleid == ctrl.moduleid) {
				au.setAttribute(ctrl.operation, value);
				window.status = "Match: " + au.roleid + "  " + au.moduleid;
			}
		}
		btn_save.disabled = false;
	}
</script>

<script>
	function init() {
		refresh();
	}

	function quit() {
		if (btn_save.disabled) {
			window.close();
		} else {
			var force = confirm(" 你作的修改尚未保存. 是否退出? ");
			if (force)
				window.close();
		}
	}

	function clear_info() {
		divMain.innerHTML = "";
		btn_save.disabled = true;
	}

	// 根据用户输入的模块号, 刷新显示信息, 包括模块的基本信息和权限信息.
	function refresh() {
		divMain.innerHTML = "";
		load_perm(<%=module_id%>);
		return;
	}

	// 此模块从后台取指定模块的权限信息.
	function load_perm(moduleid) {
		window.status = "Load...  " + moduleid;
		if (moduleid == null || moduleid.length == 0) {
			divMain.innerHTML = "请输入模块编码...";
			return;
		}

		try {
			island4reply.async = false;
			island4reply.load("../DaemonAuthority?action=list4module&moduleid="
					+ moduleid);
			var elm_err = island4reply.XMLDocument
					.selectSingleNode("/xdoc/xerr");

			var xerr = parseXErr(elm_err);
			if (xerr.code != "0") {
				alert(xerr.toString());
				return;
			}

			// 解析后台发来的帐套信息,并设置控件的值.
			if (xerr.code == "0") {
				var elm_lst = island4reply.XMLDocument
						.selectSingleNode("/xdoc/authority_list");
				authority_list = parseAuthorityList(elm_lst);
				divMain.innerHTML = island4reply
						.transformNode(format4moduleperm.documentElement);
			}
		} catch (e) {
			alert(e);
		}
	}

	// 保存权限数据后, 此函数分析后台返回的数据.
	function analyse4save(text) {
		island4result.loadXML(text);
		var elm_err = island4result.XMLDocument.selectSingleNode("/xdoc/xerr");
		var xerr = parseXErr(elm_err);
		if (xerr.code == "0") {
			alert("保存成功!");
			btn_save.disabled = true;
		} else {
			alert("保存失败: " + xerr.toString());
		}
	}

	// 此函数保存用户修改后的权限信息.
	function save() {
		window.status = "Save permission for module " + moduleid_original;
		island4req.loadXML("");
		var doc = island4req.XMLDocument;
		var elm_root = doc.selectSingleNode("/");
		var elm_req = authority_list.toElement(doc);
		elm_root.appendChild(elm_req);

		var url = "../DaemonAuthority?action=save4module&moduleid="
				+ moduleid_original;
		var courier = new AjaxCourier(url);
		courier.island4req = island4req;
		courier.reader.read = analyse4save;
		courier.call();
	}
</script>

</head>

<body onload="init()">

	<div>
		<label> 模块编号 </label>
		<%=module.moduleid%>
		<label> 模块名称 </label>
		<%=module.modulename%>
		<label> 模块路径 </label>
		<%=module.action%>

	</div>


	<input type="button" name="btn_save" disabled value=" 保存修改 "
		onclick="save()" />
	<input type="button" value=" 返回 " onclick="window.history.back()" />
	<br />
	<br />
	<br />
	<div id="divMain"></div>
	<br />


</body>
</html>
