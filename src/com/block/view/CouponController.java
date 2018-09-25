package com.block.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.block.common.model.Coupon;
import com.block.common.model.Member;
import com.block.common.model.MemberCoupon;
import com.block.common.model.PointLog;
import com.block.utils.DateUtil;
import com.block.utils.RandomStringGenerator;
import com.block.utils.XMLParser;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;

public class CouponController extends BaseController{

	/**
	 * 可兑换优惠券列表
	 */
	public void couponList(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		List<Coupon> coupons=Coupon.dao.find("select c.*,l.gradename from t_coupon c left join t_level_setting l on l.grade=c.getGrade where type in(0,1) and status=0 and leftamount>0 and (endTime is null or endTime>=now())");
		setAttr("coupons", coupons);
		setAttr("member", member);
		renderVelocity("coupon/couponList.vm");
	}
	
	/**
	 * 优惠券详情
	 */
	public void couponDetail(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		setAttr("member", member);
		Long id=getParaToLong("id");
		Coupon coupon=Coupon.dao.findById(id);
		setAttr("coupon", coupon);
		render("coupon/couponDetail.vm");
	}
	
	/**
	 * 兑换、购买优惠券
	 * @param id
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	@Before(Tx.class)
	public void buyCoupon() throws ParserConfigurationException, IOException, SAXException{
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Long id=getParaToLong("id");
		if(id==null){
			renderJson("success",false);
		}
		member=Member.dao.getMemberById(member.getId());
		Coupon coupon=Coupon.dao.findById(id);
		String txt=coupon.getType().equals("0")?"兑换":"购买";
		Record record=Db.findFirst("select count(1)cnt from t_member_coupon where status<>'3' and memberId=? and couponId=?",member.getId(),coupon.getId());
		if(record.getLong("cnt")>=coupon.getMaxGet()){
			if("0".equals(coupon.getType())){		//兑换
				renderJson("msg","达到最大"+txt+"次数限制!");
			}
			else{
				renderJavascript("$.iBox.remove();$.iBox.alert('"+"达到最大"+txt+"次数限制!"+"');");
			}
			return;
		}
		if(coupon.getLeftAmount()<=0){
			if("0".equals(coupon.getType())){		//兑换
				renderJson("msg","优惠券已被"+txt+"完了!");
			}
			else{
				renderJavascript("$.iBox.remove();$.iBox.alert('"+"优惠券已被"+txt+"完了!"+"');");
			}
			return;
		}
		if(coupon.getGetGrade()>member.getMemberLevel()){
			if("0".equals(coupon.getType())){		//兑换
				renderJson("msg","您的会员等级不能"+txt+"此优惠券!");
			}
			else{
				renderJavascript("$.iBox.remove();$.iBox.alert('"+"您的会员等级不能"+txt+"此优惠券!"+"');");
			}
			return;
		}
		Date now=new Date();
		if("0".equals(coupon.getType())){		//兑换
			if(coupon.getIntegral()>member.getMemberPoint()){
				renderJson("msg","您的积分不足兑换此优惠券!");
				return;
			}
			member.setMemberPoint(member.getMemberPoint()-coupon.getIntegral());
			member.update();		//扣除积分
			setSessionAttr(SESSION_MEMBER, member);
			
			PointLog pointLog=new PointLog();
			pointLog.setChangeTime(now);
			pointLog.setMemberId(member.getId());
			pointLog.setOrderId(coupon.getId());
			pointLog.setPoint(coupon.getIntegral());
			pointLog.setReason("4");
			pointLog.setType("1");
			pointLog.save();		//保存积分日志
			
			MemberCoupon memberCoupon=new MemberCoupon();
			memberCoupon.setMemberId(member.getId());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
			memberCoupon.setCode("J"+sdf.format(now));
			memberCoupon.setCouponId(id);
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
			memberCoupon.save();		//保存用户优惠券
			
			Db.update("update t_coupon set leftAmount=leftAmount-1 where id=?",coupon.getId());		//优惠券数量减1
			renderJson("success",true);
		}
		else if("1".equals(coupon.getType())){		//购买
			MemberCoupon memberCoupon=new MemberCoupon();
			memberCoupon.setMemberId(member.getId());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
			memberCoupon.setCode("J"+sdf.format(now));
			memberCoupon.setCouponId(id);
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
			memberCoupon.setStatus("3");
			memberCoupon.save();		//保存用户优惠券
			
			Map<String, String> unifiedorder = new HashMap<String, String>();
			unifiedorder.put("appid", ApiConfigKit.getApiConfig().getAppId());// 公众账号ID
			unifiedorder.put("mch_id", PropKit.get("mchid"));// 商户号
			unifiedorder.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32));//设备号
			unifiedorder.put("body", "尺尚优惠券购买-"+memberCoupon.getCode());// 商品描述/名称
			unifiedorder.put("out_trade_no", memberCoupon.getCode());// 商户订单号编号
			unifiedorder.put("total_fee",String.valueOf((int)(coupon.getPrice()*100)));
			unifiedorder.put("spbill_create_ip", RandomStringGenerator.getIpAddress(this.getRequest()));// 终端IP
			unifiedorder.put("notify_url", PropKit.get("paynotifyurl_coupon"));// 通知地址
			unifiedorder.put("trade_type", "JSAPI");// 交易类型
			unifiedorder.put("product_id", String.valueOf(coupon.getId()));// 
			unifiedorder.put("openid", member.getOpenid());// 用户标识
			
			//参数签名,待传key
			unifiedorder.put("sign", PaymentKit.createSign(unifiedorder, PropKit.get("paternerKey")));
			
			String jspay="";
			System.out.println("统一下单:"+PaymentKit.toXml(unifiedorder));
			String xmlStr = PaymentApi.pushOrder(unifiedorder);
			System.out.println("统一下单结果："+xmlStr);
			Map<String, Object> map = XMLParser.getMapFromXML(xmlStr);
			if("SUCCESS".equals(map.get("result_code"))){
				// H5 jsapi调用微信支付
				Map<String, String> jsparas = new HashMap<String, String>();
				jsparas.put("appId", map.get("appid").toString());
				jsparas.put("timeStamp", String.valueOf(System.currentTimeMillis()));
				jsparas.put("nonceStr", map.get("nonce_str").toString());
				jsparas.put("package", "prepay_id=" + map.get("prepay_id"));
				jsparas.put("signType", "MD5");
				jsparas.put("paySign", PaymentKit.createSign(jsparas, PropKit.get("paternerKey")));
				
				this.setAttr("jsparas", JsonUtils.toJson(jsparas));
				
				jspay = "function onBridgeReady(){" +
						"WeixinJSBridge.invoke(" +
						"'getBrandWCPayRequest'," +
						this.getAttr("jsparas") + "," +
						"function(res){" +
						"if(res.err_msg == 'get_brand_wcpay_request:ok'){$.iBox.remove();window.location.href='"+ this.getRequest().getContextPath() +"/coupon/memberCouponList';}else if(res.err_msg == 'get_brand_wcpay_request:cancel'){$.iBox.remove();alert('支付取消');}else{alert('支付失败');}" +
						"}" +
						");" +
						"}" +
						"if(typeof WeixinJSBridge == 'undefined'){" +
						"if(document.addEventListener){" +
						"document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);" +
						"}else if(document.attachEvent){" +
						"document.attachEvent('WeixinJSBridgeReady', onBridgeReady); " +
						"document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);" +
						"}" +
						"}else{" +
						"onBridgeReady();" +
						"}";
				
			}else{
				jspay = "$.iBox.remove();alert('"+ map.get("err_code_des") +"!');";
			}
			renderJavascript(jspay);
		}
	}
	
	public void memberCouponList(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		List<MemberCoupon> memberCoupons=MemberCoupon.dao.find("select mc.* from t_member_coupon mc where status<>3 and mc.memberId=?",member.getId());
		List<MemberCoupon> unUsed=new ArrayList<MemberCoupon>();
		List<MemberCoupon> used=new ArrayList<MemberCoupon>();
		List<MemberCoupon> overdued=new ArrayList<MemberCoupon>();
		for(MemberCoupon memberCoupon:memberCoupons){
			if("1".equals(memberCoupon.getStatus())){
				used.add(memberCoupon);
			}
			else{
				if(DateUtil.isInRange(memberCoupon.getStartTime(), memberCoupon.getEndTime())){
					unUsed.add(memberCoupon);
				}
				else{
					overdued.add(memberCoupon);
				}
			}
		}
		setAttr("unUsed", unUsed);
		setAttr("used", used);
		setAttr("overdued", overdued);
		render("coupon/memberCoupons.vm");
	}
	
	public void getMemberCoupon(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		List<MemberCoupon> memberCoupons=MemberCoupon.dao.find("select * from t_member_coupon where memberId=? and status=0 and startTime<=now() and endTime>=now()",member.getId());
		renderJson(memberCoupons);
	}
}
