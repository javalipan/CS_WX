package com.block.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.weixin.sdk.utils.HttpUtils;

public class TemplateMsgUtil {
	
	/**
	 * 下单成功模板通知
	 */
	public final static String template_id_order="EcgIg9KRZR4noC3g4Sb6D8q7PN2X_sv7uozTur1qTv4";
	/**
	 * 发货模板消息通知
	 */
	public final static String template_id_send="YHc4u3QKYnMqOqzwnd5mITP9SuL7JRu0dBJnq_-vIU8";
	/**
	 * 新订单管理员通知模板
	 */
	public final static String template_id_neworder="XLytaa06_CAoz01zIIrKkPSMr526ASJQhzMYMB0bnT0";

	/** 
	* 发送模板消息
	* @param access_token ：access_token
	* @param json ：消息json
	* @return    String
	* @author lipan@cqrainbowsoft.com    
	*/
	public static String sendTemplateMsg(String access_token,String json){
		String url="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token;
		String result=HttpUtils.post(url, json);
		return result;
	}
	
	/** 
	* 生成模板消息消息json
	* @param toUserOpenid :接收用户openid
	* @param url : 点击消息详情按钮跳转地址
	* @param data : 模板消息具体内容
	* @return    String
	* @author lipan@cqrainbowsoft.com    
	*/
	public static String generateMgsJson(String toUserOpenid,String templateid,String url,JSONObject data){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("touser", toUserOpenid);
		jsonObject.put("template_id", templateid);
		jsonObject.put("url", url);
		jsonObject.put("data", data);
		return jsonObject.toJSONString();
	}
	
	/**
	 * 生成下单成功模板消息
	 * @param first :订单消息标题
	 * @param orderMoneySum ： 价格
	 * @param orderProductName :商品名称
	 * @param Remark ：备注
	 * @return
	 */
	public static JSONObject generateOrderSuccessMsgData(String first,String orderMoneySum,String orderProductName,String Remark){
		JSONObject jsonObject=new JSONObject();
		JSONObject firstjson=new JSONObject();
		firstjson.put("value", first);
		firstjson.put("color", "#173177");
		jsonObject.put("first", firstjson);
		JSONObject orderMoneySumobj=new JSONObject();
		orderMoneySumobj.put("value", orderMoneySum);
		orderMoneySumobj.put("color", "#173177");
		jsonObject.put("orderMoneySum", orderMoneySumobj);
		JSONObject orderProductNameobj=new JSONObject();
		orderProductNameobj.put("value", orderProductName);
		orderProductNameobj.put("color", "#173177");
		jsonObject.put("orderProductName", orderProductNameobj);
		JSONObject remarkJson=new JSONObject();
		remarkJson.put("value", Remark);
		remarkJson.put("color", "#173177");
		jsonObject.put("Remark", remarkJson);
		return jsonObject;
	}
	
	/**
	 * 发货成功模板消息
	 * @param first :标题
	 * @param keyword1 ：发货人
	 * @param keyword2 ：发货时间
	 * @param keyword3 ：收货人
	 * @param keyword4 ：收货地址
	 * @param remark ：备注
	 * @return
	 */
	public static JSONObject generateSendSuccess(String first,String keyword1,String keyword2,String keyword3,String keyword4,String remark){
		JSONObject jsonObject=new JSONObject();
		JSONObject firstjson=new JSONObject();
		firstjson.put("value", first);
		firstjson.put("color", "#173177");
		jsonObject.put("first", firstjson);
		JSONObject keyword1Json=new JSONObject();
		keyword1Json.put("value", keyword1);
		keyword1Json.put("color", "#173177");
		jsonObject.put("keyword1", keyword1Json);
		JSONObject keyword2json=new JSONObject();
		keyword2json.put("value", keyword2);
		keyword2json.put("color", "#173177");
		jsonObject.put("keyword2", keyword2json);
		JSONObject keyword3json=new JSONObject();
		keyword3json.put("value", keyword3);
		keyword3json.put("color", "#173177");
		jsonObject.put("keyword3", keyword3json);
		JSONObject keyword4json=new JSONObject();
		keyword4json.put("value", keyword4);
		keyword4json.put("color", "#173177");
		jsonObject.put("keyword4", keyword4json);
		JSONObject remarkJson=new JSONObject();
		remarkJson.put("value", remark);
		remarkJson.put("color", "#173177");
		jsonObject.put("remark", remarkJson);
		return jsonObject;
	}
	
	/**
	 * 新订单管理员通知模板
	 * @param first :标题
	 * @param keyword1 ：下单账号
	 * @param keyword2 ：下单时间
	 * @param keyword3 ：下单产品
	 * @param keyword4 ：下单金额
	 * @param keyword5 ：联系电话
	 * @param remark ：备注
	 * @return
	 */
	public static JSONObject generateNewOrder(String first,String keyword1,String keyword2,String keyword3,String keyword4,String keyword5,String remark){
		JSONObject jsonObject=new JSONObject();
		JSONObject firstjson=new JSONObject();
		firstjson.put("value", first);
		firstjson.put("color", "#173177");
		jsonObject.put("first", firstjson);
		JSONObject keyword1Json=new JSONObject();
		keyword1Json.put("value", keyword1);
		keyword1Json.put("color", "#173177");
		jsonObject.put("keyword1", keyword1Json);
		JSONObject keyword2json=new JSONObject();
		keyword2json.put("value", keyword2);
		keyword2json.put("color", "#173177");
		jsonObject.put("keyword2", keyword2json);
		JSONObject keyword3json=new JSONObject();
		keyword3json.put("value", keyword3);
		keyword3json.put("color", "#173177");
		jsonObject.put("keyword3", keyword3json);
		JSONObject keyword4json=new JSONObject();
		keyword4json.put("value", keyword4);
		keyword4json.put("color", "#173177");
		jsonObject.put("keyword4", keyword4json);
		JSONObject keyword5json=new JSONObject();
		keyword5json.put("value", keyword5);
		keyword5json.put("color", "#173177");
		jsonObject.put("keyword5", keyword5json);
		JSONObject remarkJson=new JSONObject();
		remarkJson.put("value", remark);
		remarkJson.put("color", "#173177");
		jsonObject.put("remark", remarkJson);
		return jsonObject;
	}
}
