package com.smi.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smi.FinalValue;

@JsonSerialize
public class ReturnMsg {

	private String code = FinalValue.RtnCode.RTN_REC_OK;
	
	private String msg = "发送成功！";
	
	/**
	 * 短信的发送序列号
	 */
	private String reqNo;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}
	
	
}
