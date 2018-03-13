package com.smi.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.FinalValue;
import com.smi.SmiConfigInfo;
import com.smi.dao.SmsHistoryMapper;
import com.smi.model.MQInfo;
import com.smi.model.SmsAccount;
import com.smi.model.SmsHistory;
import com.smi.mq.SmiProducer;
import com.smi.vo.ReturnMsg;

@Service
public class SmsProducerService {
	private Logger logger=Logger.getLogger(this.getClass());
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	private Pattern phonePattern = Pattern.compile("^\\d{11}(,\\d{11})*$");
	
	@Autowired
	private SmsHistoryMapper smsHistoryMapper;
	
	
	
	/**
	 * 生成序列号 yyyyMMddHHmmssSSS + 15位随机数。大汉三通侧限制长度不能超过32位。注：此函数生成的序列号前缀必须为“yyyyMM”，因为在SmsHistoryService中有使用到
	 * @return 返回长度为32位的随机数
	 */
	public String makeReqNo() {
		return sdf.format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
	}
	
	

	/**
	 * 发布短信到mq
	 * @param reqNo
	 * @param channel
	 * @param content
	 * @param phone
	 * @param account
	 * @param mqInfo
	 * @param clientIP
	 * @return
	 */
	public ReturnMsg sendMsg(String reqNo, String channel, String content, String phone, SmsAccount account, MQInfo mqInfo, String clientIP) {
		SmsHistory sms = new SmsHistory();
		sms.setCompanyType(account.getCompanyType());
		sms.setChannel(channel);
		sms.setContent(content);
		sms.setPhoneNo(phone);
		sms.setCreateTime(new Date());
		sms.setReqNo(reqNo);
		sms.setSenderIp(clientIP);
		sms.setSign(account.getSign());
		
		ReturnMsg msg = validation(sms);
		msg.setReqNo(reqNo);
		if(!FinalValue.RtnCode.RTN_REC_OK.equals(msg.getCode())) {
			logger.error("请求校验失败：reqNo=" + msg.getReqNo() + ", error=" + msg.getMsg());
			return msg;
		}
		
		sms.setAccountType(account.getAccountType());
		
		
		//发布到mq
		SmiProducer producer = new SmiProducer(mqInfo.getHost(), mqInfo.getPort(), mqInfo.getExchangeName(), mqInfo.getQueueName(), mqInfo.getRouteKey(), mqInfo.getQos());
		try {
			producer.newConnect();
			producer.publish(sms);
		} catch(Exception e) {
			logger.error("向MQ提交短信(reqNo=" + reqNo + ")失败", e);
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg(e.getMessage());
		} finally {
			try {
				producer.close();
			} catch (Exception e) {
				logger.error("关闭提交短信(reqNo=" + reqNo + ")到MQ的连接失败：" + e.getMessage());
			}
		}
		
		return msg;
	}
	
	
	private ReturnMsg validation(SmsHistory sms) {
		ReturnMsg msg = new ReturnMsg();
		
		//检查渠道编码是否存在
		if (!SmiConfigInfo.getChannelCodes().containsKey(sms.getChannel())) {
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg("渠道编码[" + sms.getChannel() + "]错误");
			return msg;
		}
		
		// 检查手机号码格式是否正确
		if (!phonePattern.matcher(sms.getPhoneNo()).matches()) {
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg("手机号格式不正确！");
			return msg;
		}
		
		//校验手机号最大个数
		String[] phones = sms.getPhoneNo().split(",");
		if (phones.length > FinalValue.MAX_PHONES_PER_REQUEST) {
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg("一次请求中发送的手机号码数不得超过" + FinalValue.MAX_PHONES_PER_REQUEST + "个，你当前的请求中的号码数为：" + phones.length);
			return msg;
		}
		sms.setMobiCount(phones.length);
		
		return msg;
	}
	
}
