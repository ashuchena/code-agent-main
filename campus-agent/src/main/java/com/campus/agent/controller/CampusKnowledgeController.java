package com.campus.agent.controller;

import com.campus.agent.entity.CampusKnowledge;
import com.campus.agent.entity.CampusKnowledgeMissing;
import com.campus.agent.service.CampusKnowledgeImportService;
import com.campus.agent.service.CampusKnowledgeService;
import com.campus.agent.service.SystemConfigService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/campus-knowledge")
public class CampusKnowledgeController {

    @Autowired
    private CampusKnowledgeService campusKnowledgeService;

    @Autowired
    private CampusKnowledgeImportService campusKnowledgeImportService;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/query")
    public Result<CampusKnowledge> query(@RequestParam String question) {
        try {
            return Result.success(campusKnowledgeService.queryAndTrack(question));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询校园知识库失败: " + e.getMessage());
        }
    }

    @PostMapping("/import-xlsx")
    public Result<Map<String, Object>> importXlsx(@RequestParam String path) {
        try {
            return Result.success(campusKnowledgeImportService.importFromExcel(path));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入校园知识库失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        try {
            return Result.success(campusKnowledgeService.getStats());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取知识库统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/missing")
    public Result<List<CampusKnowledgeMissing>> missing(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            int safeLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 100);
            return Result.success(campusKnowledgeService.getTopMissing(safeLimit));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未命中问题失败: " + e.getMessage());
        }
    }

    @GetMapping("/config")
    public Result<Map<String, Object>> config() {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("schoolName", campusKnowledgeService.getSchoolName());
            result.put("autoLearnThreshold", campusKnowledgeService.getAutoLearnThreshold());
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取知识库配置失败: " + e.getMessage());
        }
    }

    @PostMapping("/config/school-name")
    public Result<Void> updateSchoolName(@RequestParam String schoolName) {
        try {
            if (schoolName == null || schoolName.trim().isEmpty()) {
                return Result.error("院校名称不能为空");
            }
            systemConfigService.saveOrUpdate(
                    SystemConfigService.SCHOOL_NAME_KEY,
                    schoolName.trim(),
                    "当前院校名称"
            );
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新院校名称失败: " + e.getMessage());
        }
    }
}
