package com.block.common.model;

import com.block.common.model.base.BaseOrder;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
/**
 * Generated by JFinal.
*/
@SuppressWarnings("serial")
public class Order extends BaseOrder<Order>{

	public static final Order dao = new Order();

	public String getBrandLabel(Long memberid){
		String sql="select b.name labelName,count(1) cnt from t_order o"
				+" left join t_order_detail od on o.id=od.orderId"
				+" left join t_goods_detail gd on gd.id=od.goodsDetailId"
				+" left join t_goods g on g.id=gd.goodsId"
				+" left join t_brand b on b.id=g.brandId"
				+" where o.memberId=? group by b.id order by cnt DESC,o.ordertime desc limit 0,1";
		Record record=Db.findFirst(sql,memberid);
		return record.getStr("labelName");
	}
	
	public String getStyleLabel(Long memberid){
		String sql="select s.name labelName,count(1) cnt from t_order o"
				+" left join t_order_detail od on o.id=od.orderId"
				+" left join t_goods_detail gd on gd.id=od.goodsDetailId"
				+" left join t_goods g on g.id=gd.goodsId"
				+" left join t_style s on s.id=g.styleTypeid"
				+" where o.memberId=? group by s.id order by cnt DESC,o.ordertime desc limit 0,1";
		Record record=Db.findFirst(sql,memberid);
		return record.getStr("labelName");
	}
	
	public String getSizeLabel(Long memberid){
		String sql="select s.specValue labelName,count(1) cnt from t_order o"
				+" left join t_order_detail od on o.id=od.orderId"
				+" left join t_goods_detail gd on gd.id=od.goodsDetailId"
				+" left join t_spec s on s.id=gd.sizeId"
				+" where o.memberId=? group by s.id order by cnt DESC,o.ordertime desc limit 0,1";
		Record record=Db.findFirst(sql,memberid);
		return record.getStr("labelName");
	}
	
	public Long getOrderCnt(Long memberid){
		String sql="select count(1) cnt from t_order o where o.memberId=?";
		Record record=Db.findFirst(sql,memberid);
		return record.getLong("cnt");
	}
}
