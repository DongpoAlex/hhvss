<%@page contentType="text/html;charset=UTF-8" session="false"
        import="com.royalstone.security.Permission" import="com.royalstone.security.Token"
        import="com.royalstone.util.PermissionException"
        errorPage="../errorpage/errorpage.jsp" %>
<%
    final int moduleid = 3010121;
%>
<%
    request.setCharacterEncoding("UTF-8");

    HttpSession session = request.getSession(false);
    if (session == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);
    Token token = (Token) session.getAttribute("TOKEN");
    if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

    //查询用户的权限.
    Permission perm = token.getPermission(moduleid);
    if (!perm.include(Permission.READ))
        throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title></title>

    <link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet" type="text/css">
    <link href="../css/main.css" rel="stylesheet" type="text/css">

    <script type="text/javascript" src="../AW/runtime/lib/aw.js"></script>
    <script type="text/javascript" src="../js/date/WdatePicker.js"></script>
    <script type="text/javascript" src="../js/common.js"></script>
    <script type="text/javascript" src="../js/Sheet.js"></script>
    <style type="text/css">
        .aw-strict .aw-grid-control {
            width: 99%;
            height: 500px;
            background-color: #F9F8F4;
        }

        .aw-grid-cell {
            border-right: 1px solid threedshadow;
            hepadding-bottom: 3px;
            padding-top: 3px;
        }

        .aw-grid-row {
            border-bottom: 1px solid #ccc;
            font-size: 14px;
            height: 22px;
        }

        .aw-column-0 {
            color: blue;
            cursor: pointer;
            width: 120px;
        }

        #shuiyin {
            font-size: 200px;
            width: 100%;
            text-align: center;
            position: absolute;
            top: 50%;
            letter-spacing: 200px;
            filter: progid:DXImageTransform.Microsoft.Alpha(style=0, opacity=50,
            finishOpacity=50);
            display: none;
        }
    </style>
    <script type="text/javascript">
        var s = new Sheet("Supsettle");
        window.onload = function () {
            s.init();
            s.allowPrint();
            s.disabledRead();
            s.disabledConfirm();
            s.open_win_print = function () {
                /*var flag = this._table4detail.getXMLText("xdoc/xout/sheet/head/row/flag");
                 if(flag!='G'){
                 alert("仅审核状态结算单可打印。");
                 return;
                 }*/
                var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0" +
                        ",width=" + (window.screen.width - 10) + ",height=" + (window.screen.height - 80);
                window.open("../page/print.jsp?cmid=" + s.cmid + "&clazz=Supsettle&sheetid=" + s.current_sheetid, s.current_sheetid, attributeOfNewWnd);

            };
        };
    </script>
</head>
<body>
<div id="div_tabs" style="width: 100%;"></div>
<div id="div1"></div>
<div id="div2" style="display: none;">
    <div id="div_navigator" style="margin: 4px;">
        <input type="button" value="上一单" onclick="s.sheet_navigate(-1)"/> <input
            type="button" value="下一单" onclick="s.sheet_navigate(1)"/> <input
            type="button" value="打印单据" onclick="s.open_win_print()"
            style="display: none" id="btn_print"/> <span id="offset_current"></span>
    </div>
    <div id="div_warning" class="warning"></div>
    <div id="div_sheetshow"></div>
</div>

<div id="div0" class="search_main">
    <% if (!token.isVender) { %>
    <div class="search_parms">
        供应商编码: <input type="text" id="txt_venderid" name="txt_parms"
                      notnull="notnull" alt="供应商编码"/>
    </div>
    <% } %>

    <div class="search_parms">
        单据状态:<select id="txt_status" name="txt_parms">
        <option>全部</option>
        <option value="W">结算报批</option>
        <option value="G" selected="selected">结算审核</option>
        <option value="T">付款完成</option>
    </select>
    </div>
    <div class="search_parms">
        结算日期: <input type="text" id="txt_editdate_min" class="Wdate"
                     onFocus="WdatePicker()" name="txt_parms" alt="最小日期"/> - <input
            type="text" id="txt_editdate_max" class="Wdate"
            onFocus="WdatePicker()" name="txt_parms" alt="最大日期"/>
    </div>
    <div class="search_parms">
        结算单号: <input type="text" id="txt_sheetid" size="20" name="txt_parms"/>
    </div>
    <div class="search_button" id="div_button_search"></div>
</div>
</body>
</html>
