package com.smi.mq;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.smi.beans.SMSRedisTools;
import com.smi.model.SmsHistory;
import com.smi.service.Sms2GateWaySender;

/**
 * 消费者
 *
 */
public class SmiSubscribeConsumer extends SmiRabbitMQ {
	private Logger logger = Logger.getLogger(this.getClass());
	private SMSRedisTools smsRedisTools;
	private Sms2GateWaySender smsSender;
	
	public SmiSubscribeConsumer(Sms2GateWaySender smsSender, SMSRedisTools redisTools, String host, Integer port, String exchange_name, String queueName, String routeKey, int qos) {
		super(host, port, exchange_name, queueName, routeKey, qos);
		this.smsSender = smsSender;
		this.smsRedisTools = redisTools;
	}
	
	
	public void startListen() throws IOException {
		Channel channel = getChannel();
		final DefaultConsumer consumer = new DefaultConsumer(channel) {
			
			ExecutorService executor = Executors.newFixedThreadPool(SmiSubscribeConsumer.this.getQos());
			
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
				
				executor.execute(new Runnable() {

					@Override
					public void run() {
						
						SmsHistory sms = null;
						try {
							sms = (SmsHistory)SmiRabbitMQ.unserialize(body);
						} catch (ClassNotFoundException | IOException e1) {
							logger.error("反序列化消息失败：" + e1.getMessage() + "(consumerTag=" + consumerTag + ",deliveryTag=" + envelope.getDeliveryTag() + ")");
							return;
						}
						
						smsSender.postToDHServer(sms);//此方法会修改sms中的sendTime、status、note等字段
						
						try {
							channel.basicAck(envelope.getDeliveryTag(), false); //不论处理短信成功与否，都向MQ确认
						} catch (IOException e) {
							logger.error("向MQ Ack短信（reqNo=" + sms.getReqNo() + "）时出错：" + e.getLocalizedMessage() + "(consumerTag=" + consumerTag + ",deliveryTag=" + envelope.getDeliveryTag() + ")");
						}
						
						smsRedisTools.saveSMS(sms);
					}
					
				});
			}
		};
		
		channel.basicConsume(getQueueName(), false, consumer);//非自动确认消息
	}

	
}
