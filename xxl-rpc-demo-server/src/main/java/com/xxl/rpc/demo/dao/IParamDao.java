package com.xxl.rpc.demo.dao;

import com.xxl.rpc.demo.core.model.Param;

public interface IParamDao {
	
	public Param load(String key);
	
	public int save(Param param);

	public int update(Param param);
	
}
