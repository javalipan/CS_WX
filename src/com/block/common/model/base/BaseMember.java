package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseMember <M extends BaseMember<M>> extends Model<M> implements IBean {


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
	 *获取openid
	 */
	public String getOpenid() {
		return get("openid");
	}

	/**
	 *设置openid
	 */
	public void setOpenid(String openid) {
		set("openid",openid);
	}

	/**
	 *获取Unionid
	 */
	public String getUnionid() {
		return get("Unionid");
	}

	/**
	 *设置Unionid
	 */
	public void setUnionid(String Unionid) {
		set("Unionid",Unionid);
	}

	/**
	 *获取会员卡编号
	 */
	public String getMemberCode() {
		return get("memberCode");
	}

	/**
	 *设置会员卡编号
	 */
	public void setMemberCode(String memberCode) {
		set("memberCode",memberCode);
	}

	/**
	 *获取会员昵称
	 */
	public String getNickname() {
		return get("nickname");
	}

	/**
	 *设置会员昵称
	 */
	public void setNickname(String nickname) {
		set("nickname",nickname);
	}

	/**
	 *获取头像
	 */
	public String getHeadUrl() {
		return get("headUrl");
	}

	/**
	 *设置头像
	 */
	public void setHeadUrl(String headUrl) {
		set("headUrl",headUrl);
	}

	/**
	 *获取姓名
	 */
	public String getName() {
		return get("name");
	}

	/**
	 *设置姓名
	 */
	public void setName(String name) {
		set("name",name);
	}

	/**
	 *获取性别，0：男，1：女
	 */
	public String getSex() {
		return get("sex");
	}

	/**
	 *设置性别，0：男，1：女
	 */
	public void setSex(String sex) {
		set("sex",sex);
	}

	/**
	 *获取出生日期
	 */
	public Date getBirthday() {
		return get("birthday");
	}

	/**
	 *设置出生日期
	 */
	public void setBirthday(Date birthday) {
		set("birthday",birthday);
	}

	/**
	 *获取联系电话
	 */
	public String getPhone() {
		return get("phone");
	}

	/**
	 *设置联系电话
	 */
	public void setPhone(String phone) {
		set("phone",phone);
	}

	/**
	 *获取地址id
	 */
	public Integer getAddressId() {
		return get("addressId");
	}

	/**
	 *设置地址id
	 */
	public void setAddressId(Integer addressId) {
		set("addressId",addressId);
	}

	/**
	 *获取详细地址
	 */
	public String getDetailAddress() {
		return get("detailAddress");
	}

	/**
	 *设置详细地址
	 */
	public void setDetailAddress(String detailAddress) {
		set("detailAddress",detailAddress);
	}

	/**
	 *获取是否已完善信息
	 */
	public String getIsfinish() {
		return get("isfinish");
	}

	/**
	 *设置是否已完善信息
	 */
	public void setIsfinish(String isfinish) {
		set("isfinish",isfinish);
	}

	/**
	 *获取总消费金额
	 */
	public Double getTotalmoney() {
		return get("totalmoney");
	}

	/**
	 *设置总消费金额
	 */
	public void setTotalmoney(Double totalmoney) {
		set("totalmoney",totalmoney);
	}

	/**
	 *获取会员积分
	 */
	public Integer getMemberPoint() {
		return get("memberPoint");
	}

	/**
	 *设置会员积分
	 */
	public void setMemberPoint(Integer memberPoint) {
		set("memberPoint",memberPoint);
	}

	/**
	 *获取会员等级
	 */
	public Integer getMemberLevel() {
		return get("memberLevel");
	}

	/**
	 *设置会员等级
	 */
	public void setMemberLevel(Integer memberLevel) {
		set("memberLevel",memberLevel);
	}

	/**
	 *获取是否接收微信推送,0：是，1：否
	 */
	public String getIsRecWx() {
		return get("isRecWx");
	}

	/**
	 *设置是否接收微信推送,0：是，1：否
	 */
	public void setIsRecWx(String isRecWx) {
		set("isRecWx",isRecWx);
	}

	/**
	 *获取是否接收短信推送,0：是，1：否
	 */
	public String getIsRecMsg() {
		return get("isRecMsg");
	}

	/**
	 *设置是否接收短信推送,0：是，1：否
	 */
	public void setIsRecMsg(String isRecMsg) {
		set("isRecMsg",isRecMsg);
	}

	/**
	 *获取推广来源
	 */
	public Long getFromUser() {
		return get("fromUser");
	}

	/**
	 *设置推广来源
	 */
	public void setFromUser(Long fromUser) {
		set("fromUser",fromUser);
	}

	/**
	 *获取状态，0：关注，1：取消关注，2：禁用
	 */
	public String getStatus() {
		return get("status");
	}

	/**
	 *设置状态，0：关注，1：取消关注，2：禁用
	 */
	public void setStatus(String status) {
		set("status",status);
	}

	/**
	 *获取注册时间
	 */
	public Date getRegisterTime() {
		return get("registerTime");
	}

	/**
	 *设置注册时间
	 */
	public void setRegisterTime(Date registerTime) {
		set("registerTime",registerTime);
	}

	/**
	 *获取取消关注时间
	 */
	public Date getCancelTime() {
		return get("cancelTime");
	}

	/**
	 *设置取消关注时间
	 */
	public void setCancelTime(Date cancelTime) {
		set("cancelTime",cancelTime);
	}

	/**
	 *获取余额
	 */
	public Double getBalance() {
		return get("balance");
	}

	/**
	 *设置余额
	 */
	public void setBalance(Double balance) {
		set("balance",balance);
	}

	/**
	 *获取折扣
	 */
	public Double getDiscount() {
		return get("discount");
	}

	/**
	 *设置折扣
	 */
	public void setDiscount(Double discount) {
		set("discount",discount);
	}

	/**
	 *获取新老顾客,1:新顾客，2：老顾客
	 */
	public String getOldornew() {
		return get("oldornew");
	}

	/**
	 *设置新老顾客,1:新顾客，2：老顾客
	 */
	public void setOldornew(String oldornew) {
		set("oldornew",oldornew);
	}

	/**
	 *获取最后购买日期
	 */
	public Date getLastBuyTime() {
		return get("lastBuyTime");
	}

	/**
	 *设置最后购买日期
	 */
	public void setLastBuyTime(Date lastBuyTime) {
		set("lastBuyTime",lastBuyTime);
	}

	/**
	 *获取会员等级变动日期
	 */
	public Date getLevelChangeTime() {
		return get("levelChangeTime");
	}

	/**
	 *设置会员等级变动日期
	 */
	public void setLevelChangeTime(Date levelChangeTime) {
		set("levelChangeTime",levelChangeTime);
	}

	/**
	 *获取会员类型，R:注册,N:新顾客,E:活跃顾客,S1:瞌睡顾客,S2:半睡顾客,S3:沉睡顾客,X1:唤醒瞌睡,X2:唤醒半睡,X3:唤醒沉睡
	 */
	public String getMemberType() {
		return get("memberType");
	}

	/**
	 *设置会员类型，R:注册,N:新顾客,E:活跃顾客,S1:瞌睡顾客,S2:半睡顾客,S3:沉睡顾客,X1:唤醒瞌睡,X2:唤醒半睡,X3:唤醒沉睡
	 */
	public void setMemberType(String memberType) {
		set("memberType",memberType);
	}

}
