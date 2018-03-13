package com.smi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.smi.model.CodeDict;

@Mapper
public interface CodeDictMapper {

	/**
	 * 查询所有的字典编码
	 * 
	 * @return 字典编码列表
	 */
	List<CodeDict> listAll();

}