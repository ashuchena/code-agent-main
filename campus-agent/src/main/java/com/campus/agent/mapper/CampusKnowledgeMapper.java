package com.campus.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.agent.entity.CampusKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CampusKnowledgeMapper extends BaseMapper<CampusKnowledge> {

    @Select("select * from campus_knowledge where enabled = 1 and normalized_question = #{normalizedQuestion} limit 1")
    CampusKnowledge selectEnabledByNormalizedQuestion(@Param("normalizedQuestion") String normalizedQuestion);

    @Select("select * from campus_knowledge where normalized_question = #{normalizedQuestion} limit 1")
    CampusKnowledge selectAnyByNormalizedQuestion(@Param("normalizedQuestion") String normalizedQuestion);

    @Select("select * from campus_knowledge " +
            "where enabled = 1 and (" +
            "question like concat('%', #{keyword}, '%') " +
            "or normalized_question like concat('%', #{normalizedQuestion}, '%')" +
            ") order by hit_count desc, id asc limit #{limit}")
    List<CampusKnowledge> selectCandidates(@Param("keyword") String keyword,
                                           @Param("normalizedQuestion") String normalizedQuestion,
                                           @Param("limit") int limit);

    @Update("update campus_knowledge set hit_count = coalesce(hit_count, 0) + 1, updated_at = now() where id = #{id}")
    int incrementHitCount(@Param("id") Long id);
}
