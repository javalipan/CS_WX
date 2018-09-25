package com.block.utils;

import java.math.BigDecimal;

public class NumberUtil {

	/** 
	* 加法（a+b）
	* @param a
	* @param b
	* @return    double
	* @author lipan@cqrainbowsoft.com    
	*/
	public static double add(double a,double b){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		BigDecimal result=a1.add(new BigDecimal(String.valueOf(b)));
		return result.doubleValue();
	}
	
	/** 
	* 减法(a-b)
	* @param a
	* @param b
	* @return   double 
	* @author lipan@cqrainbowsoft.com    
	*/
	public static double subtract(double a,double b){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		BigDecimal result=a1.subtract(new BigDecimal(String.valueOf(b)));
		return result.doubleValue();
	}
	
	/** 
	* 乘法(a*b)
	* @param a
	* @param b
	* @return    double
	* @author lipan@cqrainbowsoft.com    
	*/
	public static double multiply(double a,double b){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		BigDecimal result=a1.multiply(new BigDecimal(String.valueOf(b)));
		return result.doubleValue();
	}
	
	/** 
	* 除法(a/b)
	* @param a
	* @param b
	* @return    double
	* @author lipan@cqrainbowsoft.com    
	*/
	public static double divide(double a,double b){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		BigDecimal result=a1.divide(new BigDecimal(String.valueOf(b)),10,BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}
	
	/** 
	* 保留几位小数
	* @param a
	* @param count :小数位数
	* @return    double
	* @author lipan@cqrainbowsoft.com    
	*/
	public static double toFixed(double a,int count){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		return a1.setScale(count,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static String toFixedString(double a,int count){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		return a1.setScale(count,BigDecimal.ROUND_HALF_UP).toString();
	}
	
	/**
	 * 取余
	 * @param a
	 * @param b
	 * @return
	 */
	public static double remainder(double a,double b){
		BigDecimal a1=new BigDecimal(String.valueOf(a));
		BigDecimal b1=new BigDecimal(String.valueOf(b));
		BigDecimal result[] =a1.divideAndRemainder(b1);
		return result[1].doubleValue();
	}
	
	public static void main(String[] args) {
		System.out.println(3.2+3.1);
		System.out.println(add(3.2, 3.1));
		System.out.println(3.3-3.1);
		System.out.println(subtract(3.3, 3.1));
		System.out.println(toFixedString(60, 2));
		System.out.println(divide(5,2.2));
		System.out.println(remainder(0.1, 0.01));
	}
}
