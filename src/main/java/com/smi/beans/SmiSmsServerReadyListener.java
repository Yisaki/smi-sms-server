package com.smi.beans;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.smi.jobs.SMSPersistor;
import com.smi.model.MQInfo;
import com.smi.mq.SmiSubscribeConsumer;
import com.smi.service.CachMgrService;
import com.smi.service.RabbitQueueConfigService;
import com.smi.service.Sms2GateWaySender;

@Component
public class SmiSmsServerReadyListener implements ApplicationListener<ApplicationReadyEvent> {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private SMSRedisTools smsRedisTools;
	@Autowired
	private SMSPersistor smsPersistor;
	@Autowired
	private Sms2GateWaySender sms2GateWaySender;
	@Autowired
	private CachMgrService cachMgrService;
	
	//mq基本设置
	@Value("${rabbitmq.host}")
	private String host;
	@Value("${rabbitmq.port}")
	private Integer port;
	@Value("${rabbitmq.qos}")
	private Integer qos;
	
	//sms_mq队列设置
	@Value("${rabbitmq.sms_mq.exchange}")
	private String sms_mq_exchangeName;
	@Value("${rabbitmq.sms_mq.queueName}")
	private String sms_mq_queueName;
	@Value("${rabbitmq.sms_mq.routeKey}")
	private String sms_mq_routeKey;
	//delay_mq队列设置
	@Value("${rabbitmq.delay_mq.exchange}")
	private String delay_mq_exchangeName;
	@Value("${rabbitmq.delay_mq.queueName}")
	private String delay_mq_queueName;
	@Value("${rabbitmq.delay_mq.routeKey}")
	private String delay_mq_routeKey;
	
	
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		RabbitQueueConfigService.setConf(0, new MQInfo(host, port, sms_mq_exchangeName, sms_mq_queueName, sms_mq_routeKey, qos));//0表示快速队列
		RabbitQueueConfigService.setConf(1, new MQInfo(host, port, delay_mq_exchangeName, delay_mq_queueName, delay_mq_routeKey, qos)); //1表示慢速队列
		
		cachMgrService.refreshCache();//此语句必须在上面的RabbitQueueConfigService.setConf语句之后
		
		
		smsRedisTools.initPool();
		
		try {
			//监听sms_mq
			logger.info("======= 开始监听sms_mq队列 ======");
			SmiSubscribeConsumer smsConsumer = new SmiSubscribeConsumer(sms2GateWaySender, smsRedisTools, host, port, sms_mq_exchangeName, sms_mq_queueName, sms_mq_routeKey, qos);
			smsConsumer.newConnect();
			smsConsumer.startListen();
			
			//监听delay_mq
			logger.info("======= 开始监听delay_mq队列 ======");
			SmiSubscribeConsumer delayConsumer = new SmiSubscribeConsumer(sms2GateWaySender, smsRedisTools, host, port, delay_mq_exchangeName, delay_mq_queueName, delay_mq_routeKey, qos);
			delayConsumer.newConnect();
			delayConsumer.startListen();
			
			//启动线程将Redis中的短信持久化到数据库中去
			smsPersistor.start();
			
		} catch(Exception e) {
			logger.error("监听mq失败", e);
			throw new RuntimeException(e);
		}
	}
}
