package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseMemberTypeChange <M extends BaseMemberTypeChange<M>> extends Model<M> implements IBean {


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
	 *获取变更前类型
	 */
	public String getBeforeType() {
		return get("beforeType");
	}

	/**
	 *设置变更前类型
	 */
	public void setBeforeType(String beforeType) {
		set("beforeType",beforeType);
	}

	/**
	 *获取变更后类型
	 */
	public String getAfterType() {
		return get("afterType");
	}

	/**
	 *设置变更后类型
	 */
	public void setAfterType(String afterType) {
		set("afterType",afterType);
	}

	/**
	 *获取变更时间
	 */
	public Date getChangeTime() {
		return get("changeTime");
	}

	/**
	 *设置变更时间
	 */
	public void setChangeTime(Date changeTime) {
		set("changeTime",changeTime);
	}

}