package com.smi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SmsReportMapper {
	
	public int batchSave(@Param("tbl")String tableName, @Param("list")List<Map> list);
	
	
	public void createTable(@Param("tbl")String tableName);

}
