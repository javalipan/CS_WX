package com.block.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.block.common.CommonConstant;
import com.block.common.model.BalanceRecord;
import com.block.common.model.Coupon;
import com.block.common.model.FullRule;
import com.block.common.model.Goods;
import com.block.common.model.GoodsDetail;
import com.block.common.model.LevelSetting;
import com.block.common.model.Member;
import com.block.common.model.MemberAddress;
import com.block.common.model.MemberCoupon;
import com.block.common.model.MemberTypeChange;
import com.block.common.model.Order;
import com.block.common.model.OrderDetail;
import com.block.common.model.PointLog;
import com.block.common.model.Region;
import com.block.common.model.ShoppingCar;
import com.block.utils.CacheUtil;
import com.block.utils.DateUtil;
import com.block.utils.NumberUtil;
import com.block.utils.RandomStringGenerator;
import com.block.utils.TemplateMsgUtil;
import com.block.utils.XMLParser;
import com.block.utils.thread.LabelThread;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;

public class OrderController extends BaseController{

	/**
	 * 添加购物车
	 */
	public void addCart(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		Map<String, Object> result=new HashMap<String, Object>();
		Long detailid=getParaToLong("detailid");
		Integer num=getParaToInt("num");
		if(detailid!=null&&num!=null&&detailid>0&&num>0){
			GoodsDetail goodsDetail=GoodsDetail.dao.findById(detailid);
			ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select * from t_shopping_car where memberid=? and goodsDetailId=?",member.getId(),detailid);
			if(shoppingCar!=null){
				if(shoppingCar.getNum()+num>goodsDetail.getAmount()){
					result.put("ok", false);
					result.put("msg", "库存不足!");
				}
				else{
					shoppingCar.setNum(shoppingCar.getNum()+num);
					shoppingCar.update();
					result.put("ok", true);
				}
			}
			else{
				shoppingCar=new ShoppingCar();
				shoppingCar.setMemberid(member.getId());
				shoppingCar.setGoodsDetailId(detailid);
				shoppingCar.setNum(num);
				if(num>goodsDetail.getAmount()){
					result.put("ok", false);
					result.put("msg", "库存不足!");
				}
				else{
					shoppingCar.save();
					result.put("ok", true);
				}
			}
		}
		
		ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
		getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
		result.put("cnt", shoppingCar.getLong("cnt"));
		renderJson(result);
	}
	
	/**
	 * 购物车
	 */
	public void gotoShoppingCar(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		List<GoodsDetail> goodsDetails=GoodsDetail.dao.find("select g.name,g.img,g.isDiscount,c.num,d.*,s1.specvalue colorName,s2.specvalue sizeName from t_shopping_car c left join t_goods_detail d on c.goodsdetailid=d.id left join t_goods g on g.id=d.goodsid left join t_spec s1 on s1.id=d.colorid left join t_spec s2 on s2.id=d.sizeid where c.memberid=? order by c.addTime desc",member.getId());
		setAttr("goodsDetails", goodsDetails);
		renderVelocity("order/shoppingCar.vm");
	}
	
	public void deleteShoppingCar(){
		String id=getPara("detailid");
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Db.update("delete from t_shopping_car where memberid=? and goodsDetailId=?",member.getId(),id);
		
		ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
		getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
		
		renderJson("ok",true);
	}
	
	/**
	 * 编辑之后保存购物车
	 */
	@Before(Tx.class)
	public void saveShoppingCar(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		String json=getPara("json");
		if(StringUtils.isBlank(json)){
			renderJavascript("$.iBox.alert('请先选择商品!');");
			return;
		}
		Map<String, Object> result=new HashMap<String, Object>();
		Db.update("delete from t_shopping_car where memberid=?",member.getId());
		
		JSONArray jsonArray=JSONArray.parseArray(json);
		for(int i=0;i<jsonArray.size();i++){
			JSONObject obj=jsonArray.getJSONObject(i);
			Long id=obj.getLong("id");
			int num=obj.getInteger("num");
			GoodsDetail goodsDetail=GoodsDetail.dao.findById(id);
			ShoppingCar shoppingCar=new ShoppingCar();
			shoppingCar.setMemberid(member.getId());
			shoppingCar.setGoodsDetailId(id);
			shoppingCar.setNum(num);
			if(num>goodsDetail.getAmount()){
				result.put("ok", false);
				result.put("msg", "库存不足!");
			}
			else{
				shoppingCar.save();
				result.put("ok", true);
			}
		}
		ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
		getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
		
		renderJson("ok",true);
	}
	
	/**
	 * 立即购买、结算
	 */
	public void gotobuy(){
		Member sessionmember=getMember();
		if(sessionmember==null){
			renderNull();
			return;
		}
//		
		String json=getPara("json");
		setAttr("json",json);
		String from=getPara("from");
		if(StringUtils.isNotBlank(from)){		//订单来源
			setAttr("from", from);
		}
		Member member=Member.dao.findById(sessionmember.getId());
		setSessionAttr(SESSION_MEMBER, member);
		setAttr("member", member);
		if(StringUtils.isBlank(json)){
			renderJavascript("请先选择商品");
			return;
		}
		StringBuffer sb=new StringBuffer();
		JSONArray jsonArray=JSONArray.parseArray(json);
		for(int i=0;i<jsonArray.size();i++){
			JSONObject obj=jsonArray.getJSONObject(i);
			if(i==0){
				sb.append("'").append(obj.getString("id")).append("'");
			}
			else{
				sb.append(",'").append(obj.getString("id")).append("'");
			}
		}
		double total=0;
		double oldprice=0;
		List<GoodsDetail> goodsDetails=GoodsDetail.dao.find("select g.name,g.img,g.isdiscount,d.*,s1.specvalue colorName,s2.specvalue sizeName from  t_goods_detail d  left join t_goods g on g.id=d.goodsid left join t_spec s1 on s1.id=d.colorid left join t_spec s2 on s2.id=d.sizeid where d.id in("+sb.toString()+")");
		for(GoodsDetail goodsDetail:goodsDetails){
			for(int i=0;i<jsonArray.size();i++){
				if(goodsDetail.getId().longValue()==jsonArray.getJSONObject(i).getLong("id").longValue()){
					int num=jsonArray.getJSONObject(i).getInteger("num");
					if(num>goodsDetail.getAmount()){		//购买数量不能大于库存验证
						renderJavascript("商品"+goodsDetail.get("name")+"库存不足!");
						return;
					}
					if(!"1".equals(goodsDetail.getStatus())){
						renderJavascript("商品"+goodsDetail.get("name")+"已下架!");
						return;
					}
					goodsDetail.put("num", num);
					
					double t;
					if("2".equals(goodsDetail.get("isdiscount"))){		//折扣中的以折扣价计算
						t=NumberUtil.multiply(goodsDetail.getVipPrice(), num);
					}else{
						t=NumberUtil.multiply(goodsDetail.getPrice(), num);
					}
					goodsDetail.put("total", NumberUtil.toFixed(t, 2));
					total=NumberUtil.add(total, t);
					oldprice=NumberUtil.add(oldprice, NumberUtil.multiply(goodsDetail.getPrice(), num));
				}
			}
		}
		setAttr("orderprice", total);
		setAttr("goodsDetails", goodsDetails);
		setAttr("oldprice", oldprice);
		setAttr("discount", (member.getDiscount()>0&&member.getDiscount()<10)?member.getDiscount():0);
		
		List<FullRule> fullRules=FullRule.dao.find("select * from t_full_rule order by amount asc");
		setAttr("fullRules", fullRules);		//满减规则
		int subtractmoney=0;		//满减
		String subtractText="";
		for(int i=fullRules.size()-1;i>=0;i--){
			if(total>fullRules.get(i).getAmount()){
				subtractmoney=fullRules.get(i).getSubtractAmount();
				subtractText="满"+fullRules.get(i).getAmount()+"减"+subtractmoney;
				break;
			}
		}
		double topay=total;
		if(subtractmoney>0){
			topay=NumberUtil.subtract(total, subtractmoney);	//减去满减部分
			setAttr("subtractText", subtractText);
			setAttr("subtractmoney", subtractmoney);
		}
		setAttr("topay", topay);
		setAttr("total", NumberUtil.toFixed(total, 2));
//		
//		LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
//		setAttr("speed", levelSetting.getPointSpeed());
//		setAttr("points", (int)NumberUtil.multiply(total, levelSetting.getPointSpeed()));
//		setAttr("rempoints", member.getMemberPoint()-(member.getMemberPoint()%CommonConstant.INT_TIMES));
//		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		setAttr("now", sdf.format(new Date()));
//		setAttr("INT_TIMES", CommonConstant.INT_TIMES);
//		setAttr("INT_WEIGHT", CommonConstant.INT_WEIGHT);
//		
//		if(member.getAddressId()!= null && member.getAddressId()!=0){
//			Region region=Region.dao.findFirst("select * from t_region where code=?",member.getAddressId());
//			setAttr("currentAddress", region);
//		}
		List<MemberCoupon> memberCoupons=MemberCoupon.dao.find("select mc.* from t_member_coupon mc where mc.memberId=? and mc.status=0 and mc.startTime<=now() and mc.endTime>=now()",member.getId());
		setAttr("memberCoupons", memberCoupons);		//优惠券
		List<MemberAddress> memberAddresses=MemberAddress.dao.find("select * from t_member_address where memberId=?",member.getId());
		setAttr("memberAddresses", memberAddresses);		//收货地址
		MemberAddress memberAddress=MemberAddress.dao.findFirst("select * from t_member_address where memberId=? and isDefault=1",member.getId());
		setAttr("memberAddress", memberAddress);		//默认收货地址
		List<Region> regions=Region.dao.find("select * from t_region where SuperiorCode=?", 100000);		//查询中国所有省会
		setAttr("provinces", regions);
		
		render("order/orderEdit.vm");
	}
	
//	/**
//	 * 立即购买、结算（周年庆）
//	 */
//	public void gotobuy(){
//		Member sessionmember=getMember();
//		if(sessionmember==null){
//			renderNull();
//			return;
//		}
////		
//		String json=getPara("json");
//		setAttr("json",json);
//		String from=getPara("from");
//		if(StringUtils.isNotBlank(from)){		//订单来源
//			setAttr("from", from);
//		}
//		Member member=Member.dao.findById(sessionmember.getId());
//		setSessionAttr(SESSION_MEMBER, member);
//		setAttr("member", member);
//		if(StringUtils.isBlank(json)){
//			renderJavascript("请先选择商品");
//			return;
//		}
//		StringBuffer sb=new StringBuffer();
//		JSONArray jsonArray=JSONArray.parseArray(json);
//		for(int i=0;i<jsonArray.size();i++){
//			JSONObject obj=jsonArray.getJSONObject(i);
//			if(i==0){
//				sb.append("'").append(obj.getString("id")).append("'");
//			}
//			else{
//				sb.append(",'").append(obj.getString("id")).append("'");
//			}
//		}
//		double total=0;
//		double oldprice=0;
//		List<GoodsDetail> goodsDetails=GoodsDetail.dao.find("select g.name,g.img,g.isdiscount,d.*,s1.specvalue colorName,s2.specvalue sizeName from  t_goods_detail d  left join t_goods g on g.id=d.goodsid left join t_spec s1 on s1.id=d.colorid left join t_spec s2 on s2.id=d.sizeid where d.id in("+sb.toString()+")");
//		List<Double> priceList=new ArrayList<Double>();
//		int count=0;
//		for(GoodsDetail goodsDetail:goodsDetails){
//			for(int i=0;i<jsonArray.size();i++){
//				if(goodsDetail.getId().longValue()==jsonArray.getJSONObject(i).getLong("id").longValue()){
//					int num=jsonArray.getJSONObject(i).getInteger("num");
//					if(num>goodsDetail.getAmount()){		//购买数量不能大于库存验证
//						renderJavascript("库存不足!");
//						return;
//					}
//					if(!"1".equals(goodsDetail.getStatus())){
//						renderJavascript("商品"+goodsDetail.get("name")+"已下架!");
//						return;
//					}
//					goodsDetail.put("num", num);
//					
//					double t;
//					if("2".equals(goodsDetail.get("isdiscount"))){		//折扣中的以折扣价计算
//						t=NumberUtil.multiply(goodsDetail.getVipPrice(), num);
//						
//						for(int k=0;k<num;k++){
//							priceList.add(goodsDetail.getVipPrice());
//						}
//					}else{
//						t=NumberUtil.multiply(goodsDetail.getPrice(), num);
//						
//						for(int k=0;k<num;k++){
//							priceList.add(goodsDetail.getPrice());
//						}
//					}
//					goodsDetail.put("total", NumberUtil.toFixed(t, 2));
//					total=NumberUtil.add(total, t);
//					oldprice=NumberUtil.add(oldprice, NumberUtil.multiply(goodsDetail.getPrice(), num));
//					
//					
//					count+=num;
//				}
//			}
//		}
//		
//		Collections.sort(priceList);
//		int k=count/4;
//		for(int i=0;i<k;i++){
//			total=NumberUtil.subtract(total, priceList.get(i));
//		}
//		setAttr("goodsDetails", goodsDetails);
//		setAttr("oldprice", oldprice);
//		total=NumberUtil.multiply(total, member.getDiscount()<=0?1:member.getDiscount()/10);
//		
//		List<FullRule> fullRules=FullRule.dao.find("select * from t_full_rule order by amount asc");
//		setAttr("fullRules", fullRules);		//满减规则
//		int subtractmoney=0;		//满减
//		String subtractText="";
//		for(int i=fullRules.size()-1;i>=0;i--){
//			if(total>fullRules.get(i).getAmount()){
//				subtractmoney=fullRules.get(i).getSubtractAmount();
//				subtractText="满"+fullRules.get(i).getAmount()+"减"+subtractmoney;
//				break;
//			}
//		}
//		double topay=total;
//		if(subtractmoney>0){
//			topay=NumberUtil.subtract(total, subtractmoney);	//减去满减部分
//			setAttr("subtractText", subtractText);
//			setAttr("subtractmoney", subtractmoney);
//		}
//		setAttr("topay", topay);
//		setAttr("total", NumberUtil.toFixed(total, 2));
////		
////		LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
////		setAttr("speed", levelSetting.getPointSpeed());
////		setAttr("points", (int)NumberUtil.multiply(total, levelSetting.getPointSpeed()));
////		setAttr("rempoints", member.getMemberPoint()-(member.getMemberPoint()%CommonConstant.INT_TIMES));
////		
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		setAttr("now", sdf.format(new Date()));
////		setAttr("INT_TIMES", CommonConstant.INT_TIMES);
////		setAttr("INT_WEIGHT", CommonConstant.INT_WEIGHT);
////		
////		if(member.getAddressId()!= null && member.getAddressId()!=0){
////			Region region=Region.dao.findFirst("select * from t_region where code=?",member.getAddressId());
////			setAttr("currentAddress", region);
////		}
//		List<MemberCoupon> memberCoupons=MemberCoupon.dao.find("select mc.* from t_member_coupon mc where mc.memberId=? and mc.status=0 and mc.startTime<=now() and mc.endTime>=now()",member.getId());
//		setAttr("memberCoupons", memberCoupons);		//优惠券
//		List<MemberAddress> memberAddresses=MemberAddress.dao.find("select * from t_member_address where memberId=?",member.getId());
//		setAttr("memberAddresses", memberAddresses);		//收货地址
//		MemberAddress memberAddress=MemberAddress.dao.findFirst("select * from t_member_address where memberId=? and isDefault=1",member.getId());
//		setAttr("memberAddress", memberAddress);		//默认收货地址
//		List<Region> regions=Region.dao.find("select * from t_region where SuperiorCode=?", 100000);		//查询中国所有省会
//		setAttr("provinces", regions);
//		
//		render("order/orderEdit.vm");
//	}
	
//	/**
//	 * 订单提交(周年庆)
//	 * @throws SAXException 
//	 * @throws IOException 
//	 * @throws ParserConfigurationException 
//	 */
//	@Before(Tx.class)
//	public void saveOrder() throws ParserConfigurationException, IOException, SAXException{
//		Member member=getMember();
//		if(member==null){
//			renderNull();
//			return;
//		}
//		
//		String jspay = "$.iBox.remove();$.iBox.alert('订单异常!');";
//		String json=getPara("json");
//		String from=getPara("from");		//订单来源
//		if(StringUtils.isBlank(json)){
//			renderJavascript("$.iBox.remove();$.iBox.alert('请先选择商品!');");
//			return;
//		}
//		member=Member.dao.findById(member.getId());
//		setSessionAttr(SESSION_MEMBER, member);
//		JSONArray jsonArray=JSONArray.parseArray(json);
//		List<OrderDetail> orderDetails=new ArrayList<OrderDetail>();
//		Order order=new Order();
//		Date now=new Date();
//		double total=0;
//		double viptotal=0;
//		List<Double> priceList=new ArrayList<Double>();
//		int count=0;
//		for(int i=0;i<jsonArray.size();i++){
//			JSONObject obj=jsonArray.getJSONObject(i);
//			Long id=obj.getLong("id");
//			int num=obj.getIntValue("num");
//			GoodsDetail goodsDetail=GoodsDetail.dao.findById(id);
//			Goods goods=Goods.dao.findById(goodsDetail.getGoodsId());
//			
//			if(num>goodsDetail.getAmount()){		//购买数量不能大于库存验证
//				renderJavascript("$.iBox.remove();$.iBox.alert('库存不足!');");
//				return;
//			}
//			
//			total=NumberUtil.add(total, NumberUtil.multiply(num, goodsDetail.getPrice()));
//			if("2".equals(goods.getIsDiscount())){		//折扣中的以折扣价计算
//				viptotal=NumberUtil.add(viptotal, NumberUtil.multiply(num, goodsDetail.getVipPrice()));
//				
//				for(int k=0;k<num;k++){
//					priceList.add(goodsDetail.getVipPrice());
//				}
//			}
//			else{
//				viptotal=NumberUtil.add(viptotal, NumberUtil.multiply(num, goodsDetail.getPrice()));
//				
//				for(int k=0;k<num;k++){
//					priceList.add(goodsDetail.getPrice());
//				}
//			}
//			OrderDetail orderDetail=new OrderDetail();
//			orderDetail.setAmount(num);
//			orderDetail.setColorId(goodsDetail.getColorId());
//			orderDetail.setCostPrice(goodsDetail.getCostPrice());
//			orderDetail.setGoodsDetailId(goodsDetail.getId());
//			orderDetail.setGoodsName(goods.getName());
//			orderDetail.setOldPrice(goodsDetail.getOldPrice());
//			orderDetail.setPrice(goodsDetail.getPrice());
//			orderDetail.setSizeId(goodsDetail.getSizeId());
//			orderDetail.setTotalPrice(NumberUtil.toFixed(NumberUtil.multiply(num, goodsDetail.getVipPrice()), 2));
//			orderDetail.setVipPrice(goodsDetail.getVipPrice());
//			orderDetail.setDiscount("2".equals(goods.getIsDiscount())?goods.getDiscount():0);
//			orderDetails.add(orderDetail);
//			
//			count+=num;
//		}
//		Collections.sort(priceList);
//		int k=count/4;
//		for(int i=0;i<k;i++){
//			viptotal=NumberUtil.subtract(viptotal, priceList.get(i));
//		}
//		
//		double viptotal2=viptotal;
//		String delWay=getPara("delWay");
//		if(StringUtils.isBlank(delWay)){
//			delWay="0";
//		}
//		order.setDelWay(delWay);
//		order.setRemark(getPara("remark"));
//		MemberAddress memberAddress=MemberAddress.dao.findById(getPara("addrid"));		//用户选择的收货地址
//		if(delWay.equals("0")){		//商家配送
//			order.setAddress(memberAddress.getAddress());
//			order.setReveiveName(memberAddress.getName());
//			order.setPhone(memberAddress.getPhone());
//		}
//		else{
//			
//		}
//		order.setCode(genOrderCode());
//		order.setMemberId(member.getId());
//		order.setOldPrice(total);
//		
//		order.setDiscount(member.getDiscount());
//		if(member.getDiscount()!=null&&member.getDiscount()>0&&member.getDiscount()<10){		//会员折扣
//			viptotal=NumberUtil.divide(NumberUtil.multiply(viptotal, member.getDiscount()), 10);
//		}
//		List<FullRule> fullRules=FullRule.dao.find("select * from t_full_rule order by amount asc");
//		int subtractmoney=calcFullReduce(viptotal, fullRules);
//		if(subtractmoney>0){
//			viptotal=NumberUtil.subtract(viptotal, subtractmoney);	//减去满减部分
//			order.setSubtractMoney(subtractmoney);
//		}
//		String balancePay=getPara("balancePay");
//		if(StringUtils.isNotBlank(balancePay)&&Double.parseDouble(balancePay)>0){	//余额支付
//			order.setBalancePay(Double.parseDouble(balancePay));
//			
//			if (member.getBalance()<order.getBalancePay()) {
//				renderJavascript("$.iBox.remove();$.iBox.alert('余额不足!');");
//				return;
//			}
//			viptotal=NumberUtil.subtract(viptotal,order.getBalancePay());
//		}
//		else{
//			order.setBalancePay(0.0);
//		}
//		List<MemberCoupon> memberCoupons=null;
//		String couponids=getPara("couponids");
//		if(StringUtils.isNotBlank(couponids)){		//选择了优惠券
//			memberCoupons=MemberCoupon.dao.find("select * from t_member_coupon where id in("+couponids+")");
//			int coupontotal=0;		//优惠券使用总金额
//			String userCoupons="";
//			for(MemberCoupon memberCoupon:memberCoupons){
//				coupontotal+=memberCoupon.getMoney();
//				userCoupons+=memberCoupon.getCode()+",";
//			}
//			viptotal=NumberUtil.subtract(viptotal,coupontotal);
//			order.setCouponsPay(coupontotal);		//优惠券支付
//			order.setUserCoupons(userCoupons.substring(0,userCoupons.length()-1));
//		}
//		else{
//			order.setCouponsPay(0);
//		}
//		
//		if(viptotal<=0){
//			viptotal=0;
//		}
//		
//		order.setIsPay("0");
//		order.setIsReceive("0");
//		order.setIsSend("0");
//		order.setPriceModified("0");
//		order.setTotalPrice(NumberUtil.toFixed(viptotal, 2));
//		order.setType("0");
//		order.setOrderTime(now);
//		Integer integral=getParaToInt("integral");
//		if(integral!=null&&integral>member.getMemberPoint()){
//			renderJavascript("$.iBox.remove();$.iBox.alert('积分不足!');");
//			return;
//		}
//		if(integral!=null&&integral>0){		//积分兑换金额
//			Double exchangeprice=NumberUtil.toFixed(NumberUtil.divide(integral,CommonConstant.INT_WEIGHT), 2);
//			order.setIntegralPrice(exchangeprice);
//			order.setTotalPrice(NumberUtil.toFixed(NumberUtil.subtract(order.getTotalPrice(), exchangeprice), 2));
//		}
//		order.save();
//		if(order.getBalancePay()!=null&&order.getBalancePay()>0){
//			BalanceRecord balanceRecord=new BalanceRecord();
//			balanceRecord.setMemberId(member.getId());
//			balanceRecord.setAmount(order.getBalancePay());
//			balanceRecord.setBeforeAmount(member.getBalance());
//			member.setBalance(NumberUtil.subtract(member.getBalance(), order.getBalancePay()));		//扣除用户余额
//			balanceRecord.setAfterAmount(member.getBalance());
//			balanceRecord.setBusinessId(order.getId());
//			balanceRecord.setType("0");
//			balanceRecord.setUseTime(new Date());
//			balanceRecord.save();		//保存余额使用记录
//		}
//		if(memberCoupons!=null&&memberCoupons.size()>0){
//			for(MemberCoupon memberCoupon:memberCoupons){
//				memberCoupon.setStatus("1");
//				memberCoupon.setUseTime(now);
//				memberCoupon.setOrderId(order.getId());
//				memberCoupon.update();		//修改优惠券状态
//			}
//		}
//		if(integral!=null&&integral>0){		//积分兑换金额
//			PointLog pointLog=new PointLog();
//			pointLog.setChangeTime(now);
//			pointLog.setMemberId(member.getId());
//			pointLog.setOrderId(order.getId());
//			pointLog.setPoint(integral);
//			pointLog.setReason("0");
//			pointLog.setType("1");
//			pointLog.save();
//			
//			member.setMemberPoint(member.getMemberPoint()-integral);
//		}
//		member.update();
//		setSessionAttr(SESSION_MEMBER, member);
//		
//		for(OrderDetail od:orderDetails){
//			od.setOrderId(order.getId());
//			
//			od.setAvgprice(Double.valueOf(NumberUtil.toFixed((od.getDiscount()>0?od.getVipPrice().doubleValue():od.getPrice().doubleValue()) / viptotal2 * (order.getTotalPrice()+order.getBalancePay()), 2)));
//			od.setTotalPrice(NumberUtil.toFixed(od.getAvgprice()*od.getAmount(),2));
//			
//			od.save();
//			
//			if(StringUtils.isNotBlank(from)&&"cart".equals(from)){		//购物车结算之后清除购物车
//				Db.update("delete from t_shopping_car where memberid=? and goodsDetailId=?",member.getId(),od.getGoodsDetailId());
//			}
//		}
//		
//		if(viptotal>0){
//			Map<String, String> unifiedorder = new HashMap<String, String>();
//			unifiedorder.put("appid", ApiConfigKit.getApiConfig().getAppId());// 公众账号ID
//			unifiedorder.put("mch_id", PropKit.get("mchid"));// 商户号
////	unifiedorder.put("device_info", "WEB");//设备号
//			unifiedorder.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32));//设备号
//			
//			unifiedorder.put("body", "尺尚订单-"+order.getCode());// 商品描述/名称
////	unifiedorder.put("detail", "");//商品详情,String(8192)
////	unifiedorder.put("attach", "");//附加数据
//			unifiedorder.put("out_trade_no", order.getCode());// 商户订单号编号
////	unifiedorder.put("fee_type", "CNY");
//			unifiedorder.put("total_fee",String.valueOf((int)(order.getTotalPrice()*100)));
//			unifiedorder.put("spbill_create_ip", RandomStringGenerator.getIpAddress(this.getRequest()));// 终端IP
////	unifiedorder.put("time_start", "");
////	unifiedorder.put("time_expire", "");
////	unifiedorder.put("goods_tag", "");// 商品标记,代金券或立减优惠功能的参数
//			unifiedorder.put("notify_url", PropKit.get("paynotifyurl"));// 通知地址
//			unifiedorder.put("trade_type", "JSAPI");// 交易类型
//			unifiedorder.put("product_id", String.valueOf(order.getId()));// 
////	unifiedorder.put("limit_pay", "");
//			unifiedorder.put("openid", member.getOpenid());// 用户标识
//			
//			//参数签名,待传key
//			unifiedorder.put("sign", PaymentKit.createSign(unifiedorder, PropKit.get("paternerKey")));
//			
//			System.out.println("统一下单:"+PaymentKit.toXml(unifiedorder));
//			String xmlStr = PaymentApi.pushOrder(unifiedorder);
//			System.out.println("统一下单结果："+xmlStr);
//			Map<String, Object> map = XMLParser.getMapFromXML(xmlStr);
//			if("SUCCESS".equals(map.get("result_code"))){
//				// H5 jsapi调用微信支付
//				Map<String, String> jsparas = new HashMap<String, String>();
//				jsparas.put("appId", map.get("appid").toString());
//				jsparas.put("timeStamp", String.valueOf(System.currentTimeMillis()));
//				jsparas.put("nonceStr", map.get("nonce_str").toString());
//				jsparas.put("package", "prepay_id=" + map.get("prepay_id"));
//				jsparas.put("signType", "MD5");
//				jsparas.put("paySign", PaymentKit.createSign(jsparas, PropKit.get("paternerKey")));
//				
//				this.setAttr("jsparas", JsonUtils.toJson(jsparas));
//				
//				jspay = "function onBridgeReady(){" +
//						"WeixinJSBridge.invoke(" +
//						"'getBrandWCPayRequest'," +
//						this.getAttr("jsparas") + "," +
//						"function(res){" +
//						"if(res.err_msg == 'get_brand_wcpay_request:ok'){$.iBox.remove();window.location.href='"+ this.getRequest().getContextPath() +"/order/orderList?type=1';}else if(res.err_msg == 'get_brand_wcpay_request:cancel'){$.iBox.remove();alert('支付取消');}else{alert('支付失败');}" +
//						"}" +
//						");" +
//						"}" +
//						"if(typeof WeixinJSBridge == 'undefined'){" +
//						"if(document.addEventListener){" +
//						"document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);" +
//						"}else if(document.attachEvent){" +
//						"document.attachEvent('WeixinJSBridgeReady', onBridgeReady); " +
//						"document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);" +
//						"}" +
//						"}else{" +
//						"onBridgeReady();" +
//						"}";
//				
//			}else{
//				jspay = "$.iBox.remove();alert('"+ map.get("err_code_des") +"!');";
//			}
//		}
//		else{		//完全使用余额支付
//			order.setIsPay("1");
//			order.setPayTime(now);
//			order.setPayway("0");
//			
//			for(OrderDetail od:orderDetails){	//减少商品库存增加销量
//				Db.update("update t_goods set soldCnt=soldCnt+? where id=(select goodsid from t_goods_detail gd where gd.id=?)",od.getAmount(),od.getGoodsDetailId()) ;		//增加销量
//				Db.update("update t_goods_detail set amount=amount-? where id=?",od.getAmount(),od.getGoodsDetailId());		//减少库存
//			}
//			
//			LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
//			int thispoint=new Double(NumberUtil.multiply(NumberUtil.add(order.getTotalPrice(), order.getBalancePay()), levelSetting.getPointSpeed())).intValue();
//			order.setIntegral(thispoint);
//			
//			member.setMemberPoint(member.getMemberPoint()+thispoint);		//增加用户消费积分
//			member.setTotalmoney(NumberUtil.toFixed(NumberUtil.add(member.getTotalmoney(),order.getTotalPrice()), 2));		//增加消费金额
//			Date lasttime=member.getLastBuyTime();
//			if(lasttime==null){
//				MemberTypeChange memberTypeChange=new MemberTypeChange();
//				memberTypeChange.setMemberId(member.getId());
//				memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
//				memberTypeChange.setAfterType("N");
//				memberTypeChange.setChangeTime(now);
//				memberTypeChange.save();
//				
//				member.setMemberType("N");		//第一次购买为新顾客
//				
//				member.setOldornew("1");
//				
//				if(member.getFromUser()!=null){		//邀请人不为空送券
//					Coupon invitedcoupon=Coupon.dao.findById(CommonConstant.INVITED_COUPON_ID);		//被邀请人优惠券
//					if("0".equals(invitedcoupon.getStatus())){
//						MemberCoupon memberCoupon=new MemberCoupon();
//						memberCoupon.setMemberId(member.getId());
//						SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
//						memberCoupon.setCode("J"+sdf.format(new Date()));
//						memberCoupon.setCouponId(invitedcoupon.getId());
//						memberCoupon.setName(invitedcoupon.getName());
//						memberCoupon.setSubtitle(invitedcoupon.getSubtitle());
//						memberCoupon.setRemark(invitedcoupon.getRemark());
//						memberCoupon.setLogoUrl(invitedcoupon.getLogoUrl());
//						memberCoupon.setMoney(invitedcoupon.getMoney());
//						if("0".equals(invitedcoupon.getTimeType())){		//优惠券有效期为固定日期
//							memberCoupon.setStartTime(invitedcoupon.getStartTime());
//							memberCoupon.setEndTime(invitedcoupon.getEndTime());
//						}
//						else{		//优惠券有效期为固定天数
//							Calendar end=Calendar.getInstance();
//							end.add(Calendar.DAY_OF_MONTH, invitedcoupon.getDays());
//							Calendar calendar=Calendar.getInstance();
//							calendar.set(Calendar.HOUR_OF_DAY, 0);
//							calendar.set(Calendar.MINUTE, 0);
//							calendar.set(Calendar.SECOND, 0);
//							memberCoupon.setStartTime(calendar.getTime());
//							memberCoupon.setEndTime(end.getTime());
//						}
//						memberCoupon.setStatus("0");
//						memberCoupon.save();		//被邀请人优惠券
//					}
//					
//					Coupon invitercoupon=Coupon.dao.findById(CommonConstant.INVITER_COUPON_ID);		//邀请人优惠券
//					if("0".equals(invitercoupon.getStatus())){
//						MemberCoupon memberCoupon=new MemberCoupon();
//						memberCoupon.setMemberId(member.getFromUser());
//						SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
//						memberCoupon.setCode("J"+sdf.format(new Date()));
//						memberCoupon.setCouponId(invitercoupon.getId());
//						memberCoupon.setName(invitercoupon.getName());
//						memberCoupon.setSubtitle(invitercoupon.getSubtitle());
//						memberCoupon.setRemark(invitercoupon.getRemark());
//						memberCoupon.setLogoUrl(invitercoupon.getLogoUrl());
//						memberCoupon.setMoney(invitercoupon.getMoney());
//						if("0".equals(invitercoupon.getTimeType())){		//优惠券有效期为固定日期
//							memberCoupon.setStartTime(invitercoupon.getStartTime());
//							memberCoupon.setEndTime(invitercoupon.getEndTime());
//						}
//						else{		//优惠券有效期为固定天数
//							Calendar end=Calendar.getInstance();
//							end.add(Calendar.DAY_OF_MONTH, invitercoupon.getDays());
//							Calendar calendar=Calendar.getInstance();
//							calendar.set(Calendar.HOUR_OF_DAY, 0);
//							calendar.set(Calendar.MINUTE, 0);
//							calendar.set(Calendar.SECOND, 0);
//							memberCoupon.setStartTime(calendar.getTime());
//							memberCoupon.setEndTime(end.getTime());
//						}
//						memberCoupon.setStatus("0");
//						memberCoupon.save();		//邀请人优惠券
//					}
//				}
//			}
//			else{
//				member.setOldornew("2");
//				int buyperiod=CacheUtil.getBuyPeriod();		//获取购买周期设置的天数
//				double days=DateUtil.daysRange(lasttime, now);
//				if(days<buyperiod&&!"E".equals(member.getMemberType())){
//					MemberTypeChange memberTypeChange=new MemberTypeChange();
//					memberTypeChange.setMemberId(member.getId());
//					memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
//					memberTypeChange.setAfterType("E");
//					memberTypeChange.setChangeTime(now);
//					memberTypeChange.save();
//					
//					member.setMemberType("E");		//一个购买周期非第一次购买
//				}
//				if("S1".equals(member.getMemberType())){
//					MemberTypeChange memberTypeChange=new MemberTypeChange();
//					memberTypeChange.setMemberId(member.getId());
//					memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
//					memberTypeChange.setAfterType("X1");
//					memberTypeChange.setChangeTime(now);
//					memberTypeChange.save();
//					
//					member.setMemberType("X1");		//唤醒瞌睡
//				}
//				if("S2".equals(member.getMemberType())){
//					MemberTypeChange memberTypeChange=new MemberTypeChange();
//					memberTypeChange.setMemberId(member.getId());
//					memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
//					memberTypeChange.setAfterType("X2");
//					memberTypeChange.setChangeTime(now);
//					memberTypeChange.save();
//					
//					member.setMemberType("X2");		//唤醒半睡
//				}
//				if("S3".equals(member.getMemberType())){
//					MemberTypeChange memberTypeChange=new MemberTypeChange();
//					memberTypeChange.setMemberId(member.getId());
//					memberTypeChange.setBeforeType(member.getMemberType()==null?"":member.getMemberType());
//					memberTypeChange.setAfterType("X3");
//					memberTypeChange.setChangeTime(now);
//					memberTypeChange.save();
//					
//					member.setMemberType("X3");		//唤醒沉睡
//				}
//			}
//			
//			LevelSetting newlevelsetting=LevelSetting.dao.getCurrentLevelByMoney(member.getTotalmoney());
//			if(newlevelsetting.getGrade()!=member.getMemberLevel()){	//会员等级变动
//				member.setMemberLevel(newlevelsetting.getGrade());		//会员等级
//				member.setLevelChangeTime(now);		//会员等级变动日期
//				
//				if(member.getDiscount()==null||member.getDiscount()==0){
//					member.setDiscount(newlevelsetting.getDiscount());
//				}
//				else{
//					member.setDiscount(member.getDiscount()>newlevelsetting.getDiscount()?newlevelsetting.getDiscount():member.getDiscount());
//				}
//			}
//			
//			member.setLastBuyTime(now);
//			member.update();
//			order.update();
//			if(thispoint>0){
//				
//				PointLog pointLog=new PointLog();
//				pointLog.setChangeTime(now);
//				pointLog.setMemberId(member.getId());
//				pointLog.setOrderId(order.getId());
//				pointLog.setPoint(thispoint);
//				pointLog.setReason("0");
//				pointLog.setType("0");
//				pointLog.save();		//插入积分变更记录(增加)
//			}
//			
//			String names="";
//			List<OrderDetail> details=OrderDetail.dao.find("select * from t_order_detail where orderId=?",order.getId());
//			for(OrderDetail d:details){
//				names+=d.getGoodsName()+",";
//			}
//			if(names.length()>0){
//				names=names.substring(0,names.length()-1);
//			}
//			
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			//发送模板消息给用户
//			TemplateMsgApi.send(TemplateMsgUtil.generateMgsJson(member.getOpenid(), TemplateMsgUtil.template_id_order, CommonConstant.WEIXIN_DOMAIN+"member/memberCenter", TemplateMsgUtil.generateOrderSuccessMsgData("下单成功，订单编号："+order.getCode()+"。我们已收到您的货款，开始为您打包商品，请耐心等待: )", String.valueOf(order.getTotalPrice())+"元", names, "如有问题请致电尺尚客服或直接在微信留言，尺尚将第一时间为您服务！")));
//			//发送模板消息给管理员
//			for(String openid:CommonConstant.MANAGER_OPENID){
//				TemplateMsgApi.send(TemplateMsgUtil.generateMgsJson(openid, TemplateMsgUtil.template_id_neworder, CommonConstant.WEIXIN_DOMAIN, TemplateMsgUtil.generateNewOrder("您有新的订单:"+order.getCode(), member.getNickname(), sdf.format(order.getOrderTime()), names, String.valueOf(order.getTotalPrice())+"元", member.getPhone(), "买家留言："+(StringUtils.isNotBlank(order.getRemark())?order.getRemark():"无"))));
//			}
//			
//			LabelThread labelThread=new LabelThread(member);
//			ExecutorService e = Executors.newFixedThreadPool(1);
//			e.execute(labelThread);
//			
//			jspay = "$.iBox.remove();window.location.href='"+ this.getRequest().getContextPath() +"/order/orderList?type=1'";
//		}
//		
//		ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
//		getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
//		
//		renderJavascript(jspay);
//	}
	/**
	 * 订单提交
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	@Before(Tx.class)
	public void saveOrder() throws ParserConfigurationException, IOException, SAXException{
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		String jspay = "$.iBox.remove();$.iBox.alert('订单异常!');";
		String json=getPara("json");
		String from=getPara("from");		//订单来源
		if(StringUtils.isBlank(json)){
			renderJavascript("$.iBox.remove();$.iBox.alert('请先选择商品!');");
			return;
		}
		member=Member.dao.findById(member.getId());
		setSessionAttr(SESSION_MEMBER, member);
		JSONArray jsonArray=JSONArray.parseArray(json);
		List<OrderDetail> orderDetails=new ArrayList<OrderDetail>();
		Order order=new Order();
		Date now=new Date();
		double total=0;
		double viptotal=0;
		for(int i=0;i<jsonArray.size();i++){
			JSONObject obj=jsonArray.getJSONObject(i);
			Long id=obj.getLong("id");
			int num=obj.getIntValue("num");
			GoodsDetail goodsDetail=GoodsDetail.dao.findById(id);
			Goods goods=Goods.dao.findById(goodsDetail.getGoodsId());
			
			if(num>goodsDetail.getAmount()){		//购买数量不能大于库存验证
				renderJavascript("$.iBox.remove();$.iBox.alert('库存不足!');");
				return;
			}
			
			total=NumberUtil.add(total, NumberUtil.multiply(num, goodsDetail.getPrice()));
			if("2".equals(goods.getIsDiscount())){		//折扣中的以折扣价计算
				viptotal=NumberUtil.add(viptotal, NumberUtil.multiply(num, goodsDetail.getVipPrice()));
			}
			else{
				viptotal=NumberUtil.add(viptotal, NumberUtil.multiply(num, goodsDetail.getPrice()));
			}
			OrderDetail orderDetail=new OrderDetail();
			orderDetail.setAmount(num);
			orderDetail.setColorId(goodsDetail.getColorId());
			orderDetail.setCostPrice(goodsDetail.getCostPrice());
			orderDetail.setGoodsDetailId(goodsDetail.getId());
			orderDetail.setGoodsName(goods.getName());
			orderDetail.setOldPrice(goodsDetail.getOldPrice());
			orderDetail.setPrice(goodsDetail.getPrice());
			orderDetail.setSizeId(goodsDetail.getSizeId());
			orderDetail.setTotalPrice(NumberUtil.toFixed(NumberUtil.multiply(num, goodsDetail.getVipPrice()), 2));
			orderDetail.setVipPrice(goodsDetail.getVipPrice());
			orderDetail.setDiscount("2".equals(goods.getIsDiscount())?goods.getDiscount():0);
			orderDetails.add(orderDetail);
		}
		double viptotal2=viptotal;
		String delWay=getPara("delWay");
		if(StringUtils.isBlank(delWay)){
			delWay="0";
		}
		order.setDelWay(delWay);
		order.setRemark(getPara("remark"));
		MemberAddress memberAddress=MemberAddress.dao.findById(getPara("addrid"));		//用户选择的收货地址
		if(delWay.equals("0")){		//商家配送
			order.setAddress(memberAddress.getAddress());
			order.setReveiveName(memberAddress.getName());
			order.setPhone(memberAddress.getPhone());
		}
		else{
			
		}
		order.setCode(genOrderCode());
		order.setMemberId(member.getId());
		order.setOldPrice(total);
		
		order.setDiscount(member.getDiscount());
		
		List<MemberCoupon> memberCoupons=null;
		String couponids=getPara("couponids");
		if(StringUtils.isNotBlank(couponids)){		//选择了优惠券
			memberCoupons=MemberCoupon.dao.find("select * from t_member_coupon where id in("+couponids+")");
			int coupontotal=0;		//优惠券使用总金额
			String userCoupons="";
			for(MemberCoupon memberCoupon:memberCoupons){
				if(memberCoupon.getLimitmoney()>viptotal2){
					renderJavascript("$.iBox.remove();$.iBox.alert('订单金额未达到优惠券限制金额，无法使用!');");
					return;
				}
				coupontotal+=memberCoupon.getMoney();
				userCoupons+=memberCoupon.getCode()+",";
			}
			viptotal=NumberUtil.subtract(viptotal,coupontotal);
			order.setCouponsPay(coupontotal);		//优惠券支付
			order.setUserCoupons(userCoupons.substring(0,userCoupons.length()-1));
		}
		else{
			order.setCouponsPay(0);
		}
		
		List<FullRule> fullRules=FullRule.dao.find("select * from t_full_rule order by amount asc");
		int subtractmoney=calcFullReduce(viptotal, fullRules);
		if(subtractmoney>0){
			viptotal=NumberUtil.subtract(viptotal, subtractmoney);	//减去满减部分
			order.setSubtractMoney(subtractmoney);
		}
		
		if(member.getDiscount()!=null&&member.getDiscount()>0&&member.getDiscount()<10){		//会员折扣
			viptotal=NumberUtil.divide(NumberUtil.multiply(viptotal, member.getDiscount()), 10);
		}
		
		String balancePay=getPara("balancePay");
		if(StringUtils.isNotBlank(balancePay)&&Double.parseDouble(balancePay)>0){	//余额支付
			order.setBalancePay(Double.parseDouble(balancePay));
			
			if (member.getBalance()<order.getBalancePay()) {
				renderJavascript("$.iBox.remove();$.iBox.alert('余额不足!');");
				return;
			}
			viptotal=NumberUtil.subtract(viptotal,order.getBalancePay());
		}
		else{
			order.setBalancePay(0.0);
		}
		
		
		if(viptotal<=0){
			viptotal=0;
		}
		
		order.setIsPay("0");
		order.setIsReceive("0");
		order.setIsSend("0");
		order.setPriceModified("0");
		order.setTotalPrice(NumberUtil.toFixed(viptotal, 2));
		order.setType("0");
		order.setOrderTime(now);
		Integer integral=getParaToInt("integral");
		if(integral!=null&&integral>member.getMemberPoint()){
			renderJavascript("$.iBox.remove();$.iBox.alert('积分不足!');");
			return;
		}
		if(integral!=null&&integral>0){		//积分兑换金额
			Double exchangeprice=NumberUtil.toFixed(NumberUtil.divide(integral,CommonConstant.INT_WEIGHT), 2);
			order.setIntegralPrice(exchangeprice);
			order.setTotalPrice(NumberUtil.toFixed(NumberUtil.subtract(order.getTotalPrice(), exchangeprice), 2));
		}
		order.save();
		if(order.getBalancePay()!=null&&order.getBalancePay()>0){
			BalanceRecord balanceRecord=new BalanceRecord();
			balanceRecord.setMemberId(member.getId());
			balanceRecord.setAmount(order.getBalancePay());
			balanceRecord.setBeforeAmount(member.getBalance());
			member.setBalance(NumberUtil.subtract(member.getBalance(), order.getBalancePay()));		//扣除用户余额
			balanceRecord.setAfterAmount(member.getBalance());
			balanceRecord.setBusinessId(order.getId());
			balanceRecord.setType("0");
			balanceRecord.setUseTime(new Date());
			balanceRecord.save();		//保存余额使用记录
		}
		if(memberCoupons!=null&&memberCoupons.size()>0){
			for(MemberCoupon memberCoupon:memberCoupons){
				memberCoupon.setStatus("1");
				memberCoupon.setUseTime(now);
				memberCoupon.setOrderId(order.getId());
				memberCoupon.update();		//修改优惠券状态
			}
		}
		if(integral!=null&&integral>0){		//积分兑换金额
			PointLog pointLog=new PointLog();
			pointLog.setChangeTime(now);
			pointLog.setMemberId(member.getId());
			pointLog.setOrderId(order.getId());
			pointLog.setPoint(integral);
			pointLog.setReason("0");
			pointLog.setType("1");
			pointLog.save();
			
			member.setMemberPoint(member.getMemberPoint()-integral);
		}
		member.update();
		setSessionAttr(SESSION_MEMBER, member);
		
		for(OrderDetail od:orderDetails){
			od.setOrderId(order.getId());
			
			od.setAvgprice(Double.valueOf(NumberUtil.toFixed((od.getDiscount()>0?od.getVipPrice().doubleValue():od.getPrice().doubleValue()) / viptotal2 * (order.getTotalPrice()+order.getBalancePay()), 2)));
			od.setTotalPrice(NumberUtil.toFixed(od.getAvgprice()*od.getAmount(),2));
			
			od.save();
			
			if(StringUtils.isNotBlank(from)&&"cart".equals(from)){		//购物车结算之后清除购物车
				Db.update("delete from t_shopping_car where memberid=? and goodsDetailId=?",member.getId(),od.getGoodsDetailId());
			}
		}
		
		if(viptotal>0){
			Map<String, String> unifiedorder = new HashMap<String, String>();
			unifiedorder.put("appid", ApiConfigKit.getApiConfig().getAppId());// 公众账号ID
			unifiedorder.put("mch_id", PropKit.get("mchid"));// 商户号
//	unifiedorder.put("device_info", "WEB");//设备号
			unifiedorder.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32));//设备号
			
			unifiedorder.put("body", "尺尚订单-"+order.getCode());// 商品描述/名称
//	unifiedorder.put("detail", "");//商品详情,String(8192)
//	unifiedorder.put("attach", "");//附加数据
			unifiedorder.put("out_trade_no", order.getCode());// 商户订单号编号
//	unifiedorder.put("fee_type", "CNY");
			unifiedorder.put("total_fee",String.valueOf((int)(order.getTotalPrice()*100)));
			unifiedorder.put("spbill_create_ip", RandomStringGenerator.getIpAddress(this.getRequest()));// 终端IP
//	unifiedorder.put("time_start", "");
//	unifiedorder.put("time_expire", "");
//	unifiedorder.put("goods_tag", "");// 商品标记,代金券或立减优惠功能的参数
			unifiedorder.put("notify_url", PropKit.get("paynotifyurl"));// 通知地址
			unifiedorder.put("trade_type", "JSAPI");// 交易类型
			unifiedorder.put("product_id", String.valueOf(order.getId()));// 
//	unifiedorder.put("limit_pay", "");
			unifiedorder.put("openid", member.getOpenid());// 用户标识
			
			//参数签名,待传key
			unifiedorder.put("sign", PaymentKit.createSign(unifiedorder, PropKit.get("paternerKey")));
			
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
						"if(res.err_msg == 'get_brand_wcpay_request:ok'){$.iBox.remove();window.location.href='"+ this.getRequest().getContextPath() +"/order/orderList?type=1';}else if(res.err_msg == 'get_brand_wcpay_request:cancel'){$.iBox.remove();alert('支付取消');}else{alert('支付失败');}" +
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
		}
		else{		//完全使用余额支付
			order.setIsPay("1");
			order.setPayTime(now);
			order.setPayway("0");
			
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
						memberCoupon.setLimitmoney(invitedcoupon.getLimitmoney());
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
						memberCoupon.setLimitmoney(invitercoupon.getLimitmoney());
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
			
			jspay = "$.iBox.remove();window.location.href='"+ this.getRequest().getContextPath() +"/order/orderList?type=1'";
		}
		
		ShoppingCar shoppingCar=ShoppingCar.dao.findFirst("select count(1) cnt from t_shopping_car where memberid=?",member.getId());
		getSession().setAttribute(SESSION_SHOPPINGCAR, shoppingCar.getLong("cnt"));
		
		renderJavascript(jspay);
	}
//	/**
//	 * 订单提交
//	 * @throws SAXException 
//	 * @throws IOException 
//	 * @throws ParserConfigurationException 
//	 */
//	@Before(Tx.class)
//	public void saveOrder() throws ParserConfigurationException, IOException, SAXException{
//		Member member=getMember();
//		if(member==null){
//			renderNull();
//			return;
//		}
//		
//		String jspay = "alert('订单异常!');";
//		String json=getPara("json");
//		String from=getPara("from");		//订单来源
//		if(StringUtils.isBlank(json)){
//			renderJavascript("$.iBox.alert('请先选择商品!');");
//			return;
//		}
//		member=Member.dao.findById(member.getId());
//		setSessionAttr(SESSION_MEMBER, member);
//		JSONArray jsonArray=JSONArray.parseArray(json);
//		List<OrderDetail> orderDetails=new ArrayList<OrderDetail>();
//		Order order=new Order();
//		Date now=new Date();
//		double total=0;
//		double viptotal=0;
//		for(int i=0;i<jsonArray.size();i++){
//			JSONObject obj=jsonArray.getJSONObject(i);
//			Long id=obj.getLong("id");
//			int num=obj.getIntValue("num");
//			GoodsDetail goodsDetail=GoodsDetail.dao.findById(id);
//			Goods goods=Goods.dao.findById(goodsDetail.getGoodsId());
//			
//			if(num>goodsDetail.getAmount()){		//购买数量不能大于库存验证
//				renderJavascript("$.iBox.alert('库存不足!');");
//				return;
//			}
//			
//			total=NumberUtil.add(total, NumberUtil.multiply(num, goodsDetail.getPrice()));
//			viptotal=NumberUtil.add(viptotal, NumberUtil.multiply(num, goodsDetail.getPrice()));
//			OrderDetail orderDetail=new OrderDetail();
//			orderDetail.setAmount(num);
//			orderDetail.setColorId(goodsDetail.getColorId());
//			orderDetail.setCostPrice(goodsDetail.getCostPrice());
//			orderDetail.setGoodsDetailId(goodsDetail.getId());
//			orderDetail.setGoodsName(goods.getName());
//			orderDetail.setOldPrice(goodsDetail.getOldPrice());
//			orderDetail.setPrice(goodsDetail.getPrice());
//			orderDetail.setSizeId(goodsDetail.getSizeId());
//			orderDetail.setTotalPrice(NumberUtil.toFixed(NumberUtil.multiply(num, goodsDetail.getPrice()), 2));
//			orderDetail.setVipPrice(goodsDetail.getVipPrice());
//			orderDetails.add(orderDetail);
//		}
//		String delWay=getPara("delWay");
//		order.setDelWay(delWay);
//		order.setPhone(getPara("phone"));
//		order.setRemark(getPara("remark"));
//		if(delWay.equals("0")){		//商家配送
//			order.setAddress(getPara("address"));
//			order.setReveiveName(getPara("reveivename"));
//		}
//		else{
//			
//		}
//		order.setCode(genOrderCode());
//		order.setMemberId(member.getId());
//		order.setOldPrice(total);
//		order.setDiscount(total-viptotal);
//		order.setIsPay("0");
//		order.setIsReceive("0");
//		order.setIsSend("0");
//		order.setPriceModified("0");
//		order.setTotalPrice(NumberUtil.toFixed(viptotal, 2));
//		order.setType("0");
//		order.setOrderTime(now);
//		Integer integral=getParaToInt("integral");
//		if(integral!=null&&integral>member.getMemberPoint()){
//			renderJavascript("$.iBox.alert('积分不足!');");
//			return;
//		}
//		if(integral!=null&&integral>0){		//积分兑换金额
//			Double exchangeprice=NumberUtil.toFixed(NumberUtil.divide(integral,CommonConstant.INT_WEIGHT), 2);
//			order.setIntegralPrice(exchangeprice);
//			order.setTotalPrice(NumberUtil.toFixed(NumberUtil.subtract(order.getTotalPrice(), exchangeprice), 2));
//		}
//		order.save();
//		if(integral!=null&&integral>0){		//积分兑换金额
//			PointLog pointLog=new PointLog();
//			pointLog.setChangeTime(now);
//			pointLog.setMemberId(member.getId());
//			pointLog.setOrderId(order.getId());
//			pointLog.setPoint(integral);
//			pointLog.setReason("0");
//			pointLog.setType("1");
//			pointLog.save();
//			
//			member.setMemberPoint(member.getMemberPoint()-integral);
//			member.update();
//			setSessionAttr(SESSION_MEMBER, member);
//		}
//		
//		for(OrderDetail od:orderDetails){
//			od.setOrderId(order.getId());
//			od.save();
//			
//			if(StringUtils.isNotBlank(from)&&"cart".equals(from)){		//购物车结算之后清除购物车
//				Db.update("delete from t_shopping_car where memberid=? and goodsDetailId=?",member.getId(),od.getGoodsDetailId());
//			}
//		}
//		
//		Map<String, String> unifiedorder = new HashMap<String, String>();
//		unifiedorder.put("appid", ApiConfigKit.getApiConfig().getAppId());// 公众账号ID
//		unifiedorder.put("mch_id", PropKit.get("mchid"));// 商户号
////	unifiedorder.put("device_info", "WEB");//设备号
//		unifiedorder.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32));//设备号
//		
//		unifiedorder.put("body", "尺尚订单-"+order.getCode());// 商品描述/名称
////	unifiedorder.put("detail", "");//商品详情,String(8192)
////	unifiedorder.put("attach", "");//附加数据
//		unifiedorder.put("out_trade_no", order.getCode());// 商户订单号编号
////	unifiedorder.put("fee_type", "CNY");
//		unifiedorder.put("total_fee",String.valueOf((int)(order.getTotalPrice()*100)));
//		unifiedorder.put("spbill_create_ip", RandomStringGenerator.getIpAddress(this.getRequest()));// 终端IP
////	unifiedorder.put("time_start", "");
////	unifiedorder.put("time_expire", "");
////	unifiedorder.put("goods_tag", "");// 商品标记,代金券或立减优惠功能的参数
//		unifiedorder.put("notify_url", PropKit.get("paynotifyurl"));// 通知地址
//		unifiedorder.put("trade_type", "JSAPI");// 交易类型
//		unifiedorder.put("product_id", String.valueOf(order.getId()));// 
////	unifiedorder.put("limit_pay", "");
//		unifiedorder.put("openid", member.getOpenid());// 用户标识
//		
//		//参数签名,待传key
//		unifiedorder.put("sign", PaymentKit.createSign(unifiedorder, PropKit.get("paternerKey")));
//		
//		System.out.println("统一下单:"+PaymentKit.toXml(unifiedorder));
//		String xmlStr = PaymentApi.pushOrder(unifiedorder);
//		System.out.println("统一下单结果："+xmlStr);
//		Map<String, Object> map = XMLParser.getMapFromXML(xmlStr);
//		if("SUCCESS".equals(map.get("result_code"))){
//			// H5 jsapi调用微信支付
//			Map<String, String> jsparas = new HashMap<String, String>();
//			jsparas.put("appId", map.get("appid").toString());
//			jsparas.put("timeStamp", String.valueOf(System.currentTimeMillis()));
//			jsparas.put("nonceStr", map.get("nonce_str").toString());
//			jsparas.put("package", "prepay_id=" + map.get("prepay_id"));
//			jsparas.put("signType", "MD5");
//			jsparas.put("paySign", PaymentKit.createSign(jsparas, PropKit.get("paternerKey")));
//			
//			this.setAttr("jsparas", JsonUtils.toJson(jsparas));
//			
//			jspay = "function onBridgeReady(){" +
//					"WeixinJSBridge.invoke(" +
//					"'getBrandWCPayRequest'," +
//					this.getAttr("jsparas") + "," +
//					"function(res){" +
//					"if(res.err_msg == 'get_brand_wcpay_request:ok'){window.location.href='"+ this.getRequest().getContextPath() +"/order/orderList?type=1';}else if(res.err_msg == 'get_brand_wcpay_request:cancel'){alert('支付取消');}else{alert('支付失败');}" +
//					"}" +
//					");" +
//					"}" +
//					"if(typeof WeixinJSBridge == 'undefined'){" +
//					"if(document.addEventListener){" +
//					"document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);" +
//					"}else if(document.attachEvent){" +
//					"document.attachEvent('WeixinJSBridgeReady', onBridgeReady); " +
//					"document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);" +
//					"}" +
//					"}else{" +
//					"onBridgeReady();" +
//					"}";
//			
//		}else{
//			jspay = "alert('"+ map.get("err_code_des") +"!');";
//		}
//		
//		renderJavascript(jspay);
//	}
	
	/**
	 * 生成订单编号
	 * @return
	 */
	private String genOrderCode(){
		Date now=new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMM");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String seqname="order"+sdf1.format(now);
		Record record=Db.findFirst("select seqnextval(?) as seqval",seqname);
		Long seqval=record.getLong("seqval");
		return sdf.format(now)+String.format("%06d", seqval);
	}
	
	public void orderList(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		String type=getPara("type");
		setAttr("type", type);
		String sql="";
		if(type==null||"".equals(type)){	//全部订单
			sql="select o.*,g.id goodsId,g.name goodsName,g.img,s1.specvalue colorName,s2.specvalue sizeName,od.amount,od.totalPrice simgletotal from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail gd on gd.id=od.goodsdetailid left join t_goods g on g.id=gd.goodsid left join t_spec s1 on s1.id=od.colorid left join t_spec s2 on s2.id=od.sizeid"
					+" where o.memberId=? order by o.orderTime desc ";
		}
		else if("0".equals(type)){	//未支付
			sql="select o.*,g.id goodsId,g.name goodsName,g.img,s1.specvalue colorName,s2.specvalue sizeName,od.amount,od.discount,od.vipPrice,od.price,od.totalPrice simgletotal from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail gd on gd.id=od.goodsdetailid left join t_goods g on g.id=gd.goodsid left join t_spec s1 on s1.id=od.colorid left join t_spec s2 on s2.id=od.sizeid"
					+" where o.memberId=? and  o.ispay=0 and o.issend=0 and o.isreceive=0 order by o.orderTime desc ";
		}
		else if("1".equals(type)){	//已支付未发货
			sql="select o.*,g.id goodsId,g.name goodsName,g.img,s1.specvalue colorName,s2.specvalue sizeName,od.amount,od.discount,od.vipPrice,od.price,od.totalPrice simgletotal from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail gd on gd.id=od.goodsdetailid left join t_goods g on g.id=gd.goodsid left join t_spec s1 on s1.id=od.colorid left join t_spec s2 on s2.id=od.sizeid"
					+" where o.memberId=? and  o.ispay=1 and o.issend=0 and o.isreceive=0 order by o.orderTime desc ";
		}
		else if("2".equals(type)){	//未收货
			sql="select o.*,g.id goodsId,g.name goodsName,g.img,s1.specvalue colorName,s2.specvalue sizeName,od.amount,od.discount,od.vipPrice,od.price,od.totalPrice simgletotal from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail gd on gd.id=od.goodsdetailid left join t_goods g on g.id=gd.goodsid left join t_spec s1 on s1.id=od.colorid left join t_spec s2 on s2.id=od.sizeid"
					+" where o.memberId=? and  o.ispay=1 and o.issend=1 and o.isreceive=0 order by o.orderTime desc ";
		}
		else if("3".equals(type)){	//已完成
			sql="select o.*,g.id goodsId,g.name goodsName,g.img,s1.specvalue colorName,s2.specvalue sizeName,od.amount,od.discount,od.vipPrice,od.price,od.totalPrice simgletotal from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail gd on gd.id=od.goodsdetailid left join t_goods g on g.id=gd.goodsid left join t_spec s1 on s1.id=od.colorid left join t_spec s2 on s2.id=od.sizeid"
					+" where o.memberId=? and  o.ispay=1 and o.issend=1 and o.isreceive=1 order by o.orderTime desc ";
		}
		List<Order> orders=Order.dao.find(sql,member.getId());
		List<List<Order>> orderList=new ArrayList<List<Order>>();
		for(Order o:orders){
			boolean flag=true;
			for(List<Order> os:orderList){
				if(os.get(0).getId().intValue()==o.getId().intValue()){
					os.add(o);
					flag=false;
				}
			}
			if(flag){
				List<Order> os=new ArrayList<Order>();
				os.add(o);
				orderList.add(os);
			}
		}
		setAttr("orderList", orderList);
		renderVelocity("order/orderList.vm");
	}
	
	public void confirmReceive(){
		Long id=getParaToLong("id");
		Order order=new Order();
		order.setId(id);
		order.setIsReceive("1");
		order.update();
		renderJson("ok",true);
	}
	
	public void cancelOrder(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		member=Member.dao.findById(member.getId());
		Long id=getParaToLong("id");
		Order order=Order.dao.findById(id);
		if("1".equals(order.getIsPay())){
			renderJson("ok",false);
		}
		if(order.getBalancePay()!=null&&order.getBalancePay()>0){		//使用了余额支付，退还余额
			member.setBalance(NumberUtil.add(member.getBalance(), order.getBalancePay()));	//增加用户余额
			
			Db.update("delete from t_balance_record where businessId=?",order.getId());		//删除余额使用记录
		}
		if(order.getCouponsPay()!=null&&order.getCouponsPay()>0){		//使用了优惠券，将优惠券退回去
			Db.update("update t_member_coupon set status=0,useTime=null,orderId=null where orderId=?",order.getId());
		}
		Db.update("delete from t_order where id=?",id);
		Db.update("delete from t_order_detail where orderId=?",id);
		PointLog pointLog=PointLog.dao.findFirst("select * from t_point_log where memberId=? and orderid=?",member.getId(),id);
		if(pointLog!=null&&pointLog.getPoint()>0){
			member.setMemberPoint(member.getMemberPoint()+pointLog.getPoint());
			pointLog.delete();
		}
		member.update();
		setSessionAttr(SESSION_MEMBER, member);
		
		renderJson("ok",true);
	}
	
	/**
	 * 订单列表支付 
	 */
	public void againPay(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		member=Member.dao.findById(member.getId());
		setSessionAttr(SESSION_MEMBER, member);
		Long orderid=getParaToLong("id");
		Order order=Order.dao.findById(orderid);
		List<GoodsDetail> goodsDetails=GoodsDetail.dao.find("select g.name,g.img,g.isdiscount,od.*,s1.specvalue colorName,s2.specvalue sizeName,od.amount odamount,od.totalPrice odtotalPrice from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail d on d.id=od.goodsdetailid left join t_goods g on g.id=d.goodsid left join t_spec s1 on s1.id=d.colorid left join t_spec s2 on s2.id=d.sizeid where o.id=?",orderid);
		for(GoodsDetail goodsDetail:goodsDetails){
			if(goodsDetail.getInt("odamount")>goodsDetail.getAmount()){		//购买数量不能大于库存验证
				renderJavascript("库存不足!");
				return;
			}
		}
		setAttr("order",order);
		setAttr("goodsDetails",goodsDetails);
		setAttr("member",member);
		setAttr("orderTotal", NumberUtil.toFixed(NumberUtil.add(NumberUtil.add(NumberUtil.divide(NumberUtil.add(order.getBalancePay(), order.getTotalPrice()), ((order.getDiscount()>0&&order.getDiscount()<10)?NumberUtil.divide(order.getDiscount(),10):0)), order.getSubtractMoney()), order.getCouponsPay()), 2));
		
		renderVelocity("order/againpayment.vm");
	}
	
	public void orderDetail(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		member=Member.dao.findById(member.getId());
		setSessionAttr(SESSION_MEMBER, member);
		Long orderid=getParaToLong("id");
		Order order=Order.dao.findById(orderid);
		List<GoodsDetail> goodsDetails=GoodsDetail.dao.find("select g.name,g.img,g.isdiscount,d.*,s1.specvalue colorName,s2.specvalue sizeName,od.amount odamount,od.totalPrice odtotalPrice from t_order o left join t_order_detail od on o.id=od.orderid left join t_goods_detail d on d.id=od.goodsdetailid left join t_goods g on g.id=d.goodsid left join t_spec s1 on s1.id=d.colorid left join t_spec s2 on s2.id=d.sizeid where o.id=?",orderid);
		setAttr("order",order);
		setAttr("goodsDetails",goodsDetails);
		setAttr("member",member);
		setAttr("orderTotal", NumberUtil.toFixed(NumberUtil.add(NumberUtil.add(NumberUtil.divide(NumberUtil.add(order.getBalancePay(), order.getTotalPrice()), ((order.getDiscount()>0&&order.getDiscount()<10)?NumberUtil.divide(order.getDiscount(),10):0)), order.getSubtractMoney()), order.getCouponsPay()), 2));
		
		renderVelocity("order/orderDetail.vm");
	}
	
	@Before(Tx.class)
	public void againpayment() throws ParserConfigurationException, IOException, SAXException{
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		member=Member.dao.findById(getMember().getId());
		setSessionAttr(SESSION_MEMBER, member);
		Long id=getParaToLong("orderid");
		Order order=Order.dao.findById(id);
		if(order!=null){
			if(member != null && !"".equals(member.getOpenid())) {
				String jspay = "$.iBox.alert('订单异常!');";
				//保存订单到数据库
				//微信统一下单
				Map<String, String> unifiedorder = new HashMap<String, String>();
				unifiedorder.put("appid", ApiConfigKit.getApiConfig().getAppId());// 公众账号ID
				unifiedorder.put("mch_id", PropKit.get("mchid"));// 商户号
//			unifiedorder.put("device_info", "WEB");//设备号
				unifiedorder.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32));//设备号
				
				unifiedorder.put("body", "尺尚订单-"+order.getCode());// 商品描述/名称
//			unifiedorder.put("detail", "");//商品详情,String(8192)
//			unifiedorder.put("attach", "");//附加数据
				unifiedorder.put("out_trade_no", order.getCode());// 商户订单号编号
//			unifiedorder.put("fee_type", "CNY");
				unifiedorder.put("total_fee",String.valueOf((int)(order.getTotalPrice()*100)));
				unifiedorder.put("spbill_create_ip", RandomStringGenerator.getIpAddress(this.getRequest()));// 终端IP
//			unifiedorder.put("time_start", "");
//			unifiedorder.put("time_expire", "");
//			unifiedorder.put("goods_tag", "");// 商品标记,代金券或立减优惠功能的参数
				unifiedorder.put("notify_url", PropKit.get("paynotifyurl"));// 通知地址
				unifiedorder.put("trade_type", "JSAPI");// 交易类型
				unifiedorder.put("product_id", String.valueOf(order.getId()));// 
//			unifiedorder.put("limit_pay", "");
				unifiedorder.put("openid", member.getOpenid());// 用户标识
				
				//参数签名,待传key
				unifiedorder.put("sign", PaymentKit.createSign(unifiedorder, PropKit.get("paternerKey")));
				String xmlStr = PaymentApi.pushOrder(unifiedorder);
				
				System.out.println(xmlStr);
				
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
							"if(res.err_msg == 'get_brand_wcpay_request:ok'){window.location.href='"+ this.getRequest().getContextPath() +"/order/orderList?type=1';}else if(res.err_msg == 'get_brand_wcpay_request:cancel'){alert('支付取消');}else{alert('支付失败');}" +
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
					jspay = "alert('"+ map.get("err_code_des") +"!');";
				}
				
				renderJavascript(jspay);
			}else{
				renderNull();
			}
		}
	}
	
	/**
	 * 满减计算
	 * @param money
	 * @param fullRules
	 * @return
	 */
	public int calcFullReduce(Double money,List<FullRule> fullRules){
		for(int i=fullRules.size()-1;i>=0;i--){
			if(money>fullRules.get(i).getAmount()){
				return fullRules.get(i).getSubtractAmount();
			}
		}
		return 0;
	}
}
