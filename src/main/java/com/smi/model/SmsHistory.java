package com.smi.model;

import java.io.Serializable;
import java.util.Date;

public class SmsHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	/**
	 * 渠道
	 */
	private String channel;
	
	/**
	 * 提交短信方的ip地址
	 */
	private String senderIp;
	
	/**
     * 短信通道公司( 1-大汉三通  )
     */
    private Integer companyType;
	

	/**
	 * 对应短信通道公司下的账户类型，对于大汉三通来说， 1-行业短信账号       2-营销类短信账号
	 */
	private Integer accountType; 

	/**
	 * 序列号
	 */
	private String reqNo;

	/**
	 * 手机号码
	 */
	private String phoneNo;

	/**
	 * 短信内容
	 */
	private String content;
	
	/**
	 * 手机号码个数
	 */
	private Integer mobiCount;

	/**
	 * 状态编码：0：成功  1：失败 
	 * 当大汉三通响应提交短信的结果为成功时，此字段为0，当向大汉三通提交短信时出现exception或响应结果为失败时，此字段为1
	 */
	private String statusCode;

	/**
	 * 短信服务平台接收到当前短信的时间
	 */
	private Date createTime;

	/**
	 * 短信服务平台将短信发送到短信网关（如：大汉三通）的时间
	 */
	private Date sendTime;

	/**
	 * 备注
	 */
	private String note;
	
	
	/**
	 * 为提交短信出现exception时短信关联的手机号或大汉三通响应中的失败短信列表
	 */
	private String failedPhones;
	
	
	/**
	 * 短信所使用的短信签名，此签名由短信账号决定，如签名“星美生活”
	 */
	private String sign;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getMobiCount() {
		return mobiCount;
	}

	public void setMobiCount(Integer mobiCount) {
		this.mobiCount = mobiCount;
	}

	public Integer getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}

	public String getFailedPhones() {
		return failedPhones;
	}

	public void setFailedPhones(String failedPhones) {
		this.failedPhones = failedPhones;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}