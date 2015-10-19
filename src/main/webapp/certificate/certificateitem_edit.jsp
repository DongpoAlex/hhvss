<%@ page contentType="text/html;charset=UTF-8" session="false"
         import="com.royalstone.certificate.SelCertificateCategoryType"
         import="com.royalstone.certificate.SelCertificateType"
         import="com.royalstone.security.Permission"
         import="com.royalstone.security.Token" import="com.royalstone.util.PermissionException"
         import="com.royalstone.util.component.XComponent"
         errorPage="../errorpage/errorpage.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession(false);
    if (session == null) throw new PermissionException("您尚未登录,或已超时.");
    Token token = (Token) session.getAttribute("TOKEN");
    if (token == null) throw new PermissionException("您尚未登录,或已超时.");
    String vender = token.getBusinessid();

//查询用户的权限.
    final int moduleid = 8000002;
    Permission perm = token.getPermission(moduleid);
    if (!perm.include(Permission.READ))
        throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:" + moduleid);

    String type = request.getParameter("type");
    String sheetid = request.getParameter("sheetid");
    String id = request.getParameter("id");
    String filename = request.getParameter("filename");
    String venderid = request.getParameter("venderid");
    //初始值
    String flag = request.getParameter("flag");
    int f = Integer.parseInt((flag == null || flag.length() == 0) ? "0" : flag);
    String ctid = request.getParameter("ctid");
    String ccid = request.getParameter("ccid");
    String cname = request.getParameter("cname");
    String certificateid = request.getParameter("certificateid");
    String ctype = request.getParameter("ctype");
    String yeardate = request.getParameter("yeardate");
    String expirydate = request.getParameter("expirydate");
    String goodsname =request.getParameter("goodsname");
    String barcodeid = request.getParameter("barcodeid");
    String approvalnum = request.getParameter("approvalnum");
    String papprovalnum = request.getParameter("papprovalnum");
    String note =request.getParameter("note");
    ctid = (ctid == null || ctid.length() == 0) ? "0" : ctid;
    ccid = (ccid == null || ccid.length() == 0) ? "0" : ccid;
    cname = cname == null ? "" : cname;
    certificateid = certificateid == null ? "" : certificateid;
    ctype = ctype == null ? "" : ctype;
    yeardate = yeardate == null ? "" : yeardate;
    expirydate = expirydate == null ? "" : expirydate;
    goodsname = goodsname == null ? "" : goodsname;
    barcodeid = barcodeid == null ? "" : barcodeid;
    approvalnum = approvalnum == null ? "" : approvalnum;
    papprovalnum = papprovalnum == null ? "" : papprovalnum;
    note = note == null ? "" : note;

    //证照下拉列表
    XComponent sel = null;

    //控件显示
    String dis_ctid = "none";
    String dis_cname = "none";
    String dis_certificateid = "none";
    String dis_ctype = "none";
    String dis_yeardate = "none";
    String dis_expirydate = "none";
    String dis_goodsname = "none";
    String dis_barcodeid = "none";
    String dis_note = "block";
    String dis_approvalnum = "none";
    String dis_papprovalnum = "none";
    String readonly = "";
    if ("1".equals(request.getParameter("yearflag"))) {
        dis_yeardate = "block";
    } else {
        yeardate = "";
    }
    if ("1".equals(request.getParameter("appflag"))) {
        dis_approvalnum = "block";
        dis_papprovalnum = "block";
    } else if (("0".equals(request.getParameter("appflag"))) && ("1".equals(request.getParameter("whflag")))) {
        dis_papprovalnum = "block";
    }

    if ("1".equals(type)) {
        String[] flags = {"0"};
        sel = new SelCertificateType(token, flags, "请选择", ctid);
        sel.setAttribute("id", "txt_ctid");
        sel.setAttribute("onchange", "showYearDate(this);$('txt_ctype').innerText='基本证照'");
        dis_ctid = dis_certificateid = dis_ctype = dis_expirydate = "block";
    } else if ("2".equals(type)) {
        sel = new SelCertificateCategoryType(token, ccid, "请选择", ctid);
        sel.setAttribute("id", "txt_ctid");
        sel.setAttribute("onchange", "changeCtype(this)");
        dis_ctid = dis_certificateid = dis_ctype = dis_expirydate = "block";
    } else if ("3".equals(type)) {
        String[] flags = {"2"};
        sel = new SelCertificateType(token, flags, "请选择", ctid);
        sel.setAttribute("id", "txt_ctid");
        sel.setAttribute("onchange", "showYearDate(this);");
        dis_ctid = dis_certificateid = dis_expirydate = dis_barcodeid = dis_goodsname = "block";
        readonly = "readonly";
    } else if ("4".equals(type)) {
        String[] flags = {"2"};
        sel = new SelCertificateType(token, flags, "请选择", ctid);
        sel.setAttribute("id", "txt_ctid");
        sel.setAttribute("onchange", "showYearDate(this);");
        dis_ctid = dis_certificateid = dis_expirydate = dis_barcodeid = dis_goodsname = "block";
    }
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
          type="text/css"></link>
    <link href="./css.css" rel="stylesheet" type="text/css"/>
    <script language="javascript" src="../js/Date.js"></script>
    <script language="javascript" src="../AW/runtime/lib/aw.js"></script>
    <script language="javascript" src="./common.js"></script>
    <script language="javascript" src="./certificateitem_edit.js"></script>
    <script type="text/javascript">
        var g_sheetid = '<%=sheetid%>';
        var g_type = '<%=type%>';
        var g_ctid = '<%=ctid%>';
        var g_seqno = '<%=id%>';
        var g_venderid = '<%=venderid%>';
        var g_flag = '<%=flag%>';
    </script>
</head>
<body onload="init()">
<div class='detail'>
    <input id="isMdf" value="false" type="hidden"/>
    <table width="100%" border="0">
        <tr>
            <td>
                <div style='cursor: pointer;' id="divuploadIMG">
                    <img border="1" width='100' height='100' id='uploadIMG'
                         src='./images/loading.gif' onclick='openImg(this)'>
                </div>
            </td>
            <td>
                <div style='width: 100%; margin-left: 6px; margin-bottom: 8px;'>
						<span id='input_ctid'
                              style="margin-right:20px;float:left;display: <%=dis_ctid %>;"><%=sel %></span>
						<span id='input_cname'
                              style="margin-right:20px;float:left;display: <%=dis_cname %>">
							证照名称： <input class="lineinput" id='txt_cname' type='text'
                                         size='20' maxlength='64' value='<%=cname%>'/>
						</span> <span id='input_certificateid'
                                      style="margin-right:20px;float:left;display: <%=dis_certificateid %>">
							证照编码： <input class="lineinput" id='txt_certificateid' type='text'
                                         size='20' maxlength='64' value='<%=certificateid%>'/>
						</span> <span id='input_ctype'
                                      style="margin-right:20px;float:left;display: <%=dis_ctype %>">
							证照性质： <span class="lineinput" id='txt_ctype' class='xiahuaxian'><%=ctype%></span>
						</span> <span id='input_vender'
                                      style="margin-right: 20px; float: left; display: none"> <input
                        class="lineinput" id='txt_vender' type='text' size='8'
                        value='<%=vender%>'/>
						</span>
                </div>
                <div style='width: 100%; margin-left: 6px; margin-top: 4px;'>
						<span id='input_expirydate'
                              style="margin-right:20px;float:left;display: <%=dis_expirydate %>">
							有效期至： <input class="lineinput" id='txt_expirydate' type='text'
                                         size='8' onblur='checkDate(this)' value='<%=expirydate%>'/> <input
                                type="checkbox" onclick="maxYear(this)">无限期
						</span> <span id='input_yeardate'
                                      style="margin-right:20px;float:left;display: <%=dis_yeardate %>">
							年审日期： <input class="lineinput" id='txt_yeardate' type='text'
                                         size='8' onblur='checkDate(this)' value='<%=yeardate%>'/>
						</span> <span id='input_barcodeid'
                                      style="margin-right:20px;float:left;display: <%=dis_barcodeid %>">
							商品条码： <input onchange="getGoods(this)" class="lineinput"
                                         id='txt_barcodeid' type='text' size='20' maxlength='64'
                                         value='<%=barcodeid%>'/>
						</span> <span id='input_goodsname'
                                      style="margin-right:20px;float:left;display: <%=dis_goodsname %>">
							商品名称： <input class="lineinput" id='txt_goodsname' type='text'
                                         size='20' maxlength='64' value='<%=goodsname%>' <%=readonly %> />
						</span> <span id='input_approvalnum'
                                      style="margin-right:20px;float:left;display: <%=dis_approvalnum %>">
							批文号： <input class="lineinput" id='txt_approvalnum' type='text'
                                        size='16' maxlength='64' value='<%=approvalnum%>'/>
						</span> <span id='input_papprovalnum'
                                      style="margin-right:20px;float:left;display: <%=dis_papprovalnum %>">
							生产日期： <input class="lineinput" id='txt_papprovalnum' type='text'
                                         size='16' maxlength='64' value='<%=papprovalnum%>'/>
						</span> <span id='input_note' style="float:left;display: <%=dis_note %>">
							备注： <input class="lineinput" style="color: red" id='txt_note'
                                       type='text' size='30' maxlength='128' value='<%=note%>'
                                       readonly="readonly"/>
						</span>
                </div>
                <div class="line"></div>
                <div style='width: 100%; margin-left: 6px; margin-top: 8px;'>
                    <%
                        if (f <= 0) {
                    %>
                    <input class="button" id="edit_btn" value="修改文字信息" type="button"
                           onclick="initEditItem()" style="display: none;"/> <input
                        class="button" id="save_btn" value="保存文字信息" type="button"
                        onclick="saveItem()" style="display: none;"/> <input
                        class="button" id="images_btn" value="维护图片" type="button"
                        onclick="imgmore()"/> <input class="button" onclick="delItem()"
                                                     type="button" value="删除本条" id="del_btn">
                    <%
                        } else {
                            out.print("<a style='float:left;margin-right:20px;' target='_blank' href='certificate_detail.jsp?sheetid=" + sheetid + "&seqno=" + id + "&type=" + type + "'><img src='images/detail.gif'></a>");
                            if (flag.equals("0")) {
                                out.print("<span style='color: yellow'>未提交</span>");
                            } else if (flag.equals("1")) {
                                out.print("<span style='color: olive'>已提交等待审核</span>");
                            } else if (flag.equals("100")) {
                                out.print("<span style='color: blue'>审核通过</span>");
                            } else if (flag.equals("-1")) {
                                out.print("<span style='color: red'>审核返回</span>");
                            } else if (flag.equals("-10")) {
                                out.print("状态：<span style='color: red'>年审预警</span>");
                            } else if (flag.equals("-11")) {
                                out.print("状态：<span style='color: red'>过期预警</span>");
                            } else if (flag.equals("-100")) {
                                out.print("<span style='color: red'>过期作废</span>");
                            } else {
                                out.print("未定义类型");
                            }
                        }
                    %>
                </div>
            </td>
        </tr>
    </table>
</div>
</body>
</html>