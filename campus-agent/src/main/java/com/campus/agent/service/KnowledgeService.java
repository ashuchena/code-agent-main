package com.campus.agent.service;

import com.campus.agent.entity.CampusKnowledge;
import com.campus.agent.entity.Knowledge;
import com.campus.agent.mapper.KnowledgeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 知识库服务
 */
@Service
public class KnowledgeService {

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private CampusKnowledgeService campusKnowledgeService;

    /**
     * 知识库模糊查询
     * @param question 问题
     * @return 答案
     */
    public Knowledge queryKnowledge(String question) {
        CampusKnowledge campusKnowledge = campusKnowledgeService.queryAndTrack(question);
        if (campusKnowledge != null) {
            Knowledge knowledge = new Knowledge();
            knowledge.setId(campusKnowledge.getId());
            knowledge.setQuestion(campusKnowledge.getQuestion());
            knowledge.setAnswer(campusKnowledge.getAnswer());
            knowledge.setType(campusKnowledge.getCategory());
            return knowledge;
        }
        return knowledgeMapper.selectByQuestionLike(question);
    }
}
