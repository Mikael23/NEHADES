package com.nehades.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nehades.services.databases.IgniteService;
import com.nehades.services.databases.SqlService;
import com.nehades.services.databases.TrinoService;
import com.nehades.services.databases.dto.HierarchyDto;
import com.nehades.services.databases.dto.IgniteTableDto;
import com.nehades.services.jsonparserservice.JsonParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class CreateDocumentController {
    private final JsonParserService jsonParserService;
    private final IgniteService igniteService;
    private final TrinoService trinoService;
    private final SqlService sqlService;

    public CreateDocumentController(JsonParserService jsonParserService, IgniteService igniteService, TrinoService trinoService, SqlService sqlService) {
        this.jsonParserService = jsonParserService;
        this.igniteService = igniteService;
        this.trinoService = trinoService;
        this.sqlService=sqlService;
    }

    @PostMapping("/create-doc")
    public ResponseEntity<Object> createDocument(@RequestBody String json, @RequestParam  String tableName, @RequestParam String partnerId, @RequestParam String hierarchy) throws JsonProcessingException {
        return jsonParserService.craeteDocFromJson(json,tableName,partnerId,hierarchy);
    }

    @PostMapping("/create-schema")
    public int createIgniteSchema(@RequestBody IgniteTableDto igniteTableDto) throws JsonProcessingException, SQLException, ClassNotFoundException {
        igniteTableDto.fieldMap().put("frequency","INTEGER");
        int igniteTable = igniteService.createTable(igniteTableDto.cacheName(), igniteTableDto.tableName(), igniteTableDto.fieldMap());
        int trinoTable = trinoService.createTable(igniteTableDto.cacheName(), igniteTableDto.tableName(), igniteTableDto.fieldMap());
        int table = sqlService.createTable(igniteTableDto.cacheName(), igniteTableDto.tableName(), igniteTableDto.fieldMap());
        return igniteTable + trinoTable + table;
    }
    @PostMapping("/create-hierarchy")
    public int createHierarchy(@RequestBody HierarchyDto hierarchyDto){
        return sqlService.createHierarchyWithFields(hierarchyDto.partnerId(),hierarchyDto.fieldsHierarchy(),hierarchyDto.source());
    }
}
