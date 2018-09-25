package com.block.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import java.util.*;

/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class BaseGoods <M extends BaseGoods<M>> extends Model<M> implements IBean {


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
	 *获取编号
	 */
	public String getCode() {
		return get("code");
	}

	/**
	 *设置编号
	 */
	public void setCode(String code) {
		set("code",code);
	}

	/**
	 *获取分类id
	 */
	public Long getTypeId() {
		return get("typeId");
	}

	/**
	 *设置分类id
	 */
	public void setTypeId(Long typeId) {
		set("typeId",typeId);
	}

	/**
	 *获取所属品牌
	 */
	public Long getBrandId() {
		return get("brandId");
	}

	/**
	 *设置所属品牌
	 */
	public void setBrandId(Long brandId) {
		set("brandId",brandId);
	}

	/**
	 *获取性别，0：男装，1：女装
	 */
	public String getSex() {
		return get("sex");
	}

	/**
	 *设置性别，0：男装，1：女装
	 */
	public void setSex(String sex) {
		set("sex",sex);
	}

	/**
	 *获取季节，0：春装，1：夏装，2：秋装，3：冬装
	 */
	public String getSeason() {
		return get("season");
	}

	/**
	 *设置季节，0：春装，1：夏装，2：秋装，3：冬装
	 */
	public void setSeason(String season) {
		set("season",season);
	}

	/**
	 *获取商品名称
	 */
	public String getName() {
		return get("name");
	}

	/**
	 *设置商品名称
	 */
	public void setName(String name) {
		set("name",name);
	}

	/**
	 *获取图片
	 */
	public String getImg() {
		return get("img");
	}

	/**
	 *设置图片
	 */
	public void setImg(String img) {
		set("img",img);
	}

	/**
	 *获取点击量
	 */
	public Long getClickCnt() {
		return get("clickCnt");
	}

	/**
	 *设置点击量
	 */
	public void setClickCnt(Long clickCnt) {
		set("clickCnt",clickCnt);
	}

	/**
	 *获取状态,0:未上架、1：上架、2：下架
	 */
	public String getStatus() {
		return get("status");
	}

	/**
	 *设置状态,0:未上架、1：上架、2：下架
	 */
	public void setStatus(String status) {
		set("status",status);
	}

	/**
	 *获取排序
	 */
	public Integer getSortno() {
		return get("sortno");
	}

	/**
	 *设置排序
	 */
	public void setSortno(Integer sortno) {
		set("sortno",sortno);
	}

	/**
	 *获取生产地址
	 */
	public String getProductAddress() {
		return get("productAddress");
	}

	/**
	 *设置生产地址
	 */
	public void setProductAddress(String productAddress) {
		set("productAddress",productAddress);
	}

	/**
	 *获取面料
	 */
	public String getMaterial() {
		return get("material");
	}

	/**
	 *设置面料
	 */
	public void setMaterial(String material) {
		set("material",material);
	}

	/**
	 *获取洗涤方式
	 */
	public String getWashWay() {
		return get("washWay");
	}

	/**
	 *设置洗涤方式
	 */
	public void setWashWay(String washWay) {
		set("washWay",washWay);
	}

	/**
	 *获取简介
	 */
	public String getShortIntro() {
		return get("shortIntro");
	}

	/**
	 *设置简介
	 */
	public void setShortIntro(String shortIntro) {
		set("shortIntro",shortIntro);
	}

	/**
	 *获取详细介绍
	 */
	public String getIntro() {
		return get("intro");
	}

	/**
	 *设置详细介绍
	 */
	public void setIntro(String intro) {
		set("intro",intro);
	}

	/**
	 *获取价格json冗余
	 */
	public String getPriceJson() {
		return get("priceJson");
	}

	/**
	 *设置价格json冗余
	 */
	public void setPriceJson(String priceJson) {
		set("priceJson",priceJson);
	}

	/**
	 *获取规格json冗余,例如:{"size":[{"id":1,"name":"XL"},{"id":2,"name":"XXL"}],"color":[{"id":3,"name":"黑色"},{"id":4,"name":"白色"}]}
	 */
	public String getSpecJson() {
		return get("specJson");
	}

	/**
	 *设置规格json冗余,例如:{"size":[{"id":1,"name":"XL"},{"id":2,"name":"XXL"}],"color":[{"id":3,"name":"黑色"},{"id":4,"name":"白色"}]}
	 */
	public void setSpecJson(String specJson) {
		set("specJson",specJson);
	}

	/**
	 *获取是否推荐,0:否，1：是
	 */
	public String getIsRecomment() {
		return get("isRecomment");
	}

	/**
	 *设置是否推荐,0:否，1：是
	 */
	public void setIsRecomment(String isRecomment) {
		set("isRecomment",isRecomment);
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

	/**
	 *获取unit
	 */
	public String getUnit() {
		return get("unit");
	}

	/**
	 *设置unit
	 */
	public void setUnit(String unit) {
		set("unit",unit);
	}

	/**
	 *获取执行标准
	 */
	public String getStandard() {
		return get("standard");
	}

	/**
	 *设置执行标准
	 */
	public void setStandard(String standard) {
		set("standard",standard);
	}

	/**
	 *获取进货入库类型
	 */
	public String getStockType() {
		return get("stockType");
	}

	/**
	 *设置进货入库类型
	 */
	public void setStockType(String stockType) {
		set("stockType",stockType);
	}

	/**
	 *获取贺品季节
	 */
	public String getGoods_Season() {
		return get("Goods_Season");
	}

	/**
	 *设置贺品季节
	 */
	public void setGoods_Season(String Goods_Season) {
		set("Goods_Season",Goods_Season);
	}

	/**
	 *获取浏览次数
	 */
	public Integer getBrowseCnt() {
		return get("browseCnt");
	}

	/**
	 *设置浏览次数
	 */
	public void setBrowseCnt(Integer browseCnt) {
		set("browseCnt",browseCnt);
	}

	/**
	 *获取年
	 */
	public String getGoodsYear() {
		return get("goodsYear");
	}

	/**
	 *设置年
	 */
	public void setGoodsYear(String goodsYear) {
		set("goodsYear",goodsYear);
	}

	/**
	 *获取风格
	 */
	public Long getStyleTypeid() {
		return get("styleTypeid");
	}

	/**
	 *设置风格
	 */
	public void setStyleTypeid(Long styleTypeid) {
		set("styleTypeid",styleTypeid);
	}

	/**
	 *获取是否折扣（0：否，1：未开始，2：折扣中，3：已过期）
	 */
	public String getIsDiscount() {
		return get("isDiscount");
	}

	/**
	 *设置是否折扣（0：否，1：未开始，2：折扣中，3：已过期）
	 */
	public void setIsDiscount(String isDiscount) {
		set("isDiscount",isDiscount);
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
	 *获取折扣开始时间
	 */
	public Date getStartTime() {
		return get("startTime");
	}

	/**
	 *设置折扣开始时间
	 */
	public void setStartTime(Date startTime) {
		set("startTime",startTime);
	}

	/**
	 *获取折扣结束时间
	 */
	public Date getEndTime() {
		return get("endTime");
	}

	/**
	 *设置折扣结束时间
	 */
	public void setEndTime(Date endTime) {
		set("endTime",endTime);
	}

	/**
	 *获取销量
	 */
	public Integer getSoldCnt() {
		return get("soldCnt");
	}

	/**
	 *设置销量
	 */
	public void setSoldCnt(Integer soldCnt) {
		set("soldCnt",soldCnt);
	}

	/**
	 *获取所有规格中最低价
	 */
	public Double getMinPrice() {
		return get("minPrice");
	}

	/**
	 *设置所有规格中最低价
	 */
	public void setMinPrice(Double minPrice) {
		set("minPrice",minPrice);
	}

	/**
	 *获取是否新品,0:否，1:是
	 */
	public String getIsnew() {
		return get("isnew");
	}

	/**
	 *设置是否新品,0:否，1:是
	 */
	public void setIsnew(String isnew) {
		set("isnew",isnew);
	}

}
