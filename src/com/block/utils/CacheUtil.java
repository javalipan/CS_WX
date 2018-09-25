package com.block.utils;

import java.util.List;

import com.block.common.model.FullRule;
import com.block.common.model.RechargeRule;
import com.block.common.model.SysSetting;
import com.jfinal.plugin.ehcache.CacheKit;

public class CacheUtil {

	/**
	 * 缓存获取充值规则设置
	 * @return
	 */
	public static List<RechargeRule> getRechargeRules(){
		if(CacheKit.get("CS", "RECHARGERULE")==null){
			List<RechargeRule> rechargeRules=RechargeRule.dao.find("select * from t_recharge_rule");
			CacheKit.put("CS", "RECHARGERULE",rechargeRules);
			return rechargeRules;
		}
		else{
			return CacheKit.get("CS", "RECHARGERULE");
		}
	}
	
	/**
	 * 缓存获取满减设置
	 * @return
	 */
	public static List<FullRule> getFullRules(){
		if(CacheKit.get("CS", "FULLRULE")==null){
			List<FullRule> fullRules=FullRule.dao.find("select * from t_full_rule");
			CacheKit.put("CS", "FULLRULE",fullRules);
			return fullRules;
		}
		else{
			return CacheKit.get("CS", "FULLRULE");
		}
	}
	
	/**
	 * 缓存获取购买周期设置
	 * @return
	 */
	public static int getBuyPeriod(){
		if(CacheKit.get("CS", "BUYPERIOD")==null){
			SysSetting sysSetting=SysSetting.dao.findFirst("select * from t_sys_setting where typekey=?","BUYPERIOD");
			int value=Integer.parseInt(sysSetting.getTypevalue());
			CacheKit.put("CS", "BUYPERIOD",value);
			return value;
		}
		else{
			return CacheKit.get("CS", "BUYPERIOD");
		}
	}
	
	/**
	 * 根据key获取系统设置
	 * @param key
	 * @return
	 */
	public static String getSettingByKey(String key){
		if(CacheKit.get("CS", key)==null){
			SysSetting sysSetting=SysSetting.dao.findFirst("select * from t_sys_setting where typekey=?",key);
			String value=sysSetting.getTypevalue();
			CacheKit.put("CS", key,value);
			return value;
		}
		else{
			return CacheKit.get("CS",key);
		}
	}
	
	/**
	 * 根据金额获取满足条件的满减规则
	 * @param amount
	 */
	public static FullRule getFullRule(Double amount){
		List<FullRule> fullRules=getFullRules();
		FullRule fullRule=null;
		for(FullRule fr:fullRules){
			if(fr.getAmount()<=amount){
				if(fullRule==null){
					fullRule=fr;
				}
				else{
					if(fr.getAmount()>fullRule.getAmount()){
						fullRule=fr;
					}
				}
			}
		}
		return fullRule;
	}
	
	/**
	 * 清除所有缓存
	 */
	public static void clearAllCache(){
		CacheKit.removeAll("CS");
	}
}
