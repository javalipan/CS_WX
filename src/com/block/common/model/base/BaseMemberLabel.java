package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseMemberLabel <M extends BaseMemberLabel<M>> extends Model<M> implements IBean {


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
	 *获取类型编码
	 */
	public String getTypeCode() {
		return get("typeCode");
	}

	/**
	 *设置类型编码
	 */
	public void setTypeCode(String typeCode) {
		set("typeCode",typeCode);
	}

	/**
	 *获取类型名称
	 */
	public String getTypeName() {
		return get("typeName");
	}

	/**
	 *设置类型名称
	 */
	public void setTypeName(String typeName) {
		set("typeName",typeName);
	}

	/**
	 *获取标签名称
	 */
	public String getLabelName() {
		return get("labelName");
	}

	/**
	 *设置标签名称
	 */
	public void setLabelName(String labelName) {
		set("labelName",labelName);
	}

	/**
	 *获取是否系统预置,0:否，1：是
	 */
	public String getIsSystem() {
		return get("isSystem");
	}

	/**
	 *设置是否系统预置,0:否，1：是
	 */
	public void setIsSystem(String isSystem) {
		set("isSystem",isSystem);
	}

}