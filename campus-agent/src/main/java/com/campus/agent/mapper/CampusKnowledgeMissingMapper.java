package com.campus.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.agent.entity.CampusKnowledgeMissing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CampusKnowledgeMissingMapper extends BaseMapper<CampusKnowledgeMissing> {

    @Select("select * from campus_knowledge_missing where normalized_question = #{normalizedQuestion} limit 1")
    CampusKnowledgeMissing selectByNormalizedQuestion(@Param("normalizedQuestion") String normalizedQuestion);

    @Select("select * from campus_knowledge_missing order by hit_count desc, last_hit_at desc limit #{limit}")
    List<CampusKnowledgeMissing> selectTopMissing(@Param("limit") int limit);
}
