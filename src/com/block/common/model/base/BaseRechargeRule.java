package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseRechargeRule <M extends BaseRechargeRule<M>> extends Model<M> implements IBean {


	/**
	 *获取id
	 */
	public Long getId() {
		return get("id");
	}

	/**
	 *设置id
	 */
	public void setId(Long id) {
		set("id",id);
	}

	/**
	 *获取起
	 */
	public Integer getLimitFrom() {
		return get("limitFrom");
	}

	/**
	 *设置起
	 */
	public void setLimitFrom(Integer limitFrom) {
		set("limitFrom",limitFrom);
	}

	/**
	 *获取止
	 */
	public Integer getLimitEnd() {
		return get("limitEnd");
	}

	/**
	 *设置止
	 */
	public void setLimitEnd(Integer limitEnd) {
		set("limitEnd",limitEnd);
	}

	/**
	 *获取赠送金额
	 */
	public Integer getAmount() {
		return get("amount");
	}

	/**
	 *设置赠送金额
	 */
	public void setAmount(Integer amount) {
		set("amount",amount);
	}

	/**
	 *获取享受折扣
	 */
	public Double getDiscount() {
		return get("discount");
	}

	/**
	 *设置享受折扣
	 */
	public void setDiscount(Double discount) {
		set("discount",discount);
	}

}