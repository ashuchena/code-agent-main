package com.campus.agent.controller;

import com.campus.agent.entity.CampusKnowledge;
import com.campus.agent.entity.Knowledge;
import com.campus.agent.service.CampusKnowledgeService;
import com.campus.agent.service.KnowledgeService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 校园资讯控制器：知识库问答
 */
@RestController
@RequestMapping("/knowledge")
public class InfoController {

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private CampusKnowledgeService campusKnowledgeService;

    @GetMapping("/query")
    public Result<Knowledge> queryKnowledge(@RequestParam String question) {
        try {
            Knowledge knowledge = knowledgeService.queryKnowledge(question);
            return Result.success(knowledge);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询知识库失败：" + e.getMessage());
        }
    }

    @GetMapping("/campus-query")
    public Result<CampusKnowledge> queryCampusKnowledge(@RequestParam String question) {
        try {
            return Result.success(campusKnowledgeService.queryAndTrack(question));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询校园知识库失败：" + e.getMessage());
        }
    }
}
