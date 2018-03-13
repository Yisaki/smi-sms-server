package com.smi.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smi.beans.SMSRedisTools;
import com.smi.model.Req2Phone;
import com.smi.model.SmsHistory;
import com.smi.service.SmsHistoryService;
import com.smi.service.SmsReq2PhoneService;


/**
 * 负责将Redis中的已发送短信保存到数据库中
 * @author Ryan Wu
 *
 */
@Component
public class SMSPersistor extends Thread {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private SMSRedisTools smsRedisTools;
	
	@Autowired
	private SmsHistoryService smsHistoryService;
	
	@Autowired
	private SmsReq2PhoneService smsReq2PhoneService;
	
	
	private final long waitInMilliSeconds = 1000 * 30l;
	private final int fetchSMSCount = 200;
	
	@Override
	public void run() {
		
		while(true) {
			try {
				List<SmsHistory> smses = smsRedisTools.fetchSMS(fetchSMSCount);
				if(smses.size() > 0) {
					this.batchSaveSms(smses);
				} else {
					try {
						logger.info("Redis暂无可持久化的短信，故线程休眠" + waitInMilliSeconds/1000 + "秒");
						Thread.sleep(waitInMilliSeconds);
					} catch (Exception e) {}
				}
			} catch (Exception e) {
				logger.error("处理Redis中的短信的线程异常", e);
			}
		}
	}
	
	/**
	 * 不使用事务
	 * @param smses
	 */
	private void batchSaveSms(List<SmsHistory> smses) {
		smsHistoryService.batchSave(smses);
		
		List<Req2Phone> rps = new ArrayList<Req2Phone>();
		for(SmsHistory sms: smses) {
			List<String> failedPhones = new ArrayList<String>();
			if(sms.getFailedPhones() != null && sms.getFailedPhones().trim().length() > 0) {
				String[] fps = sms.getFailedPhones().trim().split(",");
				for(String fp: fps) {
					failedPhones.add(fp);
				}
			}
			
			String[] phones = sms.getPhoneNo().split(",");
			for(String p: phones) {
				if(failedPhones.contains(p)) {
					rps.add(new Req2Phone(sms.getReqNo(), p, "1"));
				} else {
					rps.add(new Req2Phone(sms.getReqNo(), p));
				}
			}
		}
		
		//分批保存序列号与手机号的对应关系
		int start = 0, end = start + fetchSMSCount;
		while(true) {
			if(end < rps.size()) {
				smsReq2PhoneService.batchSave(rps.subList(start, end));
				start = end;
				end = start + fetchSMSCount;
			} else {
				end = rps.size();
				smsReq2PhoneService.batchSave(rps.subList(start, end));
				break;
			}
		}
		
	}


}
