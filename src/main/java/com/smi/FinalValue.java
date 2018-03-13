package com.smi;

public final class FinalValue {
	/**
	 * 大汉三通的行业短信对应的账号类型（此值必须与sms_account表中companytype为COMPANY_TYPE_DHST时的accounttype值对应）
	 */
	public static final int ACCOUNT_TYPE_DHST_HANGYE = 1;
	/**
	 * 大汉三通的营销短信对应的账号类型（此值必须与sms_account表中companytype为COMPANY_TYPE_DHST时的accounttype值对应）
	 */
	public static final int ACCOUNT_TYPE_DHST_YINGXIAO = 2;
	
	/**
	 * 短信通道编码类型：大汉三通的编号(此值必须与sms_account表中companytype值对应)
	 */
	public static final int COMPANY_TYPE_DHST = 1;
	
	/****************** code_dict表中type对应的值 start ******************************/
	/**
	 * 字典类型：使用短信服务的星美各渠道
	 */
	public static final int DICT_TYPE_CHANNEL = 1;
	
	/**
	 * 字典类型： 大汉三通短信提交返回的错误码
	 */
	public static final int DICT_TYPE_SEND_CODE_DHST = 3;
	/**
	 * 字典类型： 大汉三通短信状态报告错误码
	 */
	public static final int DICT_TYPE_REPORT_CODE_DHST = 4;
	
	/******************* code_dict表中type对应的值 end *****************************/
	
	/**
	 * 发送成功
	 */
	public static final String SEND_STATUS_SUECCESS = "0";
	/**
	 * 发送失败
	 */
	public static final String SEND_STATUS_FAIL = "1";
	
	
	
	public class RtnCode {
		
		/**
		 * 请求被正确接收
		 */
		public static final String RTN_REC_OK = "22";
		
		/**
		 * 拒绝（超过短信发送的限制条件/渠道编码不存在）
		 */
		public static final String SEND_STATUS_REJECT = "3";
		
	}
	
	/**
	 * 短信表表名的前缀
	 */
	public static final String SMS_LOG_TABLE_PREFIX = "sms_history_";
	
	/**
	 * 请求序列号与手机号对应关系表的表名的前缀
	 */
	public static final String SMS_REQ2PHONE_TABLE_PREFIX = "sms_req2phone_";
	
	/**
	 * 短信状态表表名的前缀
	 */
	public static final String SMS_REPORT_TABLE_PREFIX = "sms_report_";
	
	/**
	 * 业务系统每次提交上来的短信号码字段中可以包含的最大手机号数。大汉三通侧限制为500
	 */
	public static final int MAX_PHONES_PER_REQUEST = 200;
	
	
}
