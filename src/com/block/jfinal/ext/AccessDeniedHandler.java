package com.block.jfinal.ext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.render.RenderFactory;

public class AccessDeniedHandler extends Handler {
	/**
	   * 拒绝访问的url
	   */
	  private String[] accessDeniedUrls;
	
	
	  public AccessDeniedHandler(String... accessDeniedUrls) {
	    this.accessDeniedUrls = accessDeniedUrls;
	  }
	  
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		if(checkView(target)) {
	      isHandled[0] = true;
	      RenderFactory.me().getErrorRender(403).setContext(request, response).render();
	      return;
	    }
	    next.handle(target, request, response, isHandled);
	}

	public boolean checkView(String viewUrl) {
	    if(accessDeniedUrls != null && accessDeniedUrls.length > 0) {
	      for (String url : accessDeniedUrls) {
	        if(url.equals(viewUrl)){
	          return true;
	        }
	      }
	    }
	    return false;
	  }
}
