package com.nehades.controller;

import com.nehades.dto.QueryDto;
import com.nehades.services.sqlparsesservice.SqlParserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SearchController {
    private final SqlParserService sqlParserService;

    public SearchController(SqlParserService sqlParserService) {
        this.sqlParserService = sqlParserService;
    }

    @PostMapping("/_search")
    public Map<String, Map<String, Map<String, Integer>>> search(@RequestBody QueryDto query) {
        return sqlParserService.parseSQLQuery(query.query());
    }
}
