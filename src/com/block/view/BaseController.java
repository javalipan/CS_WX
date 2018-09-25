package com.block.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.block.common.model.LoginHistory;
import com.block.common.model.Member;
import com.block.common.model.ShoppingCar;
import com.block.utils.RandomStringGenerator;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InNotDefinedMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.event.InCustomEvent;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InLocationEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMassEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMerChantOrderEvent;
import com.jfinal.weixin.sdk.msg.in.event.InNotDefinedEvent;
import com.jfinal.weixin.sdk.msg.in.event.InPoiCheckNotifyEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InShakearoundUserShakeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InSubmitMemberCardEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.in.event.InUpdateMemberCardEvent;
import com.jfinal.weixin.sdk.msg.in.event.InUserPayFromCardEvent;
import com.jfinal.weixin.sdk.msg.in.event.InUserViewCardEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifyFailEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifySuccessEvent;
import com.jfinal.weixin.sdk.msg.in.event.InWifiEvent;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;

public class BaseController extends MsgController {
	
	static final Logger log = Logger.getLogger(BaseController.class);
	final public String SESSION_MEMBER = "SESSION_MEMBER";
	final public String SESSION_SHOPPINGCAR = "SESSION_SHOPPINGCAR";
	
	
	/** 
	* @Title: getUser 
	* 获取微信绑定的会员，如果没有则插入
	* @return Member    会员信息 
	* @throws 
	*/
	public Member getMember(){
//		String ua = this.getRequest().getHeader("user-agent").toLowerCase();
//		if(ua.indexOf("micromessenger") > 0){// 是微信浏览器
			if(getSession().getAttribute(SESSION_MEMBER)!=null){		//如果session存在则直接返回session
				Member member=(Member) getSession().getAttribute(SESSION_MEMBER);
				return member;
			}else{		//session不存在则前往微信授权登陆
				String code=getPara("code");
				try{
					if(code==null){		//code不存在，前往微信网页授权登陆
						String redirectUrl=getRequest().getRequestURL().toString();
						Map<String,String[]> map=getRequest().getParameterMap();
						Set<String> keys=map.keySet();
						for(String key:keys){
							String val=getPara(key);
							if(redirectUrl.indexOf("?")>=0){
								redirectUrl+="&"+key+"="+val;
							}else{
								redirectUrl+="?"+key+"="+val;
							}
						}
						redirectUrl=URLEncoder.encode(redirectUrl,"utf-8");
	
						String orderReqStateS = RandomStringGenerator.getRandomStringByLength(32);
						String loginurl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ApiConfigKit.getApiConfig().getAppId()+"&redirect_uri="+redirectUrl+"&response_type=code&scope=snsapi_base&state="+ orderReqStateS + "#wechat_redirect";
						log.debug("loginurl:"+loginurl);
						getResponse().sendRedirect(loginurl);
						renderNull();
					}else{		//code存在未微信授权登陆之后返回
						System.out.println("**code**:"+code);
						SnsAccessToken sat = SnsAccessTokenApi.getSnsAccessToken(ApiConfigKit.getApiConfig().getAppId(), ApiConfigKit.getApiConfig().getAppSecret(), code);
						System.out.println("SnsAccessTokenApi.getSnsAccessToken:"+sat.getJson());
						if(sat==null||StringUtils.isBlank(sat.getOpenid())){
							return null;
						}
						ApiResult ar = UserApi.getUserInfo(sat.getOpenid());
						System.out.println("UserApi.getUserInfo:"+ar.getJson());
						String newhead = ar.get("headimgurl") ;
						if(StringUtils.isNotBlank(newhead)){
							int s = newhead.lastIndexOf("/");
							int e= newhead.length();
							newhead = newhead.replace(newhead.substring(s, e),"/96");
						}
						
						//根据微信返回的信息到数据库查询会员信息
						Member member= Member.dao.findFirst("select * from t_member where Openid=?", sat.getOpenid());
						
						if(member!=null && !"".equals(member.getOpenid())){	//会员存在则直接返回
							log.debug("会员存在");
							member.setNickname(ar.get("nickname").toString());
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
							getSession().setAttribute(SESSION_MEMBER, member);
							ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
							getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
							
							LoginHistory loginHistory=new LoginHistory();
							loginHistory.setMemberId(member.getId());
							loginHistory.setLoginTime(new Date());
							loginHistory.setIpAddr(getRequest().getRemoteAddr());
							loginHistory.save();
							
							return member;
						}else{		//会员不存在则插入
							log.debug("会员不存在");
							
							member=new Member();
							member.setOpenid(sat.getOpenid());
							if(ar.get("unionid") != null){
								member.setUnionid(ar.get("unionid").toString());
							}
							member.setNickname(ar.get("nickname")==null?"":ar.get("nickname").toString());
							if(StringUtils.isNotBlank(newhead)){
								member.setHeadUrl(newhead);
							}
							if(StringUtils.isBlank(member.getMemberCode())){
								Record record=Db.findFirst("select seqnextval(?) as seqval","membercode");
								Long membercode=record.getLong("seqval");
								member.setMemberCode(String.valueOf(membercode));
							}
							if(ar.getInt("subscribe")==0){
								member.setStatus("1");
							}
							member.setMemberPoint(0);
							member.setMemberLevel(1);
							member.setTotalmoney(0.0);
							member.setRegisterTime(new Date());
							
							member.save();
							
							LoginHistory loginHistory=new LoginHistory();
							loginHistory.setMemberId(member.getId());
							loginHistory.setLoginTime(new Date());
							loginHistory.setIpAddr(getRequest().getRemoteAddr());
							loginHistory.save();
	
							getSession().setAttribute(SESSION_MEMBER, member);
							
							ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
							getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
							
							return member;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
//		}else{//非微信浏览器
//			log.debug("非微信浏览器");
//			if(getSession().getAttribute(SESSION_MEMBER)!=null){
//				log.debug("session存在则直接返回member");
//				Member member=(Member) getSession().getAttribute(SESSION_MEMBER);
//				return member;
//			}else{//session不存在则前往微信授权登陆
//				
//				return null;
//			}
//		}
		return null;
	}
	
	@Override
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();
    	// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		
		/**
		 *  是否对消息进行加密，对应于微信平台的消息加解密方式：
		 *  1：true进行加密且必须配置 encodingAesKey
		 *  2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		
		return ac;
	}
	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInImageMsg(InImageMsg inImageMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInSpeechRecognitionResults(
			InSpeechRecognitionResults inSpeechRecognitionResults) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInTemplateMsgEvent(
			InTemplateMsgEvent inTemplateMsgEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInShakearoundUserShakeEvent(
			InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInVerifySuccessEvent(
			InVerifySuccessEvent inVerifySuccessEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInPoiCheckNotifyEvent(
			InPoiCheckNotifyEvent inPoiCheckNotifyEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInWifiEvent(InWifiEvent inWifiEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInUserViewCardEvent(
			InUserViewCardEvent inUserViewCardEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInSubmitMemberCardEvent(
			InSubmitMemberCardEvent inSubmitMemberCardEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInUpdateMemberCardEvent(
			InUpdateMemberCardEvent inUpdateMemberCardEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInUserPayFromCardEvent(
			InUserPayFromCardEvent inUserPayFromCardEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processInMerChantOrderEvent(
			InMerChantOrderEvent inMerChantOrderEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processIsNotDefinedMsg(InNotDefinedMsg inNotDefinedMsg) {
		// TODO Auto-generated method stub
		
	}
	
	public static Map<String, String> jsSign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        System.out.println(string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }
	
	private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    
}
