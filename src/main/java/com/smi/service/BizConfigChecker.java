package com.smi.service;

import java.util.Map;

import com.smi.SmiConfigInfo;


public class BizConfigChecker {
	
	/**
	 * 校验请求方是否合法
	 * @param clientIp
	 * @param channel
	 * @return 若不合法，则返回null，否则返回一个大小为2的数组：[0]为SmsAccount实例，[1]为MQInfo实例
	 */
	public static Object[] check(String clientIp, String channel) {
		if(clientIp != null && channel != null) {
			Map<String, Object[]> conf = SmiConfigInfo.getBizConfigInfos().get(clientIp);
			if(conf != null) {
				return conf.get(channel);	
			}
		}
		
		return null;
	}
}
