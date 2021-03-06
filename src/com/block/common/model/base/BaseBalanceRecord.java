package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseBalanceRecord <M extends BaseBalanceRecord<M>> extends Model<M> implements IBean {


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
	 *获取会员id
	 */
	public Long getMemberId() {
		return get("memberId");
	}

	/**
	 *设置会员id
	 */
	public void setMemberId(Long memberId) {
		set("memberId",memberId);
	}

	/**
	 *获取类型,0:订单消费，1 :优惠券购买
	 */
	public String getType() {
		return get("type");
	}

	/**
	 *设置类型,0:订单消费，1 :优惠券购买
	 */
	public void setType(String type) {
		set("type",type);
	}

	/**
	 *获取单据id
	 */
	public Long getBusinessId() {
		return get("businessId");
	}

	/**
	 *设置单据id
	 */
	public void setBusinessId(Long businessId) {
		set("businessId",businessId);
	}

	/**
	 *获取金额
	 */
	public Double getAmount() {
		return get("amount");
	}

	/**
	 *设置金额
	 */
	public void setAmount(Double amount) {
		set("amount",amount);
	}

	/**
	 *获取使用前余额
	 */
	public Double getBeforeAmount() {
		return get("beforeAmount");
	}

	/**
	 *设置使用前余额
	 */
	public void setBeforeAmount(Double beforeAmount) {
		set("beforeAmount",beforeAmount);
	}

	/**
	 *获取使用后余额
	 */
	public Double getAfterAmount() {
		return get("afterAmount");
	}

	/**
	 *设置使用后余额
	 */
	public void setAfterAmount(Double afterAmount) {
		set("afterAmount",afterAmount);
	}

	/**
	 *获取使用时间
	 */
	public Date getUseTime() {
		return get("useTime");
	}

	/**
	 *设置使用时间
	 */
	public void setUseTime(Date useTime) {
		set("useTime",useTime);
	}

}
