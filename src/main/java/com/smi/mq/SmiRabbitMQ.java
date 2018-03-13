package com.smi.mq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SmiRabbitMQ {
	private String host;
	private Integer port;
	private String routeKey;
	private String queueName;
	private String exchange_name;
	private int qos;
	
	private Channel channel;
	private Connection connect;

	/**
	 * 
	 * @param host
	 * @param port
	 * @param exchange_name
	 * @param queueName
	 * @param routeKey
	 * @param qos 一次接受多少消息
	 */
	public SmiRabbitMQ(String host, Integer port, String exchange_name, String queueName, String routeKey, int qos) {
		this.host = host;
		this.port = port;
		this.exchange_name = exchange_name;
		this.queueName = queueName;
		this.routeKey = routeKey;
		this.qos = qos;
	}

	/**
	 * 新建一个mq连接
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void newConnect() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		if (port != null)
			factory.setPort(port);

		this.connect = factory.newConnection();

		channel = connect.createChannel();

		// 一次性接收
		channel.basicQos(qos);
		// 声明一个交换器
		channel.exchangeDeclare(exchange_name, "direct");
		// 声明一个持久化的队列
		channel.queueDeclare(queueName, true, false, false, null);
		// 将队列绑定到指定的交换器上的routekey
		channel.queueBind(queueName, exchange_name, routeKey);
	}

	public Channel getChannel() {
		return channel;
	}

	public Connection getConnect() {
		return connect;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public String getRouteKey() {
		return routeKey;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getExchange_name() {
		return exchange_name;
	}

	/**
	 * 序列化
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream byteDataStream = new ByteArrayOutputStream();
		ObjectOutputStream obs = new ObjectOutputStream(byteDataStream);
		obs.writeObject(obj);
		obs.close();
		return byteDataStream.toByteArray();
	}

	/**
	 * 反序列化
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object unserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteDataStream = new ByteArrayInputStream(data);
		ObjectInputStream ibs = new ObjectInputStream(byteDataStream);

		Object obj = ibs.readObject();
		ibs.close();
		return obj;
	}

	public void close() throws IOException, TimeoutException {
		if(channel == null)
			return;
		
	
		channel.close();
		connect.close();
	}
}
