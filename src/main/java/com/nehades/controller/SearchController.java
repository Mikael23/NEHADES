package com.nehades.controller;

import com.nehades.dto.QueryDto;
import com.nehades.services.databases.dto.ResponseDto;
import com.nehades.services.sqlparsesservice.SqlParserService;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private final SqlParserService sqlParserService;

    public SearchController(SqlParserService sqlParserService) {
        this.sqlParserService = sqlParserService;
    }

    @PostMapping("/_search")
    public Publisher<ResponseDto> search(@RequestBody QueryDto query) throws Exception {
        return sqlParserService.parseSQLQuery(query.query(),query.hierarchy(),query.partnerId());
    }
}
