package com.nehades.services.jsonparserservice;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nehades.services.databases.IgniteService;
import com.nehades.services.databases.MongoService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JsonParserService {

    private final MongoService mongoService;
    private final IgniteService igniteService;

    public JsonParserService(MongoService mongoService, IgniteService igniteService) {
        this.mongoService = mongoService;
        this.igniteService = igniteService;
    }

    public void createDocFromSqlQuery(String sqlQuery) {
        //here we should add function for extreacting partnerId from query
    }

    public Map<String, Integer> craeteDocFromJson(String json,String tableName) throws JsonProcessingException {
        // Initialize Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string
        JsonNode rootNode = objectMapper.readTree(json);

        // Create a map to store the extracted field paths and their values
        Map<String, String> fieldMap = new HashMap<>();

        // Traverse the JSON and populate the map
        traverseJsonAndCreateMap(rootNode, "", fieldMap);
        String customerID = (rootNode.get("partnerID").asText());
        // Print the resulting map
        System.out.println("Extracted fields and values:");
        fieldMap.forEach((key, value) -> System.out.println(key + " -> " + value));
        Map<String, Integer> fieldOccurrencesInMongo = mongoService.getFieldOccurrencesInMongo(customerID, fieldMap);
        //here I meanwhile make insert to ignite
        igniteService.insertToIgniteWithJsonFields(fieldMap,customerID,tableName);
        return fieldOccurrencesInMongo;
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
}