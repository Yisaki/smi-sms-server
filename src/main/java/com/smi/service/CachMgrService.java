package com.smi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.FinalValue;
import com.smi.SmiConfigInfo;
import com.smi.dao.CodeDictMapper;
import com.smi.dao.SmsAccountMapper;
import com.smi.dao.SmsBizConfigMapper;
import com.smi.model.CodeDict;
import com.smi.model.SmsAccount;
import com.smi.model.SmsBizConfig;

/**
 * 缓存管理服务
 * @author Ryan Wu
 *
 */
@Service
public class CachMgrService{

	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	CodeDictMapper codeDictMapper;

	@Autowired
	SmsAccountMapper smsAccountMapper;
	
	@Autowired
	private SmsBizConfigMapper smsBizConfigMapper;
	
	
	/**
	 * 刷新系统中对code_dict、sms_account和sms_biz_config表的缓存数据
	 */
	public void refreshCache() {
		Map<String, String> dhstSendCodes = new HashMap<String, String>();		
		Map<String, String> dhstReportCodes = new HashMap<String, String>();
		Map<String, String> channelCodes = new HashMap<String, String>();
		List<CodeDict> dicts = codeDictMapper.listAll();
		
		logger.info("================ 开始加载字典信息到缓存 =================");
		for (CodeDict dict : dicts) {			
			switch(dict.getType()) {
				case FinalValue.DICT_TYPE_CHANNEL:
					channelCodes.put(dict.getCode(), dict.getDescription());
					break;
				case FinalValue.DICT_TYPE_REPORT_CODE_DHST:
					dhstReportCodes.put(dict.getCode(), dict.getDescription());
					break;
				case FinalValue.DICT_TYPE_SEND_CODE_DHST:
					dhstSendCodes.put(dict.getCode(), dict.getDescription());
					break;
				default:
					logger.error("未识别的配置类型：type=" + dict.getType());
			}
		}
		SmiConfigInfo.setChannelCodes(channelCodes);
		SmiConfigInfo.setDhstReportCodes(dhstReportCodes);
		SmiConfigInfo.setDhstSendCodes(dhstSendCodes);		
		logger.info("================ 完成加载字典信息到缓存 =================");
		

		logger.info("================ 开始加载短信账号信息到缓存 =================");
		Map<Integer, SmsAccount> dhstAccountInfos = new HashMap<Integer, SmsAccount>();
		List<SmsAccount> accounts = smsAccountMapper.listAll();
		for (SmsAccount acc : accounts) {			
			switch(acc.getCompanyType()) {
				case FinalValue.COMPANY_TYPE_DHST: //大汉三通
					dhstAccountInfos.put(acc.getId(), acc);
					break;
				default:
					logger.error("未识别的短信公司：companyType=" + acc.getCompanyType());
			}
		}
		SmiConfigInfo.setDhstAccountInfos(dhstAccountInfos);
		logger.info("================ 完成加载短信账号信息到缓存 =================");
		
		
		logger.info("================ 开始加载对各业务系统的配置信息到缓存 =================");
		Map<String, Map<String, Object[]>> bizConfigInfos = new HashMap<String, Map<String, Object[]>>();
		List<SmsBizConfig> configs = smsBizConfigMapper.listAll();
		for(SmsBizConfig conf: configs) {
			Map<String, Object[]> c =  bizConfigInfos.get(conf.getIp());
			if(c == null) {
				c = new HashMap<String, Object[]>();
				bizConfigInfos.put(conf.getIp(), c);
			}
			Object[] array = c.get(conf.getChannel());
			if(array == null) {
				array = new Object[2];
				c.put(conf.getChannel(), array);
			}
			
			array[0] = SmiConfigInfo.getDhstAccountInfos().get(conf.getAccountId());
			array[1]  = RabbitQueueConfigService.getConf(conf.getQueue()); 
			
		}
		SmiConfigInfo.setBizConfigInfos(bizConfigInfos);
		logger.info("================ 完成加载对各业务系统的配置信息到缓存 =================");
	}
	
}
