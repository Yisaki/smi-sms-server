package com.smi.model;



public class Req2Phone {
	/**
	 * 请求序列号
	 */
	private String reqNo;
	
	/**
	 * 对应请求序列号的单个手机号（注：向大汉三通提交发送请求时，可以指定将同一短信发送给一批用户，这时，一个请求序列号就会对应多个手机号）
	 */
	private String phone;
	
	/**
	 * 是否发送失败：0成功，1失败
	 */
	private String failed = "0";
	
	
	public Req2Phone(String reqNo, String phone) {
		this.reqNo = reqNo;
		this.phone = phone;
	}
	
	public Req2Phone(String reqNo, String phone, String failed) {
		this.reqNo = reqNo;
		this.phone = phone;
		this.failed = failed;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFailed() {
		return failed;
	}

	public void setFailed(String failed) {
		this.failed = failed;
	}
	
	
	
}
