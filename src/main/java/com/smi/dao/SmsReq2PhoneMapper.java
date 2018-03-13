package com.smi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.smi.model.Req2Phone;

@Mapper
public interface SmsReq2PhoneMapper {

	int batchSave(@Param("tbl")String tableName, @Param("list")List<Req2Phone> list);
	
	void createTable(@Param("tbl")String tableName);
}