package com.block.view;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.block.common.CommonConstant;
import com.block.common.model.Content;
import com.block.common.model.Goods;
import com.block.common.model.Member;
import com.block.common.model.MemberCoupon;
import com.block.common.model.Region;
import com.block.utils.CacheUtil;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.MenuApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;

public class MainController extends BaseController {
	
	private static final String COUPON_CNT="COUPON_CNT";
	
	public void index(){
		Member member=(Member) getSession().getAttribute(SESSION_MEMBER);
		if(member==null){
			String ua = getRequest().getHeader("user-agent").toLowerCase();
			if(ua.indexOf("micromessenger") > 0){// 是微信浏览器
				member=getMember();
			}
		}
		
		List<Content> contents=Content.dao.find("select * from t_content where programId=? order by sortno desc",CommonConstant.IMG_PROGRAM_ID);
		setAttr("contents", contents);
		
		List<Content> notices=Content.dao.find("select * from t_content where programId=? order by sortno desc",CommonConstant.NOTICE_PROGRAM_ID);
		setAttr("notices", notices);
		
		String type=getPara("type");
		if(StringUtils.isBlank(type)){
			type="1";
		}
		List<Goods> goodsList=null;
		if("1".equals(type)){	//查询推荐
			goodsList=Goods.dao.getRecomend(member==null?null:member.getId(), 20);
		}
		else if("2".equals(type)){	//折扣
			Goods goods=new Goods();
			goods.setStatus("1");
			goods.setIsDiscount("2");
			goodsList=Goods.dao.searchGoods(goods, "sortno asc", 0, 20);
		}
		else {	//新品
			Goods goods=new Goods();
			goods.setStatus("1");
			goods.setIsnew("1");
			goodsList=Goods.dao.searchGoods(goods, "sortno asc", 0, 20);
		}
		setAttr("goodsList", goodsList);
		setAttr("type", type);
		
		if(getRequest().getHeader("user-agent").toLowerCase().contains("micromessenger")){		//微信浏览器重写分享
			JsTicket jsTicket=JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
			String url = getRequest().getScheme() + "://" + getRequest().getServerName() + getRequest().getRequestURI();
			if (getRequest().getQueryString() != null) {
				url += "?" + getRequest().getQueryString();
			}
			Map<String, String> jsparas=jsSign(jsTicket.getTicket(), url);
			jsparas.put("jsapi_ticket", jsTicket.getTicket());
			jsparas.put("appId", ApiConfigKit.getApiConfig().getAppId());
			jsparas.put("link", CommonConstant.WEIXIN_DOMAIN);
			setAttr("jsparas", jsparas);
		}
		
		if(member!=null){
			if(StringUtils.isNotBlank(member.getHeadUrl())&&!member.getHeadUrl().startsWith("http")){
				String headPrev=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort();
				member.setHeadUrl(headPrev+member.getHeadUrl());
			}
			setAttr("member", member);
			
			Integer cntstr=(Integer) getRequest().getSession().getAttribute(COUPON_CNT);
			if(cntstr==null){
				List<MemberCoupon> memberCoupons=MemberCoupon.dao.find("select * from t_member_coupon where memberId=? and status=0 and startTime<=now() and endTime>=now()",member.getId());
				getRequest().getSession().setAttribute(COUPON_CNT,memberCoupons.size());
				setAttr("memberCoupons", memberCoupons);
				
				int totalamount=0;
				if(memberCoupons!=null&&memberCoupons.size()>0){
					for(MemberCoupon mc:memberCoupons){
						totalamount+=mc.getMoney();
					}
				}
				setAttr("totalamount", totalamount);
				
			}
			else{
				List<MemberCoupon> memberCoupons=MemberCoupon.dao.find("select * from t_member_coupon where memberId=? and status=0 and startTime<=now() and endTime>=now()",member.getId());
				getRequest().getSession().setAttribute(COUPON_CNT,memberCoupons.size());
				if(cntstr!=memberCoupons.size()){
					setAttr("memberCoupons", memberCoupons);
					
					int totalamount=0;
					if(memberCoupons!=null&&memberCoupons.size()>0){
						for(MemberCoupon mc:memberCoupons){
							totalamount+=mc.getMoney();
						}
					}
					setAttr("totalamount", totalamount);
					
				}
			}
		}
    	
		renderVelocity("index.vm");
	}
	
	/**
	 * 创建菜单
	 */
	public void createMenu()
	{
		String str="{"
				+"    \"button\": ["
				+"	{"
				+"		\"type\": \"view\","
				+"	    \"name\": \"在线商城\","
				+"		\"url\": \"http://weixin.karlay.cn\""
				+"	},"
				+"	{"
				+"		\"type\": \"view\","
				+"	    \"name\": \"往期回顾\","
				+"		\"url\": \"http://weixin.karlay.cn/reportList\""
				+"	},"
				+"	{"
				+"	    \"name\": \"我的\","
				+"	    \"sub_button\": ["
				+"		{"
				+"		    \"type\": \"view\","
				+"		    \"name\": \"个人中心\","
				+"		    \"url\": \"http://weixin.karlay.cn/member/memberCenter\""
				+"		},"
				+"		{"
				+"		    \"type\": \"view\","
				+"		    \"name\": \"我的订单\","
				+"		    \"url\": \"http://weixin.karlay.cn/member/memberCenter\""
				+"		},"
				+"		{"
				+"		    \"type\": \"view\","
				+"		    \"name\": \"我的收藏\","
				+"		    \"url\": \"http://weixin.karlay.cn/member/myCollect\""
				+"		},"
				+"		{"
				+"		    \"type\": \"view\","
				+"		    \"name\": \"我的购物车\","
				+"		    \"url\": \"http://weixin.karlay.cn/order/gotoShoppingCar\""
				+"		}"
				+"	    ]"
				+"	}"
				+"    ]"
				+"}";
		System.out.println(str);
		ApiResult apiResult = MenuApi.createMenu(str);
		if(apiResult.isSucceed()){
			renderText(apiResult.getJson());
		}else{
			renderText(apiResult.getErrorMsg());
		}
	}
	
	@Before(MsgInterceptor.class)
	private void getFollowers()
	{
		ApiResult apiResult = UserApi.getFollows();
		renderText(apiResult.getJson());
	}
	
	public void getChildrenRegion(){
		String pid=getPara("pid");
		List<Region> regions=Region.dao.find("select * from t_region where SuperiorCode=?", pid);
		renderJson(regions);
	}
	
	public void setTestSession(){
		String openid = "oYNT31FDZkMiAF0_sbVjVjjvGbkI";//suifeng
		Member m = Member.dao.findFirst("select * from t_member where Openid=?", openid);
		getSession().setAttribute(SESSION_MEMBER, m);
		renderNull();
	}
	
	public void updateUser(){
		String openid=getPara("openid");
		Member member=Member.dao.findFirst("select * from t_member where Openid=?", openid);
		ApiResult result=UserApi.getUserInfo(openid);
		if(result.isSucceed()){
			String newhead = result.get("headimgurl") ;
			if(newhead !=null && !"".equals(newhead)){
				int s = newhead.lastIndexOf("/");
				int e= newhead.length();
				newhead = newhead.replace(newhead.substring(s, e),"/96");
			}
			
			member.setNickname(result.get("nickname").toString());
			if(StringUtils.isNotBlank(newhead)){
				member.setHeadUrl(newhead);
			}
			if(StringUtils.isBlank(member.getMemberCode())){
				Record record=Db.findFirst("select seqnextval(?) as seqval","membercode");
				Long membercode=record.getLong("seqval");
				member.setMemberCode(String.valueOf(membercode));
			}
			if(member.getTotalmoney()==null){
				member.setTotalmoney(0.0);
			}
			member.update();
		}
		renderNull();
	}
	
	public void reportList(){
		List<Content> contents=Content.dao.find("select * from t_content where programId=? order by createTime desc",CommonConstant.REPORT_PROGRAM_ID);
		setAttr("contents", contents);
		
		render("reportList.vm");
	}
	
	/**
	 * 清除缓存
	 */
	public void clearAllCache(){
		CacheUtil.clearAllCache();
	}
	
	public void getUserInfo(){
		String openid=getPara("openid");
		ApiResult apiResult=UserApi.getUserInfo(openid);
		renderJson(apiResult);
	}
	
}
