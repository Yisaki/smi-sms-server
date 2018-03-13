package com.smi.enums;

public enum SMS_REPORT_CODE_ENUM {
	REPORT_SUCCESS(0,"成功"),REPORT_SERVER_ERROR(1,"接口处理失败"),REPORT_GATEWAY_ERROR(2,"运营商网关失败");

	private int code;
	private String desc;
	
	SMS_REPORT_CODE_ENUM(int code,String desc){
		this.code=code;
		this.desc=desc;
	}
	
	public String desc(){
		return desc;
	}
	
	public int code()
	{
		return code;
	}
}
