package com.block.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class InitParamHandler extends Handler{

	private String key;
	private String value;
	
	public InitParamHandler (String key,String value){
		this.key=key;
		this.value=value;
	}
	
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		request.setAttribute(key, value);
		next.handle(target, request, response, isHandled);
	}

}
