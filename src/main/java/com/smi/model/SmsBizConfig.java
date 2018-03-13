package com.smi.model;

/**
 * 对调用短信服务平台的各业务系统的配置信息
 * 
 * @author Ryan Wu
 *
 */
public class SmsBizConfig {
	/**
	 * 提交短信的业务系统的ip
	 */
	private String ip;
	
	/**
	 * 业务系统提交的短信所关联的渠道号（由短信服务平台分配）。业务系统的ip和channel组成主键。
	 * 注：渠道号只是标识短信关联/对应的业务，并不代表此短信是由渠道号对应的业务系统发出来的。如：运营平台系统就可能发送不同渠道号的短信
	 */
	private String channel;
	
	/**
	 * ip+channel所唯一对应的业务使用的短信账号的id（为sms_account的外键）。有些短信走大汉三通的行业通道，有些短信走营销通道
	 */
	private int accountId;
	
	/**
	 * ip+channel所唯一对应的业务在短信服务平台中所使用的队列：0：快速队列，1：慢速队列
     * 注：实际上快速队列和慢速队列在实现上没有任何区别，只是约定短时间内要发送大量短信的业务走慢速队列
	 */
	private int queue;
	
	
	/**
	 * 配置说明
	 */
	private String note;
	

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getQueue() {
		return queue;
	}

	public void setQueue(short queue) {
		this.queue = queue;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setQueue(int queue) {
		this.queue = queue;
	}
}
