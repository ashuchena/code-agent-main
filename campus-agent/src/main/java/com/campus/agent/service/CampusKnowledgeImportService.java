package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.agent.entity.CampusKnowledge;
import com.campus.agent.mapper.CampusKnowledgeMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CampusKnowledgeImportService {

    private static final int MAIN_QUESTION_COLUMN = 1;
    private static final int ANSWER_COLUMN = 2;
    private static final int CATEGORY_COLUMN = 0;
    private static final int FIRST_EXPAND_COLUMN = 3;
    private static final int LAST_EXPAND_COLUMN = 12;

    @Autowired
    private CampusKnowledgeService campusKnowledgeService;

    @Autowired
    private CampusKnowledgeMapper campusKnowledgeMapper;

    public Map<String, Object> importFromExcel(String path) throws IOException {
        File sourceFile = new File(path);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new IOException("Knowledge file not found: " + path);
        }

        if (alreadyImported(sourceFile.getName())) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("file", sourceFile.getAbsolutePath());
            result.put("rows", 0);
            result.put("importedQuestions", 0);
            result.put("skippedRows", 0);
            result.put("message", "Knowledge file already imported, skipped");
            return result;
        }

        DataFormatter formatter = new DataFormatter();
        int imported = 0;
        int rows = 0;
        int skipped = 0;

        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                String category = readCell(row.getCell(CATEGORY_COLUMN), formatter);
                String mainQuestion = readCell(row.getCell(MAIN_QUESTION_COLUMN), formatter);
                String answer = readCell(row.getCell(ANSWER_COLUMN), formatter);
                if (!StringUtils.hasText(mainQuestion) || !StringUtils.hasText(answer)) {
                    skipped++;
                    continue;
                }

                rows++;
                imported += saveQuestion(category, mainQuestion, answer, sourceFile.getName());
                for (int column = FIRST_EXPAND_COLUMN; column <= LAST_EXPAND_COLUMN; column++) {
                    String expandedQuestion = readCell(row.getCell(column), formatter);
                    if (StringUtils.hasText(expandedQuestion)) {
                        imported += saveQuestion(category, expandedQuestion, answer, sourceFile.getName());
                    }
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("file", sourceFile.getAbsolutePath());
        result.put("rows", rows);
        result.put("importedQuestions", imported);
        result.put("skippedRows", skipped);
        return result;
    }

    private boolean alreadyImported(String sourceFileName) {
        try {
            return campusKnowledgeMapper.selectCount(
                    new QueryWrapper<CampusKnowledge>().eq("source_file", sourceFileName)
            ) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private int saveQuestion(String category, String question, String answer, String sourceFile) {
        campusKnowledgeService.saveOrUpdateKnowledge(category, question, answer, "EXCEL_IMPORT", sourceFile);
        return 1;
    }

    private String readCell(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }
}
