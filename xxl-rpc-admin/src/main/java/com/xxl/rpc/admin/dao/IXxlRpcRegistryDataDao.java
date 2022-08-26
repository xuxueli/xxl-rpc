package com.xxl.rpc.admin.dao;

import com.xxl.rpc.admin.core.model.XxlRpcRegistryData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IXxlRpcRegistryDataDao {


    public int refresh(@Param("xxlRpcRegistryData") XxlRpcRegistryData xxlRpcRegistryData);

    public int add(@Param("xxlRpcRegistryData") XxlRpcRegistryData xxlRpcRegistryData);


    public List<XxlRpcRegistryData> findData(@Param("env") String env,
                                             @Param("key") String key);

    public int cleanData(@Param("timeout") int timeout);

    public int deleteData(@Param("env") String env,
                          @Param("key") String key);

    public int deleteDataValue(@Param("env") String env,
                               @Param("key") String key,
                               @Param("value") String value);

    public int count();

}
