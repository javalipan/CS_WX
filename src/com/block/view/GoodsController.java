package com.block.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.block.common.CommonConstant;
import com.block.common.model.Brand;
import com.block.common.model.Collect;
import com.block.common.model.Goods;
import com.block.common.model.GoodsBrowseHis;
import com.block.common.model.GoodsDetail;
import com.block.common.model.Imgs;
import com.block.common.model.Member;
import com.block.common.model.Type;
import com.block.utils.IpUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;

public class GoodsController extends BaseController{
	
	/**
	 * 搜索
	 */
	public void searchGoods(){
		Member member=(Member) getSession().getAttribute(SESSION_MEMBER);
		
		String orderBy=getPara("orderBy");
		if(StringUtils.isBlank(orderBy)){
			orderBy="sortno asc";
		}
		setAttr("orderBy", orderBy);
		
		String searchTxt=getPara("searchTxt");
		setAttr("searchTxt", searchTxt);
		Goods goods=new Goods();
		goods.setName(searchTxt);
		goods.setStatus("1");
		List<Goods> goodsList=Goods.dao.searchGoods(goods, orderBy, 0, 20);
		if(goodsList==null||goodsList.size()==0){		//未搜索到结果
			goodsList=Goods.dao.getRecomend(member==null?null:member.getId(), 10);
			setAttr("goodsList", goodsList);
			setAttr("resultCnt", 0);
			renderVelocity("goods/searchGoods.vm");
		}
		else{
			setAttr("goodsList", goodsList);
			setAttr("resultCnt", goodsList.size());
			renderVelocity("goods/searchGoods.vm");
		}
	}
	
	/**
	 * 分类列表
	 */
	public void typeList(){
		String code=getPara("code");
		List<Type> parents=Type.dao.find("select * from t_type where code in(select left(t.longcode, LOCATE('-', t.longcode)-1)code from (select t.*,(select count(1) from t_goods g where g.typeid=t.code and g.status=1)cnt from t_type t)t where t.cnt>0)");
		if(StringUtils.isBlank(code)){
			code=parents.get(0).getCode();
		}
		List<Type> types=Type.dao.find("select t.* from (select t.*,(select count(1) from t_goods g where g.typeid=t.code and g.status=1)cnt from t_type t)t where t.cnt>0 and longCode like '"+code+"-%'");
		if(types!=null&&types.size()>0){
			List<String> l=new ArrayList<String>();
			String typeids="";
			for(Type t:types){
				String array[]=t.getLongCode().split("-");
				if(array.length>2){
					if(!l.contains(array[1])){
						typeids+="'"+array[1]+"',";
					}
					l.add(array[1]);
				}
			}
			if(StringUtils.isNotBlank(typeids)){
				typeids=typeids.substring(0,typeids.length()-1);
				List<Type> children=Type.dao.find("select * from t_type where code in ("+typeids+")");
				for(Type type:children){
					List<Type> children_t=new ArrayList<Type>();
					for(Type t:types){
						if(t.getParentCode().equals(type.getCode())){
							children_t.add(t);
						}
					}
					type.put("children", children_t);
				}
				setAttr("children", children);
			}
		}
		
		
		setAttr("code", code);
		setAttr("parents", parents);
		renderVelocity("goods/typeList.vm");
	}
	
	/**
	 * 分类下商品列表
	 */
	public void typeGoodsList(){
		String parentCode=getPara("parentCode");
		if(StringUtils.isNotBlank(parentCode)){
			Type t=new Type();
			t.setName("全部");
			setAttr("type", t);
			
			List<Type> types=Type.dao.find("select * from t_type where parentCode =?",parentCode);
			setAttr("types", types);
			String orderBy=getPara("orderBy");
			if(StringUtils.isBlank(orderBy)){
				orderBy="sortno asc";
			}
			setAttr("orderBy", orderBy);
			String sql="select "+Goods.basecolumn+",(select sum(d.amount) from t_goods_detail d where d.goodsid=g.id and d.status=1) amount "
					+" from t_goods g where 1=1 and status=1 and typeId in (";
			for(Type type:types){
				sql+="'"+type.getCode()+"',";
			}
			sql=sql.substring(0, sql.length()-1);
			sql+=") order by "+orderBy;
			List<Goods> goodsList=Goods.dao.find(sql);
			setAttr("goodsList", goodsList);
			setAttr("parentCode", parentCode);
		}
		else{
			Long id=getParaToLong("id");
			Type type=Type.dao.findById(id);
			setAttr("type", type);
			setAttr("parentCode", type.getParentCode());
			
			List<Type> types=Type.dao.find("select * from t_type where parentCode =?",type.getParentCode());
			setAttr("types", types);
			String orderBy=getPara("orderBy");
			if(StringUtils.isBlank(orderBy)){
				orderBy="sortno asc";
			}
			setAttr("orderBy", orderBy);
			Goods goods=new Goods();
			goods.setTypeId(Long.parseLong(type.getCode()));
			goods.setStatus("1");
			List<Goods> goodsList=Goods.dao.searchGoods(goods, orderBy, null, null);
			setAttr("goodsList", goodsList);
		}
		
		renderVelocity("goods/typeGoodsList.vm");
	}
	
	public void ajaxGetGoods(){
		String orderBy=getPara("orderBy");
		if(StringUtils.isBlank(orderBy)){
			orderBy="sortno asc";
		}
		Goods goods=new Goods();
		goods.setStatus("1");
		Long typeid=getParaToLong("typeid");
		if(typeid!=null){
			goods.setTypeId(typeid);
		}
		Long brandId=getParaToLong("brandId");
		if(brandId!=null){
			goods.setBrandId(brandId);
		}
		String typeids=getPara("typeids");
		if(StringUtils.isNotBlank(typeids)){
			goods.setTypeids(typeids);
		}
		Integer from=getParaToInt("from");
		Integer count=getParaToInt("count");
		List<Goods> goodsList=Goods.dao.searchGoods(goods, orderBy, from, count);
		renderJson(goodsList);
	}

	public void brandList(){
		List<Brand> brands=Brand.dao.find("select id, type, name, intro, img, status, isRecomment, sortno, createTime, updateTime from t_brand where status=0 and type='0' order by sortno asc");
		setAttr("brands", brands);
		
		if(getRequest().getHeader("user-agent").toLowerCase().contains("micromessenger")){
			JsTicket jsTicket=JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
			String url = getRequest().getScheme() + "://" + getRequest().getServerName() + getRequest().getRequestURI();
			if (getRequest().getQueryString() != null) {
				url += "?" + getRequest().getQueryString();
			}
			Map<String, String> jsparas=jsSign(jsTicket.getTicket(), url);
			jsparas.put("jsapi_ticket", jsTicket.getTicket());
			jsparas.put("appId", ApiConfigKit.getApiConfig().getAppId());
			String title  = "尺尚设计师集合";
			String link = CommonConstant.WEIXIN_DOMAIN+"goods/brandList";
			String imgUrl=CommonConstant.WEIXIN_DOMAIN+"images/logo2.png";
			String desc = "尺尚设计师集合";
			jsparas.put("title", title);
			jsparas.put("link", link);
			jsparas.put("imgUrl", imgUrl);
			jsparas.put("desc", desc);
			setAttr("jsparas", jsparas);
		}
		
		renderVelocity("goods/brandList.vm");
	}
	
	/**
	 * 品牌商品列表
	 */
	public void brandGoodsList(){
		Long id=getParaToLong("id");
		String orderBy=getPara("orderBy");
		
		Brand brand=Brand.dao.findById(id);
		setAttr("brand", brand);
		
		List<Imgs> brandimgs=Imgs.dao.find("select * from t_imgs where type=0 and receiptID=? limit 0,2",brand.getId());
		if(brandimgs.size()==1){
			setAttr("brandimg", brandimgs.get(0));
		}
		else if(brandimgs.size()==2){
			setAttr("brandimg", brandimgs.get(1));
		}
		if(StringUtils.isBlank(orderBy)){
			orderBy="sortno asc";
		}
		
		Goods goods=new Goods();
		goods.setStatus("1");
		goods.setBrandId(brand.getId());
		List<Goods> goodsList=Goods.dao.searchGoods(goods, orderBy, null, null);
		
//		List<Goods> goods=Goods.dao.find("select id, code, typeId, brandId, sex, season, name, img, clickCnt, status, sortno, productAddress, material, washWay, shortIntro, priceJson, specJson, isRecomment, createTime, updateTime,(select sum(d.amount) from t_goods_detail d where  d.goodsid=g.id and d.status=1) amount "+
//		" from t_goods g where brandid=? and status=1 order by sortno asc",brand.getId());
		setAttr("goodsList", goodsList);
		setAttr("orderBy", orderBy);
		
		if(getRequest().getHeader("user-agent").toLowerCase().contains("micromessenger")){
			JsTicket jsTicket=JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
			String url = getRequest().getScheme() + "://" + getRequest().getServerName() + getRequest().getRequestURI();
			if (getRequest().getQueryString() != null) {
				url += "?" + getRequest().getQueryString();
			}
			Map<String, String> jsparas=jsSign(jsTicket.getTicket(), url);
			jsparas.put("jsapi_ticket", jsTicket.getTicket());
			jsparas.put("appId", ApiConfigKit.getApiConfig().getAppId());
			String title  = "尺尚-"+brand.getName();
			String link = CommonConstant.WEIXIN_DOMAIN+"goods/brandGoodsList?id="+id;
			String imgUrl=null;
			if(brandimgs.size()>0){
				if(brandimgs.size()==1){
					imgUrl = CommonConstant.FILE_DOMAIN+brandimgs.get(0).getPath();
				}
				else{
					imgUrl = CommonConstant.FILE_DOMAIN+brandimgs.get(1).getPath();
				}
			}
			else{
				imgUrl=CommonConstant.WEIXIN_DOMAIN+"images/qrcode.png";
			}
			String desc = "尺尚设计师集合";
			jsparas.put("title", title);
			jsparas.put("link", link);
			jsparas.put("imgUrl", imgUrl);
			jsparas.put("desc", desc);
			setAttr("jsparas", jsparas);
		}
		
		renderVelocity("goods/brandGoodsList.vm");
	}
	
	public void brandDetail(){
		Long id=getParaToLong("id");
		Brand brand=Brand.dao.findById(id);
		setAttr("brand", brand);
		
		List<Imgs> brandimgs=Imgs.dao.find("select * from t_imgs where type=0 and receiptID=? limit 0,2",brand.getId());
		if(brandimgs.size()==1){
			setAttr("brandimg", brandimgs.get(0));
		}
		else if(brandimgs.size()==2){
			setAttr("brandimg", brandimgs.get(1));
		}
		
		renderVelocity("goods/brandDetail.vm");
	}
	
	/**
	 * 商品详情
	 */
	public void goodsDetail(){
		Long id=getParaToLong("id");
		Member member=(Member) getSession().getAttribute(SESSION_MEMBER);
		Collect collect=null;
		if(member==null){
			String ua = getRequest().getHeader("user-agent").toLowerCase();
			if(ua.indexOf("micromessenger") > 0){// 是微信浏览器
				member=getMember();
			}
		}
		if(member!=null&&member.getId()!=null){
			collect=Collect.dao.findFirst("select * from t_collect where memberid=? and goodsid=?",member.getId(),id);
		}
		if(collect!=null){
			setAttr("collected", true);
		}
		if(member==null||member.getNickname()==null||member.getHeadUrl()==null){
			setAttr("style", "display:none");
		}
		else{
			GoodsBrowseHis goodsBrowseHis=new GoodsBrowseHis();
			goodsBrowseHis.setBrowseTime(new Date());
			goodsBrowseHis.setGoodsId(id);
			goodsBrowseHis.setIpAddr(IpUtils.getIpAddr(getRequest()).split(",")[0]);
			goodsBrowseHis.setMemberId(member.getId());
			goodsBrowseHis.save();
			
			Db.update("update t_goods set browseCnt=browseCnt+1");
		}
		
		Goods goods=Goods.dao.findById(id);
		setAttr("goods", goods);
		Brand brand=Brand.dao.findById(goods.getBrandId());
		setAttr("brand", brand);
		
		List<Imgs> goodsImgs=Imgs.dao.find("select * from t_imgs where type=1 and receiptID=?",id);
		setAttr("goodsImgs", goodsImgs);
		
		List<GoodsDetail> goodsDetails=GoodsDetail.dao.find("select d.*,s1.specvalue colorName,s2.specvalue sizeName from t_goods_detail d left join t_spec s1 on s1.id=d.colorid left join t_spec s2 on s2.id=d.sizeid where d.status=1 and d.goodsid=?",id);
		JSONArray jsonArray=new JSONArray();
		for(GoodsDetail gd:goodsDetails){
			JSONObject obj=JSONObject.parseObject(JSONObject.toJSONString(gd));
			obj.put("sizeName", gd.get("sizeName"));
			obj.put("colorName", gd.get("colorName"));
			jsonArray.add(obj);
		}
		setAttr("detailJson", jsonArray);
		
		if(!"1".equals(goods.getStatus())){
			setAttr("downstatus", "true");
		}
		
		if(getRequest().getHeader("user-agent").toLowerCase().contains("micromessenger")){
			JsTicket jsTicket=JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
			String url = getRequest().getScheme() + "://" + getRequest().getServerName() + getRequest().getRequestURI();
			if (getRequest().getQueryString() != null) {
				url += "?" + getRequest().getQueryString();
			}
			Map<String, String> jsparas=jsSign(jsTicket.getTicket(), url);
			jsparas.put("jsapi_ticket", jsTicket.getTicket());
			jsparas.put("appId", ApiConfigKit.getApiConfig().getAppId());
			String title  = "尺尚-"+goods.getName();
			String link = CommonConstant.WEIXIN_DOMAIN+"goods/goodsDetail?id="+id;
			String imgUrl=null;
			if(goodsImgs.size()>0){
				imgUrl = CommonConstant.FILE_DOMAIN+goodsImgs.get(0).getPath();
			}
			else{
				imgUrl=CommonConstant.WEIXIN_DOMAIN+"images/qrcode.png";
			}
			String desc = "尺尚设计师集合";
			jsparas.put("title", title);
			jsparas.put("link", link);
			jsparas.put("imgUrl", imgUrl);
			jsparas.put("desc", desc);
			setAttr("jsparas", jsparas);
		}
		
		renderVelocity("goods/goods.vm");
	}
	
	/**
	 * 收藏商品
	 */
	public void collectGoods(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Long id=getParaToLong("id");
		Collect collect=new Collect();
		collect.setCreateTime(new Date());
		collect.setGoodsId(id);
		collect.setMemberId(member.getId());
		collect.save();
		renderJson("ok",true);
	}
	
	/**
	 * 移除收藏
	 */
	public void removeCollect(){
		Long id=getParaToLong("id");
		if(Collect.dao.deleteById(id)){
			renderJson("ok",true);
		}
		else{
			renderJson("ok",false);
		}
	}
}
