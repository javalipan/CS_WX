package com.block.view;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.block.common.model.Member;
import com.block.common.model.MemberAddress;
import com.block.common.model.Region;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
//地址管理
public class AddressController extends BaseController{

	public void initAddressList(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		
		List<Region> regions=Region.dao.find("select * from t_region where SuperiorCode=?", 100000);		//查询中国所有省会
		setAttr("provinces", regions);
		
		List<MemberAddress> memberAddresses=MemberAddress.dao.find("select * from t_member_address where memberId=?",member.getId());
		setAttr("memberAddresses",memberAddresses);
		
		renderVelocity("member/addressList.vm");
	}
	
	public void initEditAddress(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Map<String, Object> result=new HashMap<String, Object>();
		Long id=getParaToLong("id");
		if(id!=null&&id>0){
			MemberAddress memberAddress=MemberAddress.dao.findById(id);
			result.put("memberAddress", memberAddress);
			
			if(memberAddress.getAddressId()!=null){
				Region region=Region.dao.findFirst("select * from t_region where code=?",memberAddress.getAddressId());
				result.put("currentAddress", region);
			}
		}
		renderJson(result);
	}
	
	public void saveAddress(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Map<String, Object> result=new HashMap<String, Object>();
		Long id=getParaToLong("id");
		MemberAddress memberAddress=new MemberAddress();
		memberAddress.setMemberId(member.getId());
		String name=getPara("name");
		memberAddress.setName(name);
		Integer addressId=getParaToInt("addressId");
		memberAddress.setAddressId(addressId);
		String detailAddress=getPara("detailAddress");
		memberAddress.setDetailAddress(detailAddress);
		String phone=getPara("phone");
		memberAddress.setPhone(phone);
		
		Region region=Region.dao.findFirst("select * from t_region where code=?",addressId);
		memberAddress.setAddress(region.getProvinceName()+region.getCityName()+region.getAreaName()+detailAddress);
		if(id!=null&&id!=0){		//修改
			memberAddress.setId(id);
			memberAddress.update();
		}
		else{		//新增
			Record record=Db.findFirst("select count(1) cnt from t_member_address where memberId=?",member.getId());
			if(record.getLong("cnt")<=0){
				memberAddress.setIsDefault("1");
			}
			
			memberAddress.setCreateTime(new Date());
			memberAddress.save();
			
			result.put("memberAddress", memberAddress);
		}
		
		result.put("ok", true);
		renderJson(result);
	}
	
	/**
	 * 删除地址
	 */
	public void deleteMemberAddress(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Long id=getParaToLong("id");
		MemberAddress address=MemberAddress.dao.findById(id);
		address.delete();
		if("1".equals(address.getIsDefault())){
			Db.update("update t_member_address set isdefault=1 where memberid=? order by createTime desc limit 1",member.getId());
		}
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("ok", true);
		renderJson(result);
	}
	
	/**
	 * 设置默认
	 */
	public void setDefault(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		Long id=getParaToLong("id");
		Db.update("update t_member_address set isDefault=0 where memberId=?",member.getId());		//取消当前默认
		Db.update("update t_member_address set isDefault=1 where id=?",id);		//设置选择的为默认
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("ok", true);
		renderJson(result);
	}
	
	public void getAddressList(){
		Member member=getMember();
		if(member==null){
			renderNull();
			return;
		}
		List<MemberAddress> memberAddresses=MemberAddress.dao.find("select * from t_member_address where memberId=?",member.getId());
		renderJson(memberAddresses);
	}
}
