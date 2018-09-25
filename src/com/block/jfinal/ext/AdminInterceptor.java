package com.block.jfinal.ext;

//import org.eclipse.jetty.server.Authentication.User;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AdminInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
//		Controller controller = inv.getController();
//        User user = controller.getSessionAttr("User");
//        //判断登录条件是否成立(除了登录功能不拦截之外，其他都拦截)
//        if(user == null && !inv.getMethod().getName().equals("login")){
//            controller.render("/login.html");
//        }else{
//            inv.invoke();
//        }

	}

}
