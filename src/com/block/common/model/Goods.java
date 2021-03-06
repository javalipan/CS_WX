package com.block.common.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.block.common.model.base.BaseGoods;
/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class Goods extends BaseGoods<Goods>{
	
	private String typeids;

	public static final Goods dao = new Goods();
	
	public static String basecolumn=" g.id, g.code, g.typeId, g.brandId, g.sex, g.season, g.name, g.img, g.clickCnt, g.status, g.sortno, g.productAddress,material, g.washWay, g.shortIntro, g.priceJson, g.specJson, g.isRecomment, g.createTime, g.updateTime,unit, g.standard, g.stockType, g.Goods_Season, g.browseCnt, g.goodsYear, g.styleTypeid, g.isDiscount,discount, g.startTime, g.endTime, g.soldCnt, g.minPrice ";
	
	/**
	 * 获取推荐
	 * @param memberid :会员id
	 * @param count :条数
	 * @return
	 */
	public List<Goods> getRecomend(Long memberid,int count){
		List<Goods> goods=null;
		if(memberid!=null&&memberid>0){
			
			String typesql=" select id from t_style style where style.parentId in("
					+" select g.styleTypeid from t_order o "
					+" left join t_order_detail od on o.id=od.orderId "
					+" left join t_goods_detail gd on gd.id=od.goodsDetailId "
					+" left join t_goods g on g.id=gd.goodsId"
					+" where g.status='1' and o.memberid=?"
					+" union all"
					+" select s.parentId from t_style s where s.id in("
					+" select g.styleTypeid from t_goods_browseHis bh left join t_goods g on g.id=bh.goodsId where g.status='1' and bh.memberid=?))";
			List<Goods> types = dao.find(typesql,memberid,memberid);
			String typelist="";
			for(int i=0;i<types.size();i++){
				long id=types.get(i).getLong("id");
				typelist+=(i==0?"":",")+id;
			}
			
			String sql="select distinct * from(select "+basecolumn+",b.name brandname from t_goods g left join t_brand b on b.id=g.brandid where g.status='1' and g.styleTypeid in(?) "
					+" union all "
					+" select "+basecolumn+",b.name brandname from t_goods g left join t_brand b on b.id=g.brandid where g.status='1' and g.isRecomment='1')t "
					+" limit 0,?";
			goods=dao.find(sql,typelist,count);
		}
		if(memberid==null||goods.size()==0){
			goods=dao.find("select "+basecolumn+",(select sum(d.amount) from t_goods_detail d where d.goodsid=g.id and d.status=1) amount,b.name brandname from t_goods g left join t_brand b on b.id=g.brandid where g.status='1' group by soldcnt desc limit 0,?",count);
		}
		return goods;
	}
	
	public List<Goods> searchGoods(Goods goods,String orderby,Integer from,Integer count){
		String sql="select "+basecolumn+",(select sum(d.amount) from t_goods_detail d where d.goodsid=g.id and d.status=1) ,b.name brandname from t_goods g left join t_brand b on b.id=g.brandid  "
				+" where 1=1";
		List<Object> params=new ArrayList<Object>();
		if(goods.getBrandId()!=null&&goods.getBrandId()!=0){
			sql+=" and g.brandid=?";
			params.add(goods.getBrandId());
		}
		if(StringUtils.isNotBlank(goods.getStatus())){
			sql+=" and g.status=?";
			params.add(goods.getStatus());
		}
		if(StringUtils.isNotBlank(goods.getIsRecomment())){
			sql+=" and g.isRecomment=?";
			params.add(goods.getIsRecomment());
		}
		if(goods.getTypeId()!=null&&goods.getTypeId()!=0){
			sql+=" and g.typeid=?";
			params.add(goods.getTypeId());
		}
		if(StringUtils.isNotBlank(goods.getName())){
			sql+=" and g.name like ?";
			params.add("%"+goods.getName()+"%");
		}
		if(goods.getStyleTypeid()!=null&&goods.getStyleTypeid()!=0){
			sql+=" and g.styletypeid=?";
			params.add(goods.getStyleTypeid());
		}
		if(StringUtils.isNotBlank(goods.getIsDiscount())){
			sql+=" and g.isdiscount=?";
			params.add(goods.getIsDiscount());
		}
		if(StringUtils.isNotBlank(goods.getIsnew())){
			sql+=" and g.isnew=?";
			params.add(goods.getIsnew());
		}
		if(StringUtils.isNotBlank(goods.getTypeids())){
			sql+=" and g.typeid in (?)";
			params.add(goods.getIsnew());
		}
		if(StringUtils.isNotBlank(orderby)){
			sql+=" order by "+orderby;
		}
		if(from!=null&&count!=null){
			sql+=" limit ?,?";
			params.add(from);
			params.add(count);
		}
		Object[] paramArray=new Object[params.size()];
		for(int i=0;i<params.size();i++){
			paramArray[i]=params.get(i);
		}
		return dao.find(sql,paramArray);
	}

	public JSONObject getPriceJsonObj(){
		String json=this.getPriceJson();
		return JSONObject.parseObject(json);
	}
	
	public JSONObject getSpecJsonObj(){
		String json=this.getSpecJson();
		return JSONObject.parseObject(json);
	}

	public String getTypeids() {
		return typeids;
	}

	public void setTypeids(String typeids) {
		this.typeids = typeids;
	}
	
}
