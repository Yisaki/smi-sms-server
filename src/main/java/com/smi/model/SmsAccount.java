package com.smi.model;

public class SmsAccount {
    private Integer id;
    
    /**
     * 短信通道公司( 1-大汉三通)
     */
    private Integer companyType;

    /**
     * 对应短信通道公司下的账户类型，对于大汉三通来说， 1-行业短信账号       2-营销类短信账号
     */
    private Integer accountType;

    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;

    /**
     * 接口地址
     */
    private String baseUrl;

    private String note;
    
    /**
     * 此字段保存账号对应的业务签名
     */
    private String sign;
    

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}