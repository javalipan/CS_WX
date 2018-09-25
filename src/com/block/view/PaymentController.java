package com.block.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import com.block.common.CommonConstant;
import com.block.common.model.Coupon;
import com.block.common.model.LevelSetting;
import com.block.common.model.Member;
import com.block.common.model.MemberCoupon;
import com.block.common.model.MemberTypeChange;
import com.block.common.model.Order;
import com.block.common.model.OrderDetail;
import com.block.common.model.PointLog;
import com.block.utils.CacheUtil;
import com.block.utils.DateUtil;
import com.block.utils.NumberUtil;
import com.block.utils.TemplateMsgUtil;
import com.block.utils.thread.LabelThread;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;


public class PaymentController extends BaseController{
	
	
	/**
	 * 支付回调
	 * @throws IOException 
	 */
	@Before(Tx.class)
	public void paynotify() throws IOException{
		String rspdata = "FAIL";
		StringBuffer buffer = new StringBuffer("");
		String strMessage = "";
		InputStream in = this.getRequest().getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while ((strMessage = reader.readLine()) != null) {
			buffer.append(strMessage);
		}
		System.out.println(buffer.toString());
		// 1.2、签名校验
		Map<String,String> map = PaymentKit.xmlToMap(buffer.toString());
		if(map != null && PaymentKit.verifyNotify(map,PropKit.get("paternerKey")) && "SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))){
//		if(map != null&& "SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))){
			if(!StringUtils.isEmpty(map.get("out_trade_no"))){
				Order order=Order.dao.findFirst("select * from t_order where code=?",map.get("out_trade_no"));
				Member member=Member.dao.findById(order.getMemberId());
				Date now=new Date();
				order.setIsPay("1");
				order.setPayTime(now);
				order.setPayway("0");
				
				List<OrderDetail> orderDetails=OrderDetail.dao.find("select * from t_order_detail where orderid=?",order.getId());
				for(OrderDetail od:orderDetails){	//减少商品库存增加销量
					Db.update("update t_goods set soldCnt=soldCnt+? where id=(select goodsid from t_goods_detail gd where gd.id=?)",od.getAmount(),od.getGoodsDetailId()) ;		//增加销量
					Db.update("update t_goods_detail set amount=amount-? where id=?",od.getAmount(),od.getGoodsDetailId());		//减少库存
				}
				
				LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
				int thispoint=new Double(NumberUtil.multiply(NumberUtil.add(order.getTotalPrice(), order.getBalancePay()), levelSetting.getPointSpeed())).intValue();
				order.setIntegral(thispoint);
				
				member.setMemberPoint(member.getMemberPoint()+thispoint);		//增加用户消费积分
				member.setTotalmoney(NumberUtil.toFixed(NumberUtil.add(member.getTotalmoney(),order.getTotalPrice()), 2));		//增加消费金额
				Date lasttime=member.getLastBuyTime();
				if(lasttime==null){
					MemberTypeChange memberTypeChange=new MemberTypeChange();
					memberTypeChange.setMemberId(member.getId());
					memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
					memberTypeChange.setAfterType("N");
					memberTypeChange.setChangeTime(now);
					memberTypeChange.save();
					
					member.setMemberType("N");		//第一次购买为新顾客
					
					member.setOldornew("1");
					
					if(member.getFromUser()!=null){		//邀请人不为空送券
						Coupon invitedcoupon=Coupon.dao.findById(CommonConstant.INVITED_COUPON_ID);		//被邀请人优惠券
						if("0".equals(invitedcoupon.getStatus())){
							MemberCoupon memberCoupon=new MemberCoupon();
							memberCoupon.setMemberId(member.getId());
							SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
							memberCoupon.setCode("J"+sdf.format(new Date()));
							memberCoupon.setCouponId(invitedcoupon.getId());
							memberCoupon.setName(invitedcoupon.getName());
							memberCoupon.setSubtitle(invitedcoupon.getSubtitle());
							memberCoupon.setRemark(invitedcoupon.getRemark());
							memberCoupon.setLogoUrl(invitedcoupon.getLogoUrl());
							memberCoupon.setMoney(invitedcoupon.getMoney());
							if("0".equals(invitedcoupon.getTimeType())){		//优惠券有效期为固定日期
								memberCoupon.setStartTime(invitedcoupon.getStartTime());
								memberCoupon.setEndTime(invitedcoupon.getEndTime());
							}
							else{		//优惠券有效期为固定天数
								Calendar end=Calendar.getInstance();
								end.add(Calendar.DAY_OF_MONTH, invitedcoupon.getDays());
								Calendar calendar=Calendar.getInstance();
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								memberCoupon.setStartTime(calendar.getTime());
								memberCoupon.setEndTime(end.getTime());
							}
							memberCoupon.setStatus("0");
							memberCoupon.save();		//被邀请人优惠券
						}
						
						Coupon invitercoupon=Coupon.dao.findById(CommonConstant.INVITER_COUPON_ID);		//邀请人优惠券
						if("0".equals(invitercoupon.getStatus())){
							MemberCoupon memberCoupon=new MemberCoupon();
							memberCoupon.setMemberId(member.getFromUser());
							SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
							memberCoupon.setCode("J"+sdf.format(new Date()));
							memberCoupon.setCouponId(invitercoupon.getId());
							memberCoupon.setName(invitercoupon.getName());
							memberCoupon.setSubtitle(invitercoupon.getSubtitle());
							memberCoupon.setRemark(invitercoupon.getRemark());
							memberCoupon.setLogoUrl(invitercoupon.getLogoUrl());
							memberCoupon.setMoney(invitercoupon.getMoney());
							if("0".equals(invitercoupon.getTimeType())){		//优惠券有效期为固定日期
								memberCoupon.setStartTime(invitercoupon.getStartTime());
								memberCoupon.setEndTime(invitercoupon.getEndTime());
							}
							else{		//优惠券有效期为固定天数
								Calendar end=Calendar.getInstance();
								end.add(Calendar.DAY_OF_MONTH, invitercoupon.getDays());
								Calendar calendar=Calendar.getInstance();
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								memberCoupon.setStartTime(calendar.getTime());
								memberCoupon.setEndTime(end.getTime());
							}
							memberCoupon.setStatus("0");
							memberCoupon.save();		//邀请人优惠券
						}
					}
				}
				else{
					member.setOldornew("2");
					int buyperiod=CacheUtil.getBuyPeriod();		//获取购买周期设置的天数
					double days=DateUtil.daysRange(lasttime, now);
					if(days<buyperiod&&!"E".equals(member.getMemberType())){
						MemberTypeChange memberTypeChange=new MemberTypeChange();
						memberTypeChange.setMemberId(member.getId());
						memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
						memberTypeChange.setAfterType("E");
						memberTypeChange.setChangeTime(now);
						memberTypeChange.save();
						
						member.setMemberType("E");		//一个购买周期非第一次购买
					}
					if("S1".equals(member.getMemberType())){
						MemberTypeChange memberTypeChange=new MemberTypeChange();
						memberTypeChange.setMemberId(member.getId());
						memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
						memberTypeChange.setAfterType("X1");
						memberTypeChange.setChangeTime(now);
						memberTypeChange.save();
						
						member.setMemberType("X1");		//唤醒瞌睡
					}
					if("S2".equals(member.getMemberType())){
						MemberTypeChange memberTypeChange=new MemberTypeChange();
						memberTypeChange.setMemberId(member.getId());
						memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
						memberTypeChange.setAfterType("X2");
						memberTypeChange.setChangeTime(now);
						memberTypeChange.save();
						
						member.setMemberType("X2");		//唤醒半睡
					}
					if("S3".equals(member.getMemberType())){
						MemberTypeChange memberTypeChange=new MemberTypeChange();
						memberTypeChange.setMemberId(member.getId());
						memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
						memberTypeChange.setAfterType("X3");
						memberTypeChange.setChangeTime(now);
						memberTypeChange.save();
						
						member.setMemberType("X3");		//唤醒沉睡
					}
				}
				
				LevelSetting newlevelsetting=LevelSetting.dao.getCurrentLevelByMoney(member.getTotalmoney());
				if(newlevelsetting.getGrade()!=member.getMemberLevel()){	//会员等级变动
					member.setMemberLevel(newlevelsetting.getGrade());		//会员等级
					member.setLevelChangeTime(now);		//会员等级变动日期
					
					if(member.getDiscount()==null||member.getDiscount()==0){
						member.setDiscount(newlevelsetting.getDiscount());
					}
					else{
						member.setDiscount(member.getDiscount()>newlevelsetting.getDiscount()?newlevelsetting.getDiscount():member.getDiscount());
					}
				}
				
				member.setLastBuyTime(now);
				member.update();
				order.update();
				if(thispoint>0){
					
					PointLog pointLog=new PointLog();
					pointLog.setChangeTime(now);
					pointLog.setMemberId(member.getId());
					pointLog.setOrderId(order.getId());
					pointLog.setPoint(thispoint);
					pointLog.setReason("0");
					pointLog.setType("0");
					pointLog.save();		//插入积分变更记录(增加)
				}
				
				String names="";
				List<OrderDetail> details=OrderDetail.dao.find("select * from t_order_detail where orderId=?",order.getId());
				for(OrderDetail d:details){
					names+=d.getGoodsName()+",";
				}
				if(names.length()>0){
					names=names.substring(0,names.length()-1);
				}
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//发送模板消息给用户
				TemplateMsgApi.send(TemplateMsgUtil.generateMgsJson(member.getOpenid(), TemplateMsgUtil.template_id_order, CommonConstant.WEIXIN_DOMAIN+"member/memberCenter", TemplateMsgUtil.generateOrderSuccessMsgData("下单成功，订单编号："+order.getCode()+"。我们已收到您的货款，开始为您打包商品，请耐心等待: )", String.valueOf(order.getTotalPrice())+"元", names, "如有问题请致电尺尚客服或直接在微信留言，尺尚将第一时间为您服务！")));
				//发送模板消息给管理员
				for(String openid:CommonConstant.MANAGER_OPENID){
					TemplateMsgApi.send(TemplateMsgUtil.generateMgsJson(openid, TemplateMsgUtil.template_id_neworder, CommonConstant.WEIXIN_DOMAIN, TemplateMsgUtil.generateNewOrder("您有新的订单:"+order.getCode(), member.getNickname(), sdf.format(order.getOrderTime()), names, String.valueOf(order.getTotalPrice())+"元", member.getPhone(), "买家留言："+(StringUtils.isNotBlank(order.getRemark())?order.getRemark():"无"))));
				}
				
				LabelThread labelThread=new LabelThread(member);
				ExecutorService e = Executors.newFixedThreadPool(1);
				e.execute(labelThread);
				
				rspdata = "SUCCESS";
			}
		}
		renderText(rspdata);
	}
	
	/**
	 * 优惠券支付回调
	 * @throws IOException
	 */
	@Before(Tx.class)
	public void paynotify_coupon() throws IOException{
		String rspdata = "FAIL";
		StringBuffer buffer = new StringBuffer("");
		String strMessage = "";
		InputStream in = this.getRequest().getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while ((strMessage = reader.readLine()) != null) {
			buffer.append(strMessage);
		}
		System.out.println(buffer.toString());
		// 1.2、签名校验
		Map<String,String> map = PaymentKit.xmlToMap(buffer.toString());
		if(map != null && PaymentKit.verifyNotify(map,PropKit.get("paternerKey")) && "SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))){
			if(!StringUtils.isEmpty(map.get("out_trade_no"))){
				String code=map.get("out_trade_no");
				MemberCoupon memberCoupon=MemberCoupon.dao.findFirst("select * from t_member_coupon where code=?",code);
				double totalfee=NumberUtil.divide(Double.parseDouble(map.get("total_fee")), 100);
				Member member=Member.dao.findById(memberCoupon.getMemberId());
				if("3".equals(memberCoupon.getStatus())){
					memberCoupon.setStatus("0");
					memberCoupon.update();
					
					Db.update("update t_coupon set leftAmount=leftAmount-1 where id=?",memberCoupon.getCouponId());		//优惠券数量减1
					
					member.setTotalmoney(NumberUtil.toFixed(NumberUtil.add(member.getTotalmoney(),totalfee), 2));		//增加消费金额
					
					LevelSetting newlevelsetting=LevelSetting.dao.getCurrentLevelByMoney(member.getTotalmoney());
					if(newlevelsetting.getGrade()!=member.getMemberLevel()){	//会员等级变动
						member.setMemberLevel(newlevelsetting.getGrade());		//会员等级
						member.setLevelChangeTime(new Date());		//会员等级变动日期
						
						if(member.getDiscount()==null||member.getDiscount()==0){
							member.setDiscount(newlevelsetting.getDiscount());
						}
						else{
							member.setDiscount(member.getDiscount()>newlevelsetting.getDiscount()?newlevelsetting.getDiscount():member.getDiscount());
						}
					}
					
					member.update();
				}
				rspdata = "SUCCESS";
			}
		}
		renderText(rspdata);
	}
	
}
