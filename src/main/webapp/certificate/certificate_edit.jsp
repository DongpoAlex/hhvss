<%@ page contentType="text/html;charset=UTF-8" session="false"
         import="com.royalstone.certificate.bean.Config"
         import="com.royalstone.certificate.*"
         import="com.royalstone.myshop.basic.*"
         import="com.royalstone.security.*" import="com.royalstone.util.*"
         import="com.royalstone.util.daemon.*" import="java.sql.*"
         errorPage="../errorpage/errorpage.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession(false);
    if (session == null) throw new PermissionException("您尚未登录,或已超时.");
    Token token = (Token) session.getAttribute("TOKEN");
    if (token == null) throw new PermissionException("您尚未登录,或已超时.");

//查询用户的权限.
    final int moduleid = 8000002;
    Permission perm = token.getPermission(moduleid);
    if (!perm.include(Permission.READ))
        throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:" + moduleid);


    response.setHeader("progma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");

    String type = request.getParameter("type");
    String sheetid = request.getParameter("sheetid");
    String title = Config.getTypeName(type, token);

    Connection conn = null;
    Vender vender = null;
    try {
        conn = XDaemon.openDataSource(token.site.getDbSrcName());

        vender = Vender.getVender(conn, token.getBusinessid());

        if (vender == null) {
            throw new Exception("找不到该供应商");
        }
    } catch (Exception e) {
        throw e;
    } finally {
        XDaemon.closeDataSource(conn);
    }

    SelCertificateCategory csel = null;
    if (type.equals("2")) {
        csel = new SelCertificateCategory(token, "请选择");
        csel.setAttribute("id", "txt_ccid");
    }
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
          type="text/css"/>
    <link href="./css.css" rel="stylesheet" type="text/css"/>
    <link href="../css/jquery-ui-1.8.20.custom.css" rel="stylesheet"
          type="text/css"/>
    <script language="javascript" src="../js/Date.js"></script>
    <script language="javascript" src="../js/jquery.min.js"></script>
    <script language="javascript" src="../js/jquery-ui-1.8.20.custom.min.js"></script>
    <script language="javascript" src="../AW/runtime/lib/aw.js"></script>
    <script language="javascript" src="./common.js"></script>
    <script language="javascript" src="./certificate_edit.js"></script>
    <style>
    </style>
</head>
<body onload="init(<%=type%>,'<%=sheetid%>')">
<div class="nav">
    证照录入导航： <a href="./certificate_add.jsp?type=1" target="_self">基本证照</a>
    <a href="./certificate_add.jsp?type=2" target="_self">品类证照</a> <a
        href="./certificate_add.jsp?type=3" target="_self">旧品证照</a> <a
        href="./certificate_add.jsp?type=4" target="_self">新品证照</a>
</div>
<div class="main">
    <div class="title"><%=title %>
    </div>

    <div class="content">
        <table cellpadding="4px" cellspacing="1">
            <tr>
                <th>单据编码：</th>
                <td><input class="lineinput" type="text" size="16"
                           readonly="readonly" id="txt_sheetid"/></td>
                <th>单据状态：</th>
                <td><input class="lineinput" type="text" size="10"
                           readonly="readonly" id="txt_flag"/></td>
            </tr>
            <tr>
                <th>供应商编码：</th>
                <td><%=vender.venderid %>
                </td>
                <th>供应商名称：</th>
                <td><%=vender.vendername %>
                </td>
            </tr>
            <tr>
                <th>供应商地址：</th>
                <td colspan="3"><%=vender.address %>
                </td>
            </tr>
            <tr>
                <th>联系电话：</th>
                <td><input class="lineinput" type="text" size="20"
                           maxlength="32" id="txt_contacttel" readonly="readonly"/></td>
                <th>联系人：</th>
                <td><input class="lineinput" type="text" size="20"
                           maxlength="32" id="txt_contact" readonly="readonly"/>
                    <button id="btn_editVenderExt" class="button">修改联系人</button>
                </td>
            </tr>
        </table>
        <div class="tool" id="tool">
            <%
                if ("1".equals(type)) {
            %>
            供应商类型： <label for="voteoption1"> <input type="radio"
                                                    checked="checked" name="venderType" value="1" id="voteoption1"/>生产型
        </label> <label for="voteoption2"> <input type="radio"
                                                  name="venderType" value="2" id="voteoption2"/>生产型代理或贸易型
        </label> <input class="lineinput" type="text" size="16" value=""
                        id="txt_venderTypeName"> <input class="button"
                                                        disabled="disabled" type="button" value="更新类型名称"
                                                        onclick="updateVenderType()" id="btn_venderTypeName">
            <%
            } else if ("2".equals(type)) {
            %>
            索证品类选择：<%=csel %>
            供应商类型： <label for="voteoption1"> <input type="radio"
                                                    checked="checked" name="venderType" value="1" id="voteoption1"/>生产型
        </label> <label for="voteoption2"> <input type="radio"
                                                  name="venderType" value="2" id="voteoption2"/>生产型代理或贸易型
        </label> <input class="lineinput" type="text" size="16" value=""
                        id="txt_venderTypeName" maxlength="16"> <input
                class="button" disabled="disabled" type="button" value="更新类型名称"
                onclick="updateVenderType()" id="btn_venderTypeName">
            <%
                }
            %>

        </div>

        <div class="list" id="itemList"></div>
        <input class="button" type="button"
               onclick="uploadcreate(-1,<%=type%>)" value="增加证照" id="add_btn">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input
            class="button" type="button" onclick="venderSubmit()" value="提交证照"
            id="submit_btn">
        <% if (perm.include(Permission.DELETE)) {%>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input
            class="buttonred" onclick="delSheet()" type="button" value="删除本单"
            id="del_btn">
        <%}%>
    </div>
</div>

<div id="dialog-form" title="联系人信息">
    请填写贵公司负责证照资料维护的联系人姓名（以后会默认该联系人）
    <fieldset>
        <div>
            <label for="txt_newContact">联系人： </label> <input type="text"
                                                             name="txt_newContact" id="txt_newContact"
                                                             class="text ui-widget-content ui-corner-all"/>
        </div>
        <div>
            <label for="txt_newContactTel">联系电话：</label> <input type="text"
                                                                name="txt_newContactTel" id="txt_newContactTel"
                                                                class="text ui-widget-content ui-corner-all"/>
        </div>
    </fieldset>
</div>

</body>
</html>