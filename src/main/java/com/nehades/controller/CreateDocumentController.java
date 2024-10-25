package com.nehades.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nehades.services.databases.IgniteService;
import com.nehades.services.databases.dto.IgniteTableDto;
import com.nehades.services.jsonparserservice.JsonParserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;

@RestController
public class CreateDocumentController {
    private final JsonParserService jsonParserService;
    private final IgniteService igniteService;

    public CreateDocumentController(JsonParserService jsonParserService, IgniteService igniteService) {
        this.jsonParserService = jsonParserService;
        this.igniteService = igniteService;
    }

    @PostMapping("/create-doc")
    public Map<String, Integer> createDocument(@RequestBody String json, @RequestParam  String tableName) throws JsonProcessingException {
        return jsonParserService.craeteDocFromJson(json,tableName);
    }

    @PostMapping("/create-ignite-schema")
    public int createIgniteSchema(@RequestBody IgniteTableDto igniteTableDto) throws JsonProcessingException, SQLException, ClassNotFoundException {
        return igniteService.createTable(igniteTableDto.cacheName(),igniteTableDto.tableName(),igniteTableDto.fieldMap());
    }
}
