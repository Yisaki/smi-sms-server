package com.smi.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.smi.FinalValue;
import com.smi.SmiConfigInfo;
import com.smi.model.SmsAccount;
import com.smi.model.SmsHistory;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.MD5Kit;


@Component
public class Sms2GateWaySender {
	private Logger logger = Logger.getLogger(this.getClass());
	
	
	/**
	 * 向大汉三通提交短信（不会抛异常）
	 * @param sms
	 * @return 提交短信成功返回true（但true并不意味业务逻辑上的成功，只是表示网络层面或系统层面提交成功），否则返回false(失败信息可以从入参sms的note中获取到)
	 */
	public boolean postToDHServer(SmsHistory sms) {
		Object[] conf = BizConfigChecker.check(sms.getSenderIp(), sms.getChannel());
		SmsAccount account = (SmsAccount)conf[0];
		
		Map<String, Object> params = new HashMap<String, Object>(7);
		params.put("account", account.getAccount());
		params.put("password", MD5Kit.getMD5String(account.getPassword()));
		params.put("msgid", sms.getReqNo());
		params.put("phones", sms.getPhoneNo());
		params.put("content", sms.getContent());
		params.put("sign", account.getSign());
		params.put("subcode", "");
		
		sms.setSendTime(new Date());
		
		try {
			
			String rtnStr = HttpKit.post(account.getBaseUrl(), JSONObject.toJSONString(params));
			
			logger.info("向大汉三通提交短信（ReqNo=" + sms.getReqNo() + "）的返回结果： " + rtnStr);

			Map rtnMap = JSONObject.parseObject(rtnStr, Map.class);
			int code = Integer.parseInt(rtnMap.get("result").toString());
			
			if (code != 0) {
				sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
				sms.setNote(SmiConfigInfo.getDhstSendCodes().get(code) + "(code:" + code + ", desc:" + rtnMap.get("desc").toString() + ")");
			} else {
				sms.setStatusCode(FinalValue.SEND_STATUS_SUECCESS);
			}
			
			sms.setFailedPhones((String)rtnMap.get("failPhones"));
			
		} catch (Exception e) {
			logger.error("向大汉三通提交短信（ReqNo=" + sms.getReqNo() + "）异常：" + e.getMessage());
			sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
			sms.setNote(e.getMessage());
			sms.setFailedPhones(sms.getPhoneNo());
			return false;
		}
		
		return true;
	}
	
}
