package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseType <M extends BaseType<M>> extends Model<M> implements IBean {


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
	 *获取编码
	 */
	public String getCode() {
		return get("code");
	}

	/**
	 *设置编码
	 */
	public void setCode(String code) {
		set("code",code);
	}

	/**
	 *获取长编号
	 */
	public String getLongCode() {
		return get("longCode");
	}

	/**
	 *设置长编号
	 */
	public void setLongCode(String longCode) {
		set("longCode",longCode);
	}

	/**
	 *获取名称
	 */
	public String getName() {
		return get("name");
	}

	/**
	 *设置名称
	 */
	public void setName(String name) {
		set("name",name);
	}

	/**
	 *获取父级编号
	 */
	public String getParentCode() {
		return get("parentCode");
	}

	/**
	 *设置父级编号
	 */
	public void setParentCode(String parentCode) {
		set("parentCode",parentCode);
	}

	/**
	 *获取排序
	 */
	public Long getSortno() {
		return get("sortno");
	}

	/**
	 *设置排序
	 */
	public void setSortno(Long sortno) {
		set("sortno",sortno);
	}

	/**
	 *获取状态,0：正常，1：禁用
	 */
	public String getStatus() {
		return get("status");
	}

	/**
	 *设置状态,0：正常，1：禁用
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
	 *获取更新时间
	 */
	public Date getUpdateTime() {
		return get("updateTime");
	}

	/**
	 *设置更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		set("updateTime",updateTime);
	}

}
