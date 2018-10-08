package com.block.utils.thread;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.block.common.LabelTypeEnum;
import com.block.common.model.LevelSetting;
import com.block.common.model.Member;
import com.block.common.model.MemberLabel;
import com.block.common.model.Order;
import com.block.utils.DateUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 标签计算
 * @author Administrator
 *
 */
public class LabelThread implements Runnable{
	
	private Member member;
	
	public LabelThread(Member member){
		this.member=member;
	}

	public void run() {
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
		sizeLabel.setLabelName(Order.dao.getSizeLabel(member.getId()));
		
		MemberLabel styleLabel=new MemberLabel();		//风格
		styleLabel.setIsSystem("1");
		styleLabel.setTypeCode(LabelTypeEnum.LABELTYPE_STYLE.getCode());
		styleLabel.setTypeName(LabelTypeEnum.LABELTYPE_STYLE.getName());
		styleLabel.setMemberId(member.getId());
		String ln=Order.dao.getStyleLabel(member.getId());
		styleLabel.setLabelName(StringUtils.isBlank(ln)?"":ln);
		
		MemberLabel timeLabel=new MemberLabel();		//入店资历
		timeLabel.setIsSystem("1");
		timeLabel.setTypeCode(LabelTypeEnum.LABELTYPE_TIME.getCode());
		timeLabel.setTypeName(LabelTypeEnum.LABELTYPE_TIME.getName());
		timeLabel.setMemberId(member.getId());
		timeLabel.setLabelName(DateUtil.getInTimeLabel(member.getRegisterTime()));
		
		Record levelLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_LEVEL.getCode());
		if(levelLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",levelLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_LEVEL.getCode());
		}
		else{
			levelLabel.save();
		}
		
		Record moneyLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_MONEY.getCode());
		if(moneyLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",moneyLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_MONEY.getCode());
		}
		else{
			moneyLabel.save();
		}
		
		Record recentLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_RECENT.getCode());
		if(recentLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",recentLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_RECENT.getCode());
		}
		else{
			recentLabel.save();
		}
		
		Record repeatLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_REPEAT.getCode());
		if(repeatLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",repeatLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_REPEAT.getCode());
		}
		else{
			repeatLabel.save();
		}
		
		Record brandLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_BRAND.getCode());
		if(brandLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",brandLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_BRAND.getCode());
		}
		else{
			brandLabel.save();
		}
		
		Record sizeLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_SIZE.getCode());
		if(sizeLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",sizeLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_SIZE.getCode());
		}
		else{
			sizeLabel.save();
		}
		
		Record styleLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_STYLE.getCode());
		if(styleLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",styleLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_STYLE.getCode());
		}
		else{
			styleLabel.save();
		}
		
		Record timeLabelrecord=Db.findFirst("select count(1) cnt from t_member_label where memberId=? and typeCode=?",member.getId(),LabelTypeEnum.LABELTYPE_TIME.getCode());
		if(timeLabelrecord.getLong("cnt")>0){
			Db.update("update t_member_label set labelName=? where memberId=? and typeCode=?",timeLabel.getLabelName(),member.getId(),LabelTypeEnum.LABELTYPE_TIME.getCode());
		}
		else{
			timeLabel.save();
		}
	}
}
