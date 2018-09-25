package com.block.jfinal.ext;

import java.util.HashSet;
import java.util.Set;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class WallInterceptor implements Interceptor {
	Set<String> set=new HashSet<String>();
		
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		if(set.contains(c.getRequest().getRequestURI())){
			String clean = new HTMLInputFilter().filter(c.getPara(""));
			c.setAttr("", clean);
		}
	}

}
