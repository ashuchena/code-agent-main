package com.campus.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.agent.entity.ChatHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    @Select(
        "SELECT * FROM (" +
        " SELECT * FROM chat_history" +
        " WHERE user_id = #{userId}" +
        " ORDER BY create_time DESC, id DESC" +
        " LIMIT #{limit}" +
        ") recent_history " +
        "ORDER BY create_time ASC, id ASC"
    )
    List<ChatHistory> selectRecentWindowByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    @Select(
        "SELECT * FROM chat_history " +
        "WHERE user_id = #{userId} " +
        "ORDER BY create_time ASC, id ASC"
    )
    List<ChatHistory> selectAllByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM chat_history WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}
