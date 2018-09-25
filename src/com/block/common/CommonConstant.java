package com.block.common;

/**
 * @author Administrator
 *
 */
public interface CommonConstant {
	
	
	/**
	 * 推广积分限制（通过推广获得的总积分不能超过此数量）
	 */
	public static final int TGTOTAL_POINT=5000;
	/**
	 * 完善个人信息赠送积分
	 */
	public static final int REGIST_POINT=1000;
	/**
	 * 二维码推广积分
	 */
	public static final int TUIGUANG_POINT=50;
	
	/**
	 * 积分兑换比例
	 */
	public static final int INT_WEIGHT=20;
	/**
	 * 积分兑换倍数，只能兑换此数额的整数倍
	 */
	public static final int INT_TIMES=1000;
	
	/**
	 * 管理员openid
	 */
	public static final String MANAGER_OPENID[]={"oYNT31OPyxUMsBWymWKmn7WAgy84","oYNT31ON_PosCwi-JFAWgKtEECg8"};
	
	public static final Long IMG_PROGRAM_ID=2l;
	
	/**
	 * 特别报道
	 */
	public static final Long REPORT_PROGRAM_ID=5l;
	/**
	 * 公告
	 */
	public static final Long NOTICE_PROGRAM_ID=3l;
	
	/**
	 * 注册赠送优惠券id
	 */
	public static final Long REGISTE_COUPON_ID=1l;
	/**
	 * 被邀请人优惠券id
	 */
	public static final Long INVITED_COUPON_ID=2l;
	/**
	 * 邀请人优惠券id
	 */
	public static final Long INVITER_COUPON_ID=3l;
	
	public static final String WEIXIN_DOMAIN="http://weixin.karlay.cn/";
	public static final String FILE_DOMAIN="http://admin.karlay.cn/";
}
