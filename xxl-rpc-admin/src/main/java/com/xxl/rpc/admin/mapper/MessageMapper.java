package com.xxl.rpc.admin.mapper;

import com.xxl.rpc.admin.model.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
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

    /**
     * 查询有效消息
     *
     * @param msgTimeValidStart     有效消息开始时间，处于区间内才判定有效消息
     * @param msgTimeValidEnd       有效消息开始时间，处于区间内才判定有效消息
     * @param excludeMsgIds         已处理消息ID，避免重复处理
     * @return
     */
    public List<Message> queryValidMessage(@Param("msgTimeValidStart") Date msgTimeValidStart,
                                           @Param("msgTimeValidEnd") Date msgTimeValidEnd,
                                           @Param("excludeMsgIds") List<Long> excludeMsgIds);

    /**
     * 清理无效消息
     *
     * @param msgTimeValidStart 有效消息开始时间，处于区间内才判定有效消息
     * @param msgTimeValidEnd   有效消息开始时间，处于区间内才判定有效消息
     * @return
     */
    public int cleanMessageInValid(@Param("msgTimeValidStart") Date msgTimeValidStart,
                                   @Param("msgTimeValidEnd") Date msgTimeValidEnd);

}
