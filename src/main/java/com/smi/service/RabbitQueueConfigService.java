package com.smi.service;

import java.util.HashMap;
import java.util.Map;

import com.smi.model.MQInfo;


public class RabbitQueueConfigService {
	private static Map<Integer, MQInfo> confs = new HashMap<Integer, MQInfo>();
	
	public static void setConf(Integer key, MQInfo conf) {
		confs.put(key, conf);
	}
	
	public static MQInfo getConf(Integer key) {
		return confs.get(key);
	}
}
