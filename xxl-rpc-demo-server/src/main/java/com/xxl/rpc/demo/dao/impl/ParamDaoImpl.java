package com.xxl.rpc.demo.dao.impl;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.xxl.rpc.demo.core.model.Param;
import com.xxl.rpc.demo.dao.IParamDao;

@Repository
public class ParamDaoImpl implements IParamDao {
	
	@Resource
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public Param load(String key) {
		return sqlSessionTemplate.selectOne("ParamMapper.load", key);
	}
	
	@Override
	public int save(Param param) {
		return sqlSessionTemplate.insert("ParamMapper.save", param);
	}
	
	@Override
	public int update(Param param) {
		return sqlSessionTemplate.update("ParamMapper.update", param);
	}
	
}
