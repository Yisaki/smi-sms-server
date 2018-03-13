package com.smi;

import java.util.HashMap;
import java.util.Map;

import com.smi.model.SmsAccount;

public class SmiConfigInfo {
	
	/**
	 * 缓存大汉三通短信发送结果编码和描述
	 */
	private static Map<String, String> dhstSendCodes = new HashMap<String, String>();
	
	
	/**
	 * 缓存大汉三通短信状态报告的编码和描述
	 */
	private static Map<String, String> dhstReportCodes = new HashMap<String, String>();
	
	
	/**
	 * 使用短信的渠道编码
	 */
	private static Map<String, String> channelCodes = new HashMap<String, String>();
	
	

	/**
	 * 大汉三通短信账号信息，key为sms_account表的id
	 */
	private static Map<Integer, SmsAccount> dhstAccountInfos = new HashMap<Integer, SmsAccount>();
	
	
	/**
	 * 缓存大汉三通对各业务系统的配置信息，不在此配置缓存中的请求方的短信将不会提交到大汉三通
	 * 最外层Map的key为业务系统的ip, value为Map类型，其中：key为channel，value为Object[2]数组，[0]为SmsAccount实例，[1]为RabbitQueueConfig实例
	 */
	private static Map<String, Map<String, Object[]>> bizConfigInfos = new HashMap<String, Map<String, Object[]>>();
	
	
	/**
	 * 返回大汉三通短信发送结果编码集
	 * 
	 */
	public static Map<String, String> getDhstSendCodes() {
		return dhstSendCodes;
	}
	
	
	/**
	 * 返回大汉三通短信状态报告结果编码集
	 * 
	 */
	public static Map<String, String> getDhstReportCodes() {
		return dhstReportCodes;
	}
	

	/**
	 * 返回短信服务平台上当前已配置的所有渠道信息
	 * 
	 */
	public static Map<String, String> getChannelCodes() {
		return channelCodes;
	}
	
	
	/**
	 * 返回大汉三通账号信息
	 */
	public static Map<Integer, SmsAccount> getDhstAccountInfos() {
		return dhstAccountInfos;
	}


	public static Map<String, Map<String, Object[]>> getBizConfigInfos() {
		return bizConfigInfos;
	}


	public static void setDhstSendCodes(Map<String, String> dhstSendCodes) {
		SmiConfigInfo.dhstSendCodes = dhstSendCodes;
	}


	public static void setDhstReportCodes(Map<String, String> dhstReportCodes) {
		SmiConfigInfo.dhstReportCodes = dhstReportCodes;
	}


	public static void setChannelCodes(Map<String, String> channelCodes) {
		SmiConfigInfo.channelCodes = channelCodes;
	}


	public static void setDhstAccountInfos(Map<Integer, SmsAccount> dhstAccountInfos) {
		SmiConfigInfo.dhstAccountInfos = dhstAccountInfos;
	}


	public static void setBizConfigInfos(Map<String, Map<String, Object[]>> bizConfigInfos) {
		SmiConfigInfo.bizConfigInfos = bizConfigInfos;
	}
}
