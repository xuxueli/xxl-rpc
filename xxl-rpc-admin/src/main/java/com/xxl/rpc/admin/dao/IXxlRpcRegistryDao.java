package com.xxl.rpc.admin.dao;

import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IXxlRpcRegistryDao {

    public List<XxlRpcRegistry> pageList(@Param("offset") int offset,
                                         @Param("pagesize") int pagesize,
                                         @Param("biz") String biz,
                                         @Param("env") String env,
                                         @Param("key") String key);
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("biz") String biz,
                             @Param("env") String env,
                             @Param("key") String key);

    public XxlRpcRegistry load(@Param("biz") String biz,
                               @Param("env") String env,
                               @Param("key") String key);

    public XxlRpcRegistry loadById(@Param("id") int id);

    public int add(@Param("xxlRpcRegistry") XxlRpcRegistry xxlRpcRegistry);

    public int update(@Param("xxlRpcRegistry") XxlRpcRegistry xxlRpcRegistry);

    public int delete(@Param("id") int id);

}
