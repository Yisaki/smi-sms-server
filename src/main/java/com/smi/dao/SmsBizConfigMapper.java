package com.smi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.smi.model.SmsBizConfig;

@Mapper
public interface SmsBizConfigMapper {

	/**
	 * 查询所有业务配置数据
	 * 
	 */
	List<SmsBizConfig> listAll();

}