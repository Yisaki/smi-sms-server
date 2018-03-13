package com.smi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.smi.model.SmsAccount;

@Mapper
public interface SmsAccountMapper {

	/**
	 * 查询所有的记录
	 * 
	 * @return 记录列表
	 */
	List<SmsAccount> listAll();

}