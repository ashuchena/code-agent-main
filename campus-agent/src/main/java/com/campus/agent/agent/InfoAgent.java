package com.campus.agent.agent;

import com.campus.agent.entity.Knowledge;
import com.campus.agent.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InfoAgent {

    @Autowired
    private KnowledgeService knowledgeService;

    public Knowledge queryInfo(String keywords) {
        return knowledgeService.queryKnowledge(keywords);
    }
}
