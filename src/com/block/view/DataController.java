package com.block.view;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.block.common.LabelTypeEnum;
import com.block.common.model.LevelSetting;
import com.block.common.model.Member;
import com.block.common.model.MemberLabel;
import com.block.common.model.MemberTypeChange;
import com.block.common.model.Order;
import com.block.utils.CacheUtil;
import com.block.utils.DateUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;

public class DataController extends BaseController{
	
	/**
	 * 初始化标签
	 */
	public void initLabelData(){
		List<Member> members=Member.dao.find("select * from t_member where id in(select memberId from t_order where ispay=1)");
		for(Member member:members){
			MemberLabel levelLabel=new MemberLabel();		//会员级别
			levelLabel.setIsSystem("1");
			levelLabel.setTypeCode(LabelTypeEnum.LABELTYPE_LEVEL.getCode());
			levelLabel.setTypeName(LabelTypeEnum.LABELTYPE_LEVEL.getName());
			levelLabel.setMemberId(member.getId());
			LevelSetting levelSetting=LevelSetting.dao.getLevelByGrade(member.getMemberLevel());
			levelLabel.setLabelName(levelSetting.getGradename()+"会员");
			
			MemberLabel moneyLabel=new MemberLabel();		//购物金额
			moneyLabel.setIsSystem("1");
			moneyLabel.setTypeCode(LabelTypeEnum.LABELTYPE_MONEY.getCode());
			moneyLabel.setTypeName(LabelTypeEnum.LABELTYPE_MONEY.getName());
			moneyLabel.setMemberId(member.getId());
			if(member.getTotalmoney()<2000){
				moneyLabel.setLabelName("低消费买家");
			}
			else if(member.getTotalmoney()>=2000&&member.getTotalmoney()<=5000){
				moneyLabel.setLabelName("中消费买家");
			}
			else{
				moneyLabel.setLabelName("高消费买家");
			}
			
			long count=Order.dao.getOrderCnt(member.getId());
			MemberLabel repeatLabel=new MemberLabel();		//回头客
			repeatLabel.setIsSystem("1");
			repeatLabel.setTypeCode(LabelTypeEnum.LABELTYPE_REPEAT.getCode());
			repeatLabel.setTypeName(LabelTypeEnum.LABELTYPE_REPEAT.getName());
			repeatLabel.setMemberId(member.getId());
			if(count<3){
				repeatLabel.setLabelName("低回头顾客");
			}
			else if(count>=3&&count<=5){
				repeatLabel.setLabelName("中回头顾客");
			}
			else if(count>=6&&count<=10){
				repeatLabel.setLabelName("高回头顾客");
			}
			else{
				repeatLabel.setLabelName("超高回头顾客");
			}
			
			MemberLabel recentLabel=new MemberLabel();		//近期购物情境
			recentLabel.setIsSystem("1");
			recentLabel.setTypeCode(LabelTypeEnum.LABELTYPE_RECENT.getCode());
			recentLabel.setTypeName(LabelTypeEnum.LABELTYPE_RECENT.getName());
			recentLabel.setMemberId(member.getId());
			double days=DateUtil.daysRange(member.getLastBuyTime(), new Date());
			if(days<30){
				recentLabel.setLabelName("最近买家");
			}
			else if(days>30&&days<=90){
				recentLabel.setLabelName("中期未购物买家");
			}
			else{
				recentLabel.setLabelName("长期未购物买家");
			}
			
			MemberLabel brandLabel=new MemberLabel();		//品牌
			brandLabel.setIsSystem("1");
			brandLabel.setTypeCode(LabelTypeEnum.LABELTYPE_BRAND.getCode());
			brandLabel.setTypeName(LabelTypeEnum.LABELTYPE_BRAND.getName());
			brandLabel.setMemberId(member.getId());
			brandLabel.setLabelName(Order.dao.getBrandLabel(member.getId()));
			
			MemberLabel sizeLabel=new MemberLabel();		//身形
			sizeLabel.setIsSystem("1");
			sizeLabel.setTypeCode(LabelTypeEnum.LABELTYPE_SIZE.getCode());
			sizeLabel.setTypeName(LabelTypeEnum.LABELTYPE_SIZE.getName());
			sizeLabel.setMemberId(member.getId());
			String sizeLabelName=Order.dao.getSizeLabel(member.getId());
			sizeLabel.setLabelName(sizeLabelName==null?"":sizeLabelName);
			
			MemberLabel styleLabel=new MemberLabel();		//风格
			styleLabel.setIsSystem("1");
			styleLabel.setTypeCode(LabelTypeEnum.LABELTYPE_STYLE.getCode());
			styleLabel.setTypeName(LabelTypeEnum.LABELTYPE_STYLE.getName());
			styleLabel.setMemberId(member.getId());
			String styleLabelName=Order.dao.getStyleLabel(member.getId());
			styleLabel.setLabelName(styleLabelName==null?"":styleLabelName);
			
			MemberLabel timeLabel=new MemberLabel();		//入店资历
			timeLabel.setIsSystem("1");
			timeLabel.setTypeCode(LabelTypeEnum.LABELTYPE_TIME.getCode());
			timeLabel.setTypeName(LabelTypeEnum.LABELTYPE_TIME.getName());
			timeLabel.setMemberId(member.getId());
			timeLabel.setLabelName(DateUtil.getInTimeLabel(member.getRegisterTime()));
			
			Record record=Db.findFirst("select count(1)cnt from t_member_label where memberid=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_LEVEL.getCode());
			if(record.getLong("cnt")==null||record.getLong("cnt")<=0){		//第一次下单,需要新增标签
				levelLabel.save();
				moneyLabel.save();
				recentLabel.save();
				repeatLabel.save();
				brandLabel.save();
				sizeLabel.save();
				styleLabel.save();
				timeLabel.save();
			}
			else{		//非第一次下单更新标签
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",levelLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_LEVEL.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",moneyLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_MONEY.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",recentLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_RECENT.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",repeatLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_REPEAT.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",brandLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_BRAND.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",sizeLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_SIZE.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",styleLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_STYLE.getCode());
				Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",timeLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_TIME.getCode());
			}
		}
		renderText("success");
	}
	
	/**
	 * 初始化会员类型
	 */
	public void initMemberType(){
		int buyperiod=CacheUtil.getBuyPeriod();
		Date now=new Date();
		List<Member> members=Member.dao.find("select * from t_member where lastBuyTime is not null && lastBuyTime!=''");
		for(Member member:members){
			double days=DateUtil.daysRange(member.getLastBuyTime(), now);
			System.out.println(member.getId()+member.getNickname()+":"+days);
			if(days>buyperiod*3){		//超过3个购买周期
				if(!"S3".equals(member.getMemberType())){		//不是沉睡的改为沉睡
					MemberTypeChange memberTypeChange=new MemberTypeChange();
					memberTypeChange.setMemberId(member.getId());
					memberTypeChange.setBeforeType(member.getMemberType());
					memberTypeChange.setAfterType("S3");
					memberTypeChange.setChangeTime(now);
					
					member.setMemberType("S3");
					member.update();
					memberTypeChange.save();		//插入类型变更记录
				}
			}
			else if(days>buyperiod*2){	//超过2个购买周期
				if(!"S2".equals(member.getMemberType())){		//不是半睡的改为半睡
					MemberTypeChange memberTypeChange=new MemberTypeChange();
					memberTypeChange.setMemberId(member.getId());
					memberTypeChange.setBeforeType(member.getMemberType());
					memberTypeChange.setAfterType("S2");
					memberTypeChange.setChangeTime(now);
					
					member.setMemberType("S2");
					member.update();		//更新类型
					memberTypeChange.save();		//插入类型变更记录
				}
			}
			else if(days>buyperiod){		//超过1个购买周期
				if(!"S1".equals(member.getMemberType())){		//不是瞌睡的改为瞌睡
					MemberTypeChange memberTypeChange=new MemberTypeChange();
					memberTypeChange.setMemberId(member.getId());
					memberTypeChange.setBeforeType(member.getMemberType());
					memberTypeChange.setAfterType("S1");
					memberTypeChange.setChangeTime(now);
					
					member.setMemberType("S1");
					member.update();		//更新类型
					memberTypeChange.save();		//插入类型变更记录
				}
			}
			else if(days<buyperiod){		//一个购买周期内有消费
				Calendar start=Calendar.getInstance();
				start.add(Calendar.DAY_OF_MONTH, (0-buyperiod));
				Record record=Db.findFirst("select count(1)cnt from t_order where memberid=? and ispay=1 and orderTime>=? and orderTime<=?",member.getId(),start.getTime(),now);
				if(record.getLong("cnt")!=null&&record.getLong("cnt")>=2){
					if(!"E".equals(member.getMemberType())){
						MemberTypeChange memberTypeChange=new MemberTypeChange();
						memberTypeChange.setMemberId(member.getId());
						memberTypeChange.setBeforeType(member.getMemberType());
						memberTypeChange.setAfterType("E");
						memberTypeChange.setChangeTime(now);
						
						member.setMemberType("E");
						member.update();		//更新类型
						memberTypeChange.save();		//插入类型变更记录
					}
				}
				else{
					if(!"N".equals(member.getMemberType())){
						MemberTypeChange memberTypeChange=new MemberTypeChange();
						memberTypeChange.setMemberId(member.getId());
						memberTypeChange.setBeforeType(member.getMemberType());
						memberTypeChange.setAfterType("N");
						memberTypeChange.setChangeTime(now);
						
						member.setMemberType("N");
						member.update();		//更新类型
						memberTypeChange.save();		//插入类型变更记录
					}
				}
			}
		}
		renderText("success");
	}
	
	/** 
	*  初始化昵称 
	* @return void     
	*/
	public void initNickname(){
		List<Member> members=Member.dao.find("select * from t_member");
		for(Member member:members){
			if(StringUtils.isNotBlank(member.getOpenid())){
				ApiResult apiResult=UserApi.getUserInfo(member.getOpenid());
				member.setNickname(apiResult.get("nickname").toString());
				member.update();
			}
		}
		renderText("success");
	}
}
