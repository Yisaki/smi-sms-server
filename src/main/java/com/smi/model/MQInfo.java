package com.smi.model;

public class MQInfo {
	private String host;
	private Integer port;
	private String exchangeName;
	private String routeKey;
	private String queueName;
	private Integer qos;
	
	public MQInfo(String host, Integer port, String exchange, String queue, String routKey, Integer qos) {
		this.host = host;
		this.port = port;
		this.exchangeName = exchange;
		this.queueName = queue;
		this.routeKey = routKey;
		this.qos = qos;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public String getRouteKey() {
		return routeKey;
	}

	public String getQueueName() {
		return queueName;
	}

	public Integer getQos() {
		return qos;
	}	
}
