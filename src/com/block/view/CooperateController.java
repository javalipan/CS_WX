package com.block.view;

import java.util.List;

import com.block.common.model.Cooperate;
import com.block.common.model.Goods;

public class CooperateController extends BaseController {

	public void cooperateList(){
		List<Cooperate> cooperates=Cooperate.dao.find("select * from t_cooperate order by createtime desc");
		setAttr("cooperates", cooperates);
		renderVelocity("goods/cooperate.vm");
	}

	public void cooperateDetail(){
		Long id=getParaToLong("id");
		Cooperate cooperate=Cooperate.dao.findById(id);
		setAttr("cooperate",cooperate);
		List<Goods> goods=Goods.dao.find("select g.* from t_cooperate_goods cg left join t_goods g on g.id=cg.goodsid where g.status=1 and cg.cooperateid=? order by g.sortno asc",cooperate.getId());
		setAttr("goods", goods);
		renderVelocity("goods/cooperateDetail.vm");
	}
}
