package com.block.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * 判断是否在时间范围，精确到天
	 * @param from
	 * @param end
	 * @return boolean
	 * @throws ParseException 
	 */
	public static boolean isInRange(Date from,Date end) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date now = null;
		try {
			now = sdf.parse(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(now.getTime()>=from.getTime()&&now.getTime()<=end.getTime()){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前日期是否在范围
	 * @param from
	 * @param end
	 * @return 1：未开始，2：已开始未到期，3：已过期
	 */
	public static String compareRange(Date from,Date end){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date now = null;
		try {
			now = sdf.parse(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(now.getTime()<from.getTime()){
			return "1";
		}
		else if(now.getTime()>end.getTime()){
			return "3";
		}
		else{
			return "2";
		}
	}
	
	/**
	 * 计算量日期相差天数
	 * @param lasttime
	 * @param currentTime
	 * @return
	 */
	public static double daysRange(Date lasttime,Date currentTime){
		double days=(currentTime.getTime()-lasttime.getTime())/1000/60/60/24;
		return days;
	}

	public static String getInTimeLabel(Date indate){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date from=null;
		Date now=new Date();
		try {
			from=sdf.parse("2017-07-15");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long total=now.getTime()-from.getTime();
		long item=total/3;
		if(indate.compareTo(from)<=0){
			return "早期关注";
		}
		if((indate.getTime()-from.getTime())>2*item){
			return "晚期关注";
		}
		else if((indate.getTime()-from.getTime())>1*item){
			return "中期关注";
		}
		else{
			return "早期关注";
		}
	}
}
