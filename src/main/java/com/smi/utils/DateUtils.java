package com.smi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static String formateDate(Date date,String pattern)
	{
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 返回yyyy_MM格式时间
	 * @return
	 */
	public static String getCurMonth()
	{
		return new SimpleDateFormat("yyyy_MM").format(new Date());
	}
	
	/**
	 * 返回yyyy-MM格式时间
	 * @return
	 */
	public static String getYearMonth(Date date)
	{
		if(date==null)
			return null;
		
		return new SimpleDateFormat("yyyy_MM").format(date);
	}
}
