package com.nehades.services.jsonparserservice;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nehades.services.databases.IgniteService;
import com.nehades.services.databases.MongoService;
import com.nehades.services.databases.SqlService;
import com.nehades.services.databases.TrinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static com.nehades.services.sqlparsesservice.SqlParserService.generateCombinations;

@Service
public class JsonParserService {

    private final MongoService mongoService;
    private final IgniteService igniteService;
    private final TrinoService trinoService;
    private final SqlService sqlService;

    public JsonParserService(MongoService mongoService, IgniteService igniteService, TrinoService trinoService, SqlService sqlService) {
        this.mongoService = mongoService;
        this.igniteService = igniteService;
        this.trinoService = trinoService;
        this.sqlService = sqlService;
    }

    public void createDocFromSqlQuery(String sqlQuery) {
        //here we should add function for extreacting partnerId from query
    }

    public ResponseEntity<Object> craeteDocFromJson(String json, String tableName, String partnerId, String hierarchy) throws JsonProcessingException {
        // Initialize Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string
        JsonNode rootNode = objectMapper.readTree(json);

        // Create a map to store the extracted field paths and their values
        Map<String, String> fieldMap = new HashMap<>();

        // Traverse the JSON and populate the map
        traverseJsonAndCreateMap(rootNode, "", fieldMap);

        // Print the resulting map
        System.out.println("Extracted fields and values:");
        fieldMap.forEach((key, value) -> System.out.println(key + " -> " + value));
//        Map<String, Integer> fieldOccurrencesInMongo = mongoService.getFieldOccurrencesInMongo(partnerId, fieldMap);
        //here I meanwhile make insert to ignite
        igniteService.insertToTrinoOrIgniteWithJsonFields(fieldMap, partnerId, tableName);
        trinoService.insertToTrinoOrIgniteWithJsonFields(fieldMap, partnerId, tableName);
        String combinations = extractFields(fieldMap, hierarchy);
        sqlService.insertToStoreProperties(combinations);
        return ResponseEntity.ok().build();
    }

    // Recursive function to traverse JSON and add fields to the map
    public static void traverseJsonAndCreateMap(JsonNode node, String parentPath, Map<String, String> result) {
        if (node.isObject()) {
            // Traverse each field in the JSON object
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String fullPath = parentPath.isEmpty() ? fieldName : parentPath + "." + fieldName;
                JsonNode childNode = node.get(fieldName);

                // If the child node is a value, add it to the map
                if (childNode.isValueNode()) {
                    result.put(fullPath, childNode.asText());
                } else {
                    // If it's an object or array, recurse
                    traverseJsonAndCreateMap(childNode, fullPath, result);
                }
            }
        } else if (node.isArray()) {
            // Traverse array elements
            for (int i = 0; i < node.size(); i++) {
                traverseJsonAndCreateMap(node.get(i), parentPath + "[" + i + "]", result);
            }
        }
    }
    public static String extractFields(Map<String, String> fieldMap, String fields) {
        // Split the fields string into individual field names
        String[] fieldNames = fields.split(",");

        // Extract the values for the specified fields
        List<String> fieldValues = new ArrayList<>();
        for (String field : fieldNames) {
            // Trim whitespace and get the value from the map
            String value = fieldMap.get(field.trim());
            if (value != null) {
                fieldValues.add(value);
            }
        }

        // Join the extracted values with commas
        return String.join(",", fieldValues);
    }
}