package com.royalstone.vss.sheet;

import com.royalstone.security.Token;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;
import org.jdom.Element;

import java.sql.Connection;
import java.util.Map;

public class Supsettle extends SheetService {
    static String tableName = "fin_supsettle_ih";
    String sql4Body2;
    String sql4Body4;
    String sql4Body5;
    String sql4Body6;

    public Supsettle(Connection conn, Token token, String cmid) {
        super(conn, token, cmid, 3020210, "3020210001", "3020210002", tableName);
        super.catTableName = "";

        int sid = token.site.getSid();
        sql4Body2 = SqlMapLoader.getInstance().getSql(sid, 3020210003L).toString();
        sql4Body4 = SqlMapLoader.getInstance().getSql(sid, 3020210004L).toString();
        sql4Body5 = SqlMapLoader.getInstance().getSql(sid, 3020210005L).toString();
        sql4Body6 = SqlMapLoader.getInstance().getSql(sid, 3020210006L).toString();
    }

    public Filter cookFilter(Map parms) {
        Filter filter = new Filter();
        String[] ss = null;

        //filter.add("a.flag NOT IN ('C','T') ");

        ss = (String[]) parms.get("venderid");

        if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
            //如果是父供应商查询，加载子供应商
//			if(token.isVender && token.getBusinessid().equals(token.getDefbusinessid())){
//				ss = (String[])token.getBusinessidSet().toArray(ss);
//			}
            Values val_vender = new Values(ss);
            filter.add("a.supid IN (" + val_vender.toString4String() + ") ");
        }

        /**
         * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
         */
        ss = (String[]) parms.get("sheetid");
        if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
            Values val_sheetid = new Values(ss);
            filter.add("a.billno IN (" + val_sheetid.toString4String() + ") ");
            return filter;
        }

        ss = (String[]) parms.get("status");
        if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
            Values val_sheetid = new Values(ss);
            /*filter.add("a.flag IN (" + val_sheetid.toString4String() + ") ");*/
            filter.add("decode(a.flag,'C','W','I','G',a.flag) IN (" + val_sheetid.toString4String() + ") ");
        }

        ss = (String[]) parms.get("editdate_min");
        if (ss != null && ss.length > 0) {
            String date = ss[0];
            filter.add("trunc(a.thisdate) >= " + ValueAdapter.std2mdy(date));
        }

        ss = (String[]) parms.get("editdate_max");
        if (ss != null && ss.length > 0) {
            String date = ss[0];
            filter.add("trunc(a.thisdate) <= " + ValueAdapter.std2mdy(date));
        }
        return filter;
    }

    public Element setSheetInfo(String sheetid, String sheettype) {
        ColModel cm = ColModelLoader.getInstance().getCM(token.site.getSid(), cmid);
        String title = cm.getTitle();
        String logo = "../img/crv_logo.jpg";

        String xslview = cm.getXslView();
        String xslprint = cm.getXslPrint();

        String sql = "select btype from " + tableName + " where billno=? and btype=?";
        String btype = SqlUtil.queryPS4SingleColumn(conn, sql, new String[]{sheetid, sheettype}).get(0);
        xslview = btype + xslview;
        xslprint = btype + xslprint;

        Element elm_sheet = new Element("sheet");
        elm_sheet.setAttribute("title", title);
        elm_sheet.setAttribute("logo", logo);
        elm_sheet.setAttribute("xsl", token.site.toXSLPatch(xslview));
        elm_sheet.setAttribute("xslprint", token.site.toXSLPatch(xslprint));
        return elm_sheet;
    }

    @Override
    public Element veiw(String sheetid) {

        String id = sheetid.substring(3, sheetid.length());
        String sheettype = sheetid.substring(0, 3);

        Element elm_sheet = setSheetInfo(id, sheettype);

        elm_sheet.addContent(getHead(id, sheettype));
        elm_sheet.addContent(getBody(id, sheettype));
        elm_sheet.addContent(SqlUtil.getRowSetElement(conn, sql4Body2, new Object[]{id}, "body2"));
        elm_sheet.addContent(SqlUtil.getRowSetElement(conn, sql4Body4, new Object[]{id}, "body4"));
        elm_sheet.addContent(SqlUtil.getRowSetElement(conn, sql4Body5, new Object[]{id}, "body5"));
        elm_sheet.addContent(SqlUtil.getRowSetElement(conn, sql4Body6, new Object[]{id}, "body6"));
        return elm_sheet;
    }

    private Element getHead(String sheetid, String sheettype) {
        return SqlUtil.getRowSetElement(conn, sql4Head, new Object[]{sheetid}, "head");
    }

    private Element getBody(String sheetid, String sheettype) {
        return SqlUtil.getRowSetElement(conn, sql4Body, new Object[]{sheetid}, "body");
    }

    public String getVenderid(String sheetid) {

        String id = sheetid.substring(3, sheetid.length());
        String sheettype = sheetid.substring(0, 3);

        String sql = " select supid from " + tableName + " a where a.billno=?";
        return SqlUtil.queryPS4SingleColumn(conn, sql, new Object[]{id}).get(0);
    }
}
