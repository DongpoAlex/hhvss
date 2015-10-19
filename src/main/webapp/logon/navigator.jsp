<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.CtrlNavigator"
	import="com.royalstone.security.Token"
	import="com.royalstone.syslog.SyslogManager"
	import="com.royalstone.util.TokenException"
	import="com.royalstone.util.daemon.LogonAdm"
	import="com.royalstone.util.daemon.XDaemon"
	errorPage="../errorpage/errorpage.jsp" import="java.sql.Connection"%>
<%
    HttpSession session = request.getSession(false);
    if (session == null)
        throw new TokenException("您尚未登录,或已超时.");
    Token token = (Token) session.getAttribute("TOKEN");
    if (token == null)
        throw new TokenException("您尚未登录,或已超时.");

    CtrlNavigator nav;

    String s_menuid = request.getParameter("menuid");
    String s_moduleid = request.getParameter("moduleid");
    s_menuid = (s_menuid == null) ? "" : s_menuid.trim();
    s_moduleid = (s_moduleid == null) ? "" : s_moduleid.trim();

    if (s_menuid.length() > 0) {

        // 选择指定的菜单
        int menuid = Integer.parseInt(s_menuid);
        nav = new CtrlNavigator(token, menuid);

    } else if (s_moduleid.length() > 0) {

        // 直接指定模块号
        int moduleid = Integer.parseInt(s_moduleid);
        nav = new CtrlNavigator(token, 0, moduleid);

    } else {
        // 初次进入系统
        nav = new CtrlNavigator(token);
    }

    String action = nav.action();
    if (action != null && action.length() > 0) {

        action += "?cmid=" + nav.cmid();
        //增加一个可选参数operation，使得用户可以为调用页面传参
        String operation = request.getParameter("operation");
        if (operation != null) {
            action += "&" + operation;
        }

        //记录日志
        Connection conn = null;
        try {
            conn = XDaemon.openDataSource(token.site.getDbSrcName());
            SyslogManager log_manager = new SyslogManager(conn);
            log_manager.addInfo(token, nav.moduleid(),
                    nav.modulename(), request.getRemoteAddr(),
                    "LOAD_MODULE");
        } finally {
            XDaemon.closeDataSource(conn);
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title><%=token.site.getSiteName()%></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="keywords" content="实业,VSS,供应商,服务,零售商,富基融通,JAVA" />
<link rel="SHORTCUT ICON" href="favicon.ico" />
<link rel="stylesheet" type="text/css" media="screen"
	href="common/css/screen.css" />
<script language="javascript" src="../AW/runtime/lib/aw.js"></script>

<script language="javascript">
        var init_action = '<%=action%>';

        function init() {
            if (init_action != null && init_action != '')
                execute(init_action);
        }

        function load(menuid) {
            window.location.href = "navigator.jsp?menuid=" + menuid;
            execute("blank.htm");
        }

        function execute(action) {
            frmMain = parent.frames[1];
            frmMain.location.href = action;
        }

        function changeBusinessid(v) {
            alert("该操作将更换业务ID到" + v);
            var url = "../DaemonLogonVender?action=changeBusinessid&site=0&newBusinessid=" + v;
            setLoading(true, '正在处理，请稍后……');
            var table = new AW.XML.Table;
            table.setURL(url);
            table.request();//发送url请求
            table.response = function (text) {
                setLoading(false);
                table.setXML(text);
                if (table.getErrCode() != '0') {//处理xml中的错误消息
                    alert(table.getErrNote());
                    return;
                }
                window.location.reload();
            };
        }
    </script>
</head>
<body class="www" onload="init()">
	<div id="nav_head">
		<img alt="" src="../resources/images/main_f.png" width="470px"
			height="58px" style="position: absolute; left: -20px; top: 0px;" />

		<div id="nav_head_userinfo">

			<span>登陆用户:<%=token.loginid%></span>
			<%
            if (token.isVender) {
        %>
			<span>当前业务ID:<%=token.getBusinessid()%></span> <span>可选业务ID:<%=LogonAdm.getExtBusinessidForSelect(token)%></span>
			<%
            }
        %>
		</div>
		<%=nav.toString()%>
	</div>
</body>
</html>