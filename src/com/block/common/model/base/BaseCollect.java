package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseCollect <M extends BaseCollect<M>> extends Model<M> implements IBean {


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
	 *获取商品id
	 */
	public Long getGoodsId() {
		return get("goodsId");
	}

	/**
	 *设置商品id
	 */
	public void setGoodsId(Long goodsId) {
		set("goodsId",goodsId);
	}

	/**
	 *获取收藏时间
	 */
	public Date getCreateTime() {
		return get("createTime");
	}

	/**
	 *设置收藏时间
	 */
	public void setCreateTime(Date createTime) {
		set("createTime",createTime);
	}

}
