package com.xxl.rpc.admin.mapper;

import com.xxl.rpc.admin.model.entity.Instance;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* Instance Mapper
*
* Created by xuxueli on '2024-12-07 21:44:18'.
*/
@Mapper
public interface InstanceMapper {

    /**
    * 新增
    */
    public int insert(@Param("instance") Instance instance);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("instance") Instance instance);

    /**
    * Load查询
    */
    public Instance load(@Param("id") int id);

    /**
    * 分页查询Data
    */
	public List<Instance> pageList(@Param("offset") int offset,
                                   @Param("pagesize") int pagesize,
                                   @Param("appname") String appname,
                                   @Param("env") String env);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("appname") String appname,
                             @Param("env") String env);

}
