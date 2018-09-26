package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseMemberCoupon <M extends BaseMemberCoupon<M>> extends Model<M> implements IBean {


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
	 *获取优惠券名称
	 */
	public String getName() {
		return get("name");
	}

	/**
	 *设置优惠券名称
	 */
	public void setName(String name) {
		set("name",name);
	}

	/**
	 *获取副标题
	 */
	public String getSubtitle() {
		return get("subtitle");
	}

	/**
	 *设置副标题
	 */
	public void setSubtitle(String subtitle) {
		set("subtitle",subtitle);
	}

	/**
	 *获取描述
	 */
	public String getRemark() {
		return get("remark");
	}

	/**
	 *设置描述
	 */
	public void setRemark(String remark) {
		set("remark",remark);
	}

	/**
	 *获取优惠券图标
	 */
	public String getLogoUrl() {
		return get("logoUrl");
	}

	/**
	 *设置优惠券图标
	 */
	public void setLogoUrl(String logoUrl) {
		set("logoUrl",logoUrl);
	}

	/**
	 *获取优惠券编号
	 */
	public String getCode() {
		return get("code");
	}

	/**
	 *设置优惠券编号
	 */
	public void setCode(String code) {
		set("code",code);
	}

	/**
	 *获取优惠券id
	 */
	public Long getCouponId() {
		return get("couponId");
	}

	/**
	 *设置优惠券id
	 */
	public void setCouponId(Long couponId) {
		set("couponId",couponId);
	}

	/**
	 *获取面值
	 */
	public Integer getMoney() {
		return get("money");
	}

	/**
	 *设置面值
	 */
	public void setMoney(Integer money) {
		set("money",money);
	}

	/**
	 *获取有效期起
	 */
	public Date getStartTime() {
		return get("startTime");
	}

	/**
	 *设置有效期起
	 */
	public void setStartTime(Date startTime) {
		set("startTime",startTime);
	}

	/**
	 *获取有效期止
	 */
	public Date getEndTime() {
		return get("endTime");
	}

	/**
	 *设置有效期止
	 */
	public void setEndTime(Date endTime) {
		set("endTime",endTime);
	}

	/**
	 *获取状态,0:未使用，1：已使用
	 */
	public String getStatus() {
		return get("status");
	}

	/**
	 *设置状态,0:未使用，1：已使用
	 */
	public void setStatus(String status) {
		set("status",status);
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

	/**
	 *获取使用订单
	 */
	public Long getOrderId() {
		return get("orderId");
	}

	/**
	 *设置使用订单
	 */
	public void setOrderId(Long orderId) {
		set("orderId",orderId);
	}

	/**
	 *获取金额制限
	 */
	public Integer getLimitmoney() {
		return get("limitmoney");
	}

	/**
	 *设置金额制限
	 */
	public void setLimitmoney(Integer limitmoney) {
		set("limitmoney",limitmoney);
	}

}
