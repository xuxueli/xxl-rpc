package com.xxl.rpc.admin.mapper;

import com.xxl.rpc.admin.model.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* XxlRpcMessage Mapper
*
* Created by xuxueli on '2024-12-07 15:34:24'.
*/
@Mapper
public interface MessageMapper {

    /**
    * 新增
    */
    public int insert(@Param("message") Message xxlRpcMessage);

    /**
    * 删除
    */
    public int delete(@Param("ids") List<Integer> ids);

    /**
    * 更新
    */
    public int update(@Param("message") Message xxlRpcMessage);

    /**
    * Load查询
    */
    public Message load(@Param("id") long id);

    /**
    * 分页查询Data
    */
	public List<Message> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize);

    /**
    * 分页查询Count
    */
    public int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize);

}
