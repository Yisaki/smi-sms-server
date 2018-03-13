package com.smi.mq;

import java.io.IOException;

import com.rabbitmq.client.MessageProperties;


public class SmiProducer extends SmiRabbitMQ {

	public SmiProducer(String host, Integer port, String exchange_name, String queueName, String routeKey, int qos) {
		super(host, port, exchange_name, queueName, routeKey, qos);
	}

	public void publish(Object msg) throws IOException {
		getChannel().basicPublish(getExchange_name(), getRouteKey(), MessageProperties.PERSISTENT_BASIC, serialize(msg));
	}
}
