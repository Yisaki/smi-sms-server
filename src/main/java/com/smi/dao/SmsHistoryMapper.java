package com.smi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.smi.model.SmsHistory;

@Mapper
public interface SmsHistoryMapper {

	int batchSave(@Param("tbl")String tableName, @Param("list")List<SmsHistory> list);
	
	void createTable(@Param("tbl")String tableName);
}