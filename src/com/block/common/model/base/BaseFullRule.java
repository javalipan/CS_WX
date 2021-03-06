package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseFullRule <M extends BaseFullRule<M>> extends Model<M> implements IBean {


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
	 *获取消费金额
	 */
	public Integer getAmount() {
		return get("amount");
	}

	/**
	 *设置消费金额
	 */
	public void setAmount(Integer amount) {
		set("amount",amount);
	}

	/**
	 *获取减少金额
	 */
	public Integer getSubtractAmount() {
		return get("subtractAmount");
	}

	/**
	 *设置减少金额
	 */
	public void setSubtractAmount(Integer subtractAmount) {
		set("subtractAmount",subtractAmount);
	}

}
