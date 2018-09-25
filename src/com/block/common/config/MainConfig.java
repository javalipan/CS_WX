package com.block.common.config;

import com.block.common.CommonConstant;
import com.block.common.model._MappingKit;
import com.block.utils.InitParamHandler;
import com.block.view.AddressController;
import com.block.view.AttachmentController;
import com.block.view.CooperateController;
import com.block.view.CouponController;
import com.block.view.DataController;
import com.block.view.GoodsController;
import com.block.view.MainController;
import com.block.view.MemberController;
import com.block.view.OrderController;
import com.block.view.PaymentController;
import com.block.view.VelocityLayoutRender.VelocityLayoutRenderFactory;
import com.block.view.WxMsgController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;

public class MainConfig extends JFinalConfig {
	
	private void loadProp(String pro, String dev) {
		try {
			PropKit.use(pro);
		}
		catch (Exception e) {
			PropKit.use(dev);
		}
	}
	
	/**
	 * 配置JFinal常量
	 */
	@Override
	public void configConstant(Constants me) {
		//读取数据库配置文件
		loadProp("config1.properties", "config_dev.properties");
		//设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		//设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath("upload/temp/");
		//设置上传最大限制尺寸
		me.setMaxPostSize(1024*1024*10);
		//设置默认下载文件路径 renderFile使用
//		me.setBaseDownloadPath("upload/cards/");
		//设置默认视图类型
		me.setBaseViewPath("/WEB-INF/tmp");
		me.setViewType(ViewType.VELOCITY);
		//设置404渲染视图
		me.setError401View("/WEB-INF/tmp/errors/401.vm");
		me.setError403View("/WEB-INF/tmp/errors/403.vm");
		me.setError404View("/WEB-INF/tmp/errors/404.vm");
		me.setError500View("/WEB-INF/tmp/errors/500.vm");
		
		ApiConfigKit.setDevMode(PropKit.getBoolean("devMode"));
		me.setMainRenderFactory(new VelocityLayoutRenderFactory());
	}
	
	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		me.add("/", MainController.class,"/");
		me.add("/data", DataController.class,"/");
		me.add("/wxmsg", WxMsgController.class,"/");
		me.add("/goods", GoodsController.class,"/");
		me.add("/member", MemberController.class,"/");
		me.add("/order", OrderController.class,"/");
		me.add("/wxpay", PaymentController.class,"/");
		me.add("/cooperate", CooperateController.class,"/");
		me.add("/coupon", CouponController.class,"/");
		me.add("/atta", AttachmentController.class,"/");
		me.add("/addr", AddressController.class,"/");
	}
	
	/**
	 * 配置JFinal插件
	 * 数据库连接池
	 * ORM
	 * 缓存等插件
	 * 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		//配置数据库连接池插件
		C3p0Plugin c3p0Plugin=new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		//添加到插件列表中
		me.add(c3p0Plugin);
		//缓存插件
		me.add(new EhCachePlugin(PathKit.getRootClassPath()+"/ehcache.xml"));
		
		//orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp=new ActiveRecordPlugin(c3p0Plugin);
		arp.setShowSql(PropKit.getBoolean("devMode"));
		arp.setDialect(new MysqlDialect());
		
		/********在此添加数据库 表-Model 映射*********/
		_MappingKit.mapping(arp);
		me.add(arp);
		
	}
	
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new SessionInViewInterceptor(false));
		me.add(new MsgInterceptor());
//		me.add(new WallInterceptor());
	}
	
	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
		me.add(new InitParamHandler("ftppath",CommonConstant.FILE_DOMAIN));
	}
	
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);
	}

}
