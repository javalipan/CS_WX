package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseLevelSetting <M extends BaseLevelSetting<M>> extends Model<M> implements IBean {


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
	 *获取等级图标
	 */
	public String getIcon() {
		return get("icon");
	}

	/**
	 *设置等级图标
	 */
	public void setIcon(String icon) {
		set("icon",icon);
	}

	/**
	 *获取起
	 */
	public Integer getLevelstart() {
		return get("levelstart");
	}

	/**
	 *设置起
	 */
	public void setLevelstart(Integer levelstart) {
		set("levelstart",levelstart);
	}

	/**
	 *获取止
	 */
	public Integer getLevelend() {
		return get("levelend");
	}

	/**
	 *设置止
	 */
	public void setLevelend(Integer levelend) {
		set("levelend",levelend);
	}

	/**
	 *获取等级
	 */
	public Integer getGrade() {
		return get("grade");
	}

	/**
	 *设置等级
	 */
	public void setGrade(Integer grade) {
		set("grade",grade);
	}

	/**
	 *获取等级名称
	 */
	public String getGradename() {
		return get("gradename");
	}

	/**
	 *设置等级名称
	 */
	public void setGradename(String gradename) {
		set("gradename",gradename);
	}

	/**
	 *获取积分获取速度
	 */
	public Float getPointSpeed() {
		return get("pointSpeed");
	}

	/**
	 *设置积分获取速度
	 */
	public void setPointSpeed(Float pointSpeed) {
		set("pointSpeed",pointSpeed);
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

}