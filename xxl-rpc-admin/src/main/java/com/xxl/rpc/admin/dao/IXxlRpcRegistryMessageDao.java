package com.xxl.rpc.admin.dao;

import com.xxl.rpc.admin.core.model.XxlRpcRegistryMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IXxlRpcRegistryMessageDao {

    public int add(@Param("xxlRpcRegistryMessage") XxlRpcRegistryMessage xxlRpcRegistryMessage);

    public List<XxlRpcRegistryMessage> findMessage(@Param("excludeIds") List<Integer> excludeIds);

    public int cleanMessage(@Param("messageTimeout") int messageTimeout);

}
