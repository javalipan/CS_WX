package com.block.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.block.common.CommonConstant;
import com.block.common.model.BalanceRecord;
import com.block.common.model.Coupon;
import com.block.common.model.Goods;
import com.block.common.model.LevelSetting;
import com.block.common.model.Member;
import com.block.common.model.MemberCoupon;
import com.block.common.model.PointLog;
import com.block.utils.NumberUtil;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.QrcodeApi;

public class MemberController extends BaseController{

	/**
	 * 会员中心
	 */
	public void memberCenter(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		else{
			member=Member.dao.findById(member.getId());
			if(StringUtils.isNotBlank(member.getHeadUrl())&&!member.getHeadUrl().startsWith("http")){
				String headPrev=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort();
				member.setHeadUrl(headPrev+member.getHeadUrl());
			}
			setAttr("member", member);
			setSessionAttr(SESSION_MEMBER, member);
		}
		
		LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
			
		
		Record record=Db.findFirst("select (select count(1) from t_order where memberid=? and isPay=0) unpayed,(select count(1) from t_order where memberid=? and isPay=1 and isSend=0) unsended,(select count(1) from t_order where memberid=? and isPay=1 and isSend=1 and isReceive=0) sended,(select count(1) from t_order where memberid=? and isPay=1 and isSend=1 and isReceive=1) finished",member.getId(),member.getId(),member.getId(),member.getId());
		Record couponCnt=Db.findFirst("select count(1) couponCnt from t_member_coupon where memberId=? and status=0 and startTime<=now() and endTime>=now()",member.getId());
		
		setAttr("record", record);
		setAttr("levelSetting", levelSetting);
		setAttr("couponCnt", couponCnt.getLong("couponCnt"));
		renderVelocity("member/memberCenter.vm");
	}
	
	/**
	 * 我的会员等级
	 */
	public void memberGrade(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		else{
			member=Member.dao.findById(member.getId());
			setSessionAttr(SESSION_MEMBER, member);
		}
		
		setAttr("member", member);
		
		List<LevelSetting> levelSettings=LevelSetting.dao.find("select * from t_level_setting");
		
		LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
		
		setAttr("levelSetting", levelSetting);
		setAttr("levelSettings", levelSettings);
		
		LevelSetting nextLevel=LevelSetting.dao.findFirst("select * from t_level_setting where id=?",levelSetting.getId()+1);
		if(nextLevel!=null){
			setAttr("remaining",NumberUtil.toFixed(nextLevel.getLevelstart()-member.getTotalmoney(), 2));
			setAttr("nextLevel",nextLevel);
		}
		
		renderVelocity("member/memberGrade.vm");
	}
	
	/**
	 * 个人信息
	 */
	public void memberInfo(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		String headPrev=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort();
		if(member != null && !"".equals(member.getOpenid())) {
			if(StringUtils.isNotBlank(member.getHeadUrl())&&!member.getHeadUrl().startsWith("http")){
				member.setHeadUrl(headPrev+member.getHeadUrl());
			}
			setAttr("headPrev", headPrev);
			setAttr("member", member);
			
//			List<Region> regions=Region.dao.find("select * from t_region where SuperiorCode=?", 100000);		//查询中国所有省会
//			setAttr("provinces", regions);
//			
//			if(member.getAddressId()!= null && member.getAddressId()!=0){
//				Region region=Region.dao.findFirst("select * from t_region where code=?",member.getAddressId());
//				setAttr("currentAddress", region);
//			}
			render("member/memberInfo.vm");
		}else{
			renderNull();
		}
	}
	
	/**
	 * 保存个人信息
	 */
	@Before(Tx.class)
	public void saveMemberInfo(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}

		Map<String, Object> result=new HashMap<String, Object>();
		member=Member.dao.findById(member.getId());
		member.setHeadUrl(getPara("headUrl"));
		member.setNickname(getPara("nickname"));
		member.setPhone(getPara("phone"));
		member.setName(getPara("name"));
		member.setSex(getPara("sex"));
		member.setBirthday(getParaToDate("birthday"));
		if("0".equals(member.getIsfinish())){	//初次完善个人信息赠送积分
			member.setIsfinish("1");
//			member.setMemberPoint(member.getMemberPoint()+CommonConstant.REGIST_POINT);		//增加完善信息积分
			
//			PointLog pointLog=new PointLog();
//			pointLog.setMemberId(member.getId());
//			pointLog.setPoint(CommonConstant.REGIST_POINT);
//			pointLog.setReason("1");
//			pointLog.setType("0");
//			pointLog.setChangeTime(new Date());
//			pointLog.save();		//插入注册赠送积分记录
			Coupon coupon=Coupon.dao.findById(CommonConstant.REGISTE_COUPON_ID);
			if("0".equals(coupon.getStatus())){
				MemberCoupon memberCoupon=new MemberCoupon();
				memberCoupon.setMemberId(member.getId());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
				memberCoupon.setCode("J"+sdf.format(new Date()));
				memberCoupon.setCouponId(coupon.getId());
				memberCoupon.setName(coupon.getName());
				memberCoupon.setSubtitle(coupon.getSubtitle());
				memberCoupon.setRemark(coupon.getRemark());
				memberCoupon.setLogoUrl(coupon.getLogoUrl());
				memberCoupon.setMoney(coupon.getMoney());
				if("0".equals(coupon.getTimeType())){		//优惠券有效期为固定日期
					memberCoupon.setStartTime(coupon.getStartTime());
					memberCoupon.setEndTime(coupon.getEndTime());
				}
				else{		//优惠券有效期为固定天数
					Calendar end=Calendar.getInstance();
					end.add(Calendar.DAY_OF_MONTH, coupon.getDays());
					Calendar calendar=Calendar.getInstance();
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					memberCoupon.setStartTime(calendar.getTime());
					memberCoupon.setEndTime(end.getTime());
				}
				memberCoupon.setStatus("0");
				memberCoupon.save();		//用户注册赠送优惠券
				
				result.put("redirect", true);
			}
			
//			if(member.getFromUser()!=null){
//				Member fromMember=Member.dao.findById(member.getFromUser());		
//				if(fromMember!=null){		//推广人不为空需要给推广人增加积分
//					Record record=Db.findFirst("select sum(point) tgpoints from t_point_log where reason=3 and type=0 and memberid=?",fromMember.getId());
//					BigDecimal tgpoints=record.getBigDecimal("tgpoints");
//					if(tgpoints==null||tgpoints.intValue()<CommonConstant.TGTOTAL_POINT){		//没有达到推广积分限制才增加推广积分，超过推广限制将不再增加积分
//						fromMember.setMemberPoint(fromMember.getMemberPoint()+CommonConstant.TUIGUANG_POINT);
//						fromMember.update();		//增加推广人积分
//						
//						PointLog pointLog2=new PointLog();
//						pointLog2.setMemberId(fromMember.getId());
//						pointLog2.setPoint(CommonConstant.TUIGUANG_POINT);
//						pointLog2.setReason("3");
//						pointLog2.setType("0");
//						pointLog2.setChangeTime(new Date());
//						pointLog2.save();		//插入推广积分记录
//					}
//				}
//			}
		}
		member.update();
		setSessionAttr(SESSION_MEMBER, member);
		result.put("ok", true);
		renderJson(result);
	}
	
	
	/**
	 * 完善信息赠送红包
	 */
	public void hongbao(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		setAttr("member", member);
		render("member/hongbao.vm");
	}
	
	/**
	 * 我的积分
	 */
	public void myscore(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		else{
			member=Member.dao.findById(member.getId());
			setSessionAttr(SESSION_MEMBER, member);
		}
		
		setAttr("member", member);
		List<PointLog> pointLogs=PointLog.dao.find("select p.*,o.code from t_point_log p left join t_order o on o.id=p.orderid where p.memberid=? and p.type=0 order by p.changetime desc",member.getId());
		setAttr("pointLogs", pointLogs);
		render("member/myscore.vm");
	}
	
	/**
	 * 我的积分兑换记录
	 */
	public void myexchange(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		setAttr("member", member);
		List<PointLog> pointLogs=PointLog.dao.find("select p.*,o.code from t_point_log p left join t_order o on o.id=p.orderid where p.memberid=? and p.type=1 order by p.changetime desc",member.getId());
		setAttr("pointLogs", pointLogs);
		render("member/myexchange.vm");
	}
	
	/**
	 * 我的收藏
	 */
	public void myCollect(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		setAttr("member", member);
		List<Goods> collects=Goods.dao.find("select  g.id, g.code, g.typeId, g.brandId, g.sex, g.season, g.name, g.img, g.clickCnt, g.status, g.sortno, g.productAddress, g.material, g.washWay, g.shortIntro, g.priceJson, g.specJson, g.isRecomment, g.createTime, g.updateTime"
		+",c.id as collectid from t_collect c left join t_goods g on g.id=c.goodsid where c.memberid=? order by c.createtime desc",member.getId());
		setAttr("collects", collects);
		render("member/collect.vm");
	}
	
	
	/**
	 * 我的二维码
	 */
	public void myQrCode(){
		Long id=getParaToLong("id");
		Member member=Member.dao.getMemberById(id);
		if(member==null){
			renderNull();
			return;
		}
		
		ApiResult apiResult=QrcodeApi.createPermanent(String.valueOf(member.getId()));
		String qrurl=apiResult.getStr("url");
		setAttr("url", qrurl);
		
		if(getRequest().getHeader("user-agent").toLowerCase().contains("micromessenger")){
			JsTicket jsTicket=JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
			String url = getRequest().getScheme() + "://" + getRequest().getServerName() + getRequest().getRequestURI();
			if (getRequest().getQueryString() != null) {
				url += "?" + getRequest().getQueryString();
			}
			Map<String, String> jsparas=jsSign(jsTicket.getTicket(), url);
			jsparas.put("jsapi_ticket", jsTicket.getTicket());
			jsparas.put("appId", ApiConfigKit.getApiConfig().getAppId());
			String title  = "尺尚设计师集合";
			String link = CommonConstant.WEIXIN_DOMAIN+"member/qrCode?id="+id;
			String imgUrl=null;
			imgUrl=CommonConstant.WEIXIN_DOMAIN+"images/logo.jpg";
			String desc = "邀请好友有礼";
			jsparas.put("title", title);
			jsparas.put("link", link);
			jsparas.put("imgUrl", imgUrl);
			jsparas.put("desc", desc);
			setAttr("jsparas", jsparas);
		}
		
		render("member/myQrCode.vm");
	}
	
	public void qrCode(){
		Long id=getParaToLong("id");
		Member member=Member.dao.getMemberById(id);
		if(member==null){
			renderNull();
			return;
		}
		
		ApiResult apiResult=QrcodeApi.createPermanent(String.valueOf(member.getId()));
		String url=apiResult.getStr("url");
		setAttr("url", url);
		render("member/qrCode.vm");
	}
	
	public void rechargeRecord(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		else{
			member=Member.dao.findById(member.getId());
			setSessionAttr(SESSION_MEMBER, member);
		}
		
		setAttr("member", member);
		List<Record> records=Db.find("select * from t_recharge where status=1 and memberid=? order by dealTime desc",member.getId());
		setAttr("records", records);
		render("member/rechargeRecord.vm");
	}
	
	public void balanceRecord(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		else{
			member=Member.dao.findById(member.getId());
			setSessionAttr(SESSION_MEMBER, member);
		}
		setAttr("member", member);
		List<BalanceRecord> balanceRecords=BalanceRecord.dao.find("select br.*,o.code from t_balance_record br left join t_order o on br.businessId=o.id  where br.memberId=?",member.getId());
		setAttr("balanceRecords", balanceRecords);
		render("member/balanceRecord.vm");
	}
	
}
