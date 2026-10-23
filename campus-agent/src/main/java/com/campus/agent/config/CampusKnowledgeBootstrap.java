package com.campus.agent.config;

import com.campus.agent.service.CampusKnowledgeImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class CampusKnowledgeBootstrap {

    private final CampusKnowledgeImportService campusKnowledgeImportService;

    @Value("classpath:knowledge/qa.xlsx")
    private Resource bundledKnowledgeFile;

    public CampusKnowledgeBootstrap(CampusKnowledgeImportService campusKnowledgeImportService) {
        this.campusKnowledgeImportService = campusKnowledgeImportService;
    }

    @PostConstruct
    public void bootstrap() {
        try {
            if (!bundledKnowledgeFile.exists()) {
                System.err.println("[CampusKnowledge] Bundled knowledge file not found");
                return;
            }

            File file = bundledKnowledgeFile.getFile();
            campusKnowledgeImportService.importFromExcel(file.getAbsolutePath());
            System.out.println("[CampusKnowledge] Bootstrap import completed: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[CampusKnowledge] Bootstrap import failed: " + e.getMessage());
        }
    }
}
