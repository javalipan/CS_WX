package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseCoupon <M extends BaseCoupon<M>> extends Model<M> implements IBean {


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
	 *获取类型,0:用户兑换，1:现金购买，2:系统发放
	 */
	public String getType() {
		return get("type");
	}

	/**
	 *设置类型,0:用户兑换，1:现金购买，2:系统发放
	 */
	public void setType(String type) {
		set("type",type);
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
	 *获取数量
	 */
	public Integer getAmount() {
		return get("amount");
	}

	/**
	 *设置数量
	 */
	public void setAmount(Integer amount) {
		set("amount",amount);
	}

	/**
	 *获取剩余数量
	 */
	public Integer getLeftAmount() {
		return get("leftAmount");
	}

	/**
	 *设置剩余数量
	 */
	public void setLeftAmount(Integer leftAmount) {
		set("leftAmount",leftAmount);
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
	 *获取价格
	 */
	public Double getPrice() {
		return get("price");
	}

	/**
	 *设置价格
	 */
	public void setPrice(Double price) {
		set("price",price);
	}

	/**
	 *获取兑换所需积分
	 */
	public Integer getIntegral() {
		return get("integral");
	}

	/**
	 *设置兑换所需积分
	 */
	public void setIntegral(Integer integral) {
		set("integral",integral);
	}

	/**
	 *获取有效期类型，0：固定日期，1：天数
	 */
	public String getTimeType() {
		return get("timeType");
	}

	/**
	 *设置有效期类型，0：固定日期，1：天数
	 */
	public void setTimeType(String timeType) {
		set("timeType",timeType);
	}

	/**
	 *获取有效期天数
	 */
	public Integer getDays() {
		return get("days");
	}

	/**
	 *设置有效期天数
	 */
	public void setDays(Integer days) {
		set("days",days);
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
	 *获取兑换上限
	 */
	public Integer getMaxGet() {
		return get("maxGet");
	}

	/**
	 *设置兑换上限
	 */
	public void setMaxGet(Integer maxGet) {
		set("maxGet",maxGet);
	}

	/**
	 *获取兑换会员级别起点
	 */
	public Integer getGetGrade() {
		return get("getGrade");
	}

	/**
	 *设置兑换会员级别起点
	 */
	public void setGetGrade(Integer getGrade) {
		set("getGrade",getGrade);
	}

	/**
	 *获取状态,0:正常、1：禁用
	 */
	public String getStatus() {
		return get("status");
	}

	/**
	 *设置状态,0:正常、1：禁用
	 */
	public void setStatus(String status) {
		set("status",status);
	}

	/**
	 *获取创建时间
	 */
	public Date getCreateTime() {
		return get("createTime");
	}

	/**
	 *设置创建时间
	 */
	public void setCreateTime(Date createTime) {
		set("createTime",createTime);
	}

	/**
	 *获取创建人
	 */
	public String getCreateUser() {
		return get("createUser");
	}

	/**
	 *设置创建人
	 */
	public void setCreateUser(String createUser) {
		set("createUser",createUser);
	}

}