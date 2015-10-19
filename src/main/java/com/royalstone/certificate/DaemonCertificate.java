/**
 * 
 */
package com.royalstone.certificate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * @author BaiJian
 */
public class DaemonCertificate extends XDaemon {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2551779319492026807L;

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		this.check4Gzip(request);
		request.setCharacterEncoding( "UTF-8" );
		Element elm_doc = new Element( "xdoc" );
        Element elm_out = new Element( "xout" );
    	Connection conn = null;
	    try {
	    	/**
	    	 * 检查是否已登录
	    	 */
	        if( ! this.isSessionActive( request ) ) {
	        	throw new PermissionException( PermissionException.LOGIN_PROMPT );
	        }
	        Token token = this.getToken( request );
	        
	        String venderid = token.getBusinessid();
	        
	    	conn = openDataSource(token.site.getDbSrcName());
	    	String action= request.getParameter("action");
	    	if(action==null || action.length()==0){
	    		throw new Exception("action is null");
	    	}
	    	
	    	CertificateService service = new CertificateService(request, conn, token);
	    	
	    	if(action.equals("addHead")){
	    		elm_out.addContent(service.addHead());
	    	//修改证照表头
	    	}else if(action.equals("editHead")){
	    		service.editHead();
	    		//删除单据
	    	}else if(action.equals("delSheet")){
	    		if(token.isVender){
	    			service.venderDelSheet();
	    		}else{
	    			service.delSheet();
	    		}
	    	//删除证照商品明细
	    	}else if(action.equals("delSheetItem")){	
	    		if(token.isVender){
	    			service.venderDelItem();
	    		}else{
	    			service.delSheetItem();
	    		}
	    	//修改证照表体
	    	}else if(action.equals("editItem")){
	    		elm_out.addContent(service.editItem());
	    	}else if(action.equals("venderSubmit")){
	    		service.venderSubmit();
	    	//审核表体OK
	    	}else if(action.equals("checkitemOK")){
	    		service.checkItemOK();
	    	//审核表体NO
	    	}else if(action.equals("checkitemNO")){
	    		service.checkItemNO();
	    	//审核整单
	    	}else if(action.equals("checkAllOK")){	
	    		service.checkHeadOK();
	    	//浏览列表
	    	}else if(action.equals("getList")){	
	    		elm_out.addContent(service.searchVenderList());
	    	}else if(action.equals("getDetailList")){	
	    		elm_out.addContent(service.searchVenderDetailList());
	    	//
	    	}else if(action.equals("getplcount")){	
		    	elm_out.addContent(service.getplcount());	
		    //重复性检查
	    	}else if(action.equals("checkitemList")){	
		    	elm_out.addContent(service.searchcheckitemList());	
	    	}else if(action.equals("checkimageList")){	
		    	elm_out.addContent(service.searchcheckimage());	
	    	//返回证照明细list
	    	}else if(action.equals("show")){
	    		elm_out.addContent(service.getDetailList());
	    	//返回单条证照明细
	    	}else if(action.equals("showDetail")){
	    		elm_out.addContent(service.getDetail());
	    		
	    	//证照种类控件
	    	}else if(action.equals("selct")){
	    		String flag= request.getParameter("flag");
	    		String[] flags = {flag};
	    		SelCertificateType type = new SelCertificateType(token,flags);
	    		elm_out.addContent(type.getElmCtrl());
	    	//证照品类控件
	    	}else if(action.equals("selcc")){
	    		SelCertificateCategory c = new SelCertificateCategory(token);
	    		elm_out.addContent(c.getElmCtrl());
	    	//证照品类-种类关系控件
	    	}else if(action.equals("selctc")){
	    		String ccid= request.getParameter("ccid");
	    		SelCertificateCategoryType c = new SelCertificateCategoryType(token,ccid,"","");
	    		elm_out.addContent(c.getElmCtrl());
	    		
	    	//取得类型列表	
	    	}else if(action.equals("getCT")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getCTList());
	    	}else if(action.equals("getCTByFlag")){
	    		int flag= Integer.parseInt(request.getParameter("flag"));
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getCTList(flag));
	    		
	    	//取得品类列表
	    	}else if(action.equals("getCC")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getCCList());
	    		
	    	//取得类型品类关系列表	
	    	}else if(action.equals("getCTC")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getCTCList());
	    	}else if(action.equals("getCTCByCCID")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getCTCByCCID());
	    	}else if(action.equals("addCT")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.addCT();
	    	}else if(action.equals("addCC")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.addCC();
	    	}else if(action.equals("updateCC")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.updateCC();
	    	}else if(action.equals("updateCT")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.updateCT();
	    	}else if(action.equals("addCTCByCCID")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.insertCTCByCCID();
	    	}else if(action.equals("delCTCByCCID")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.delCTCByCCID();
	    		
	    	//多图片添加
	    	}else if(action.equals("addImg")){
	    		service.addImage();
	    	}else if(action.equals("editImg")){
	    		service.editImage();
	    	}else if(action.equals("delImg")){
	    		service.delImage();
	    	}else if(action.equals("getImage")){
	    		elm_out.addContent(service.getImage());
	    	}else if(action.equals("getImageList")){
	    		elm_out.addContent(service.getImageList());
	    		
	    		
	    		//供应商扩展信息
	    	}else if(action.equals("getVenderExt")){
	    		elm_out.addContent(service.getVenderExt());
	    	}else if(action.equals("setVenderExt")){
	    		Document doc = this.getParamDoc(request);
	    		Element root = doc.getRootElement();
	    		Element xparms = root.getChild("xparms");
	    		service.setVenderExt(xparms);
	    	//修改供应商类型名称
	    	}else if(action.equals("updateVenderTypeName")){
	    		service.updateVenderTypeName();
	    		//供应商关系
	    	}else if(action.equals("updateVenderRelation")){
	    		Document doc = this.getParamDoc(request);
	    		Element root = doc.getRootElement();
	    		Element xparms = root.getChild("xparms");
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.updateVenderRelation(xparms);
	    	}else if(action.equals("getVenderRelation")){
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getVenderRelation());
	    	}else if(action.equals("getVRCCList")){
	    		venderid = request.getParameter("venderid");
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		elm_out.addContent(ss.getVRCCList(venderid));
	    		//更新某个cc下面的关系
	    	}else if(action.equals("updateVenderCCRelation")){
	    		Document doc = this.getParamDoc(request);
	    		Element root = doc.getRootElement();
	    		Element xparms = root.getChild("xparms");
	    		BaseCertificateService ss = new BaseCertificateService(request,conn,token);
	    		ss.updateVenderRelationByCC(xparms);
	    	}else if(action.equals("getVenderCertificateList")){
	    		//如果是零售商，供应商ID通过前台URL参数来确定
	    		String temp = request.getParameter("venderid");
	    		if(!token.isVender && temp!=null)
	    			venderid = temp;
	    		
	    		elm_out.addContent(service.getVenderCertificateList(venderid));
	    	}else if(action.equals("getTask")){
	    		elm_out.addContent(service.getTask(venderid));
	    	}else if(action.equals("getVendersByCCID")){
	    		elm_out.addContent(new BaseCertificateService(request,conn,token).getVendersByCCID());
	    	
	    		//已审核供应商清单
	    	}else if(action.equals("getCheckedVenderList")){
	    		elm_out.addContent(service.getCheckedVenderList());
	    	}else if(action.equals("getWarnVenderCertificateList")){
	    		elm_out.addContent(service.getWarnVenderCertificateList());
	    	}else{
	    		throw new Exception("无法识别的动作");
	    	}
	    	
	    	elm_doc.addContent(elm_out);
	    	elm_doc.addContent(new XErr(0,"OK").toElement());
		} catch (SQLException e) {
			e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() );
	    } catch (Exception e) {
	    	e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
        	output( response, elm_doc );
	    	closeDataSource(conn);
        }
		
	}

}
