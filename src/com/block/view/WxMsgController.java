package com.block.view;

import java.util.Date;

import com.block.common.CommonConstant;
import com.block.common.model.Coupon;
import com.block.common.model.Member;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
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
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

public class WxMsgController extends MsgController {

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

	/**
	 * 实现父类抽方法，处理消息推送
	 */
	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
//		OutTextMsg outMsg = new OutTextMsg(inTextMsg);
//		outMsg.setContent("欢迎使用尺尚公众平台!");
//		render(outMsg);
		renderNull();
	}

	@Override
	protected void processInImageMsg(InImageMsg inImageMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}
	
	@Override
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	/**
	 * 实现父类抽方法，处理关注/取消关注消息
	 */
	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		
		
		if(InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())){
			addMember(inFollowEvent.getFromUserName(),inFollowEvent.getEvent(),"");
			
			Coupon coupon=Coupon.dao.findById(CommonConstant.REGISTE_COUPON_ID);
			OutTextMsg outTextMsg=new OutTextMsg(inFollowEvent);
			if(coupon!=null&&"0".equals(coupon.getStatus())){
				outTextMsg.setContent("欢迎关注尺尚，感谢您的支持，首次关注点击<a href='"+CommonConstant.WEIXIN_DOMAIN+"member/memberInfo'>前往完善信息</a>可获赠"+coupon.getMoney()+"元现金券");
			}
			else{
				outTextMsg.setContent("欢迎关注尺尚，感谢您的支持，首次关注点击<a href='"+CommonConstant.WEIXIN_DOMAIN+"member/memberInfo'>前往完善信息</a>");
			}
			render(outTextMsg);
		}
		// 如果为取消关注事件，将无法接收到传回的信息
		if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent()))
		{
			Member member=Member.dao.getMember(inFollowEvent.getFromUserName());
			member.setStatus("1");
			member.setCancelTime(new Date());
			member.update();
			renderOutTextMsg("");
		}

	}

	private void addMember(String openid,String event,String fromUser){
		Member member=Member.dao.getMember(openid);
		if(InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(event) || InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(event)){
			ApiResult ar = UserApi.getUserInfo(openid);
			if(member == null && ar != null){
				
				member=new Member();
				member.setOpenid(openid);
				if(ar.get("unionid") != null){
					member.setUnionid(ar.get("unionid").toString());
				}
				member.setMemberPoint(0);
				member.setMemberLevel(1);
				member.setNickname(ar.get("nickname").toString());
				
				String newhead = ar.get("headimgurl") ;
				if(newhead !=null && !"".equals(newhead)){
					int s = newhead.lastIndexOf("/");
					int e= newhead.length();
					newhead = newhead.replace(newhead.substring(s, e),"/96");
				}
				
				member.setHeadUrl(newhead);
				member.setRegisterTime(new Date());
				
				if(fromUser != null && !"".equals(fromUser) && InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(event)){
					String fromid = fromUser.substring(8,fromUser.length());
					Member fromMember = Member.dao.findFirst("select * from t_member where id=?", fromid);
					/*存在根据fromMember无法获取商户的情况，需要判断*/
					if(fromMember != null){
						member.setFromUser(fromMember.getId());//推广来源
					}
				}
				Record record=Db.findFirst("select seqnextval(?) as seqval","membercode");
				Long membercode=record.getLong("seqval");
				member.setMemberCode(String.valueOf(membercode));
				member.setTotalmoney(0.0);
				member.save();
//				getSession().setAttribute("SESSION_MEMBER", member);
			}else{
				if(fromUser != null && !"".equals(fromUser) && InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(event)){
					String fromid = fromUser.substring(8,fromUser.length());
					Member fromMember = Member.dao.findFirst("select * from t_member where id=?", fromid);
					/*存在根据fromMember无法获取商户的情况，需要判断*/
					if(fromMember != null){
						member.setFromUser(fromMember.getId());//推广来源
					}
				}
				member.setStatus("0");
				member.update();//关注
			}
		}else if(member !=null &&  InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(event)){//带参数二维码扫描时间-已关注
			
		}else if(member !=null && InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(event)){//取消关注
			member.setStatus("1");
			member.setCancelTime(new Date());
			member.update();
		}
	}

	
	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		if(InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())){
			addMember(inQrCodeEvent.getFromUserName(),inQrCodeEvent.getEvent(),inQrCodeEvent.getEventKey());
			
			Coupon coupon=Coupon.dao.findById(CommonConstant.REGISTE_COUPON_ID);
			OutTextMsg outTextMsg=new OutTextMsg(inQrCodeEvent);
			if(coupon!=null&&"0".equals(coupon.getStatus())){
				outTextMsg.setContent("欢迎关注尺尚，感谢您的支持，首次关注点击<a href='"+CommonConstant.WEIXIN_DOMAIN+"member/memberInfo'>前往完善信息</a>可获赠"+coupon.getMoney()+"元现金券");
			}
			else{
				outTextMsg.setContent("欢迎关注尺尚，感谢您的支持，首次关注点击<a href='"+CommonConstant.WEIXIN_DOMAIN+"member/memberInfo'>前往完善信息</a>");
			}
			render(outTextMsg);
		}else if(InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent())){
			renderOutTextMsg("");
		}else{
			renderOutTextMsg("");
		}
	}

	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	/**
	 * 实现父类抽方法，处理菜单事件消息,推送订单追溯信息
	 */
	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		String key = inMenuEvent.getEventKey();
		if("TRACE_BACK".equals(key)){
			renderOutTextMsg("");
		}
	}

	@Override
	protected void processInSpeechRecognitionResults(
			InSpeechRecognitionResults inSpeechRecognitionResults) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInTemplateMsgEvent(
			InTemplateMsgEvent inTemplateMsgEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInShakearoundUserShakeEvent(
			InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInVerifySuccessEvent(
			InVerifySuccessEvent inVerifySuccessEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		// TODO Auto-generated method stub
		renderNull();
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
		renderNull();
	}

	@Override
	protected void processInUpdateMemberCardEvent(
			InUpdateMemberCardEvent inUpdateMemberCardEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInUserPayFromCardEvent(
			InUserPayFromCardEvent inUserPayFromCardEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processInMerChantOrderEvent(
			InMerChantOrderEvent inMerChantOrderEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent) {
		// TODO Auto-generated method stub
		renderNull();
	}

	@Override
	protected void processIsNotDefinedMsg(InNotDefinedMsg inNotDefinedMsg) {
		// TODO Auto-generated method stub
		renderNull();
	}

}
