package com.nehades.services.databases;

import com.nehades.services.databases.dto.ResponseDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface InsertSearchService {
    void insertToTrinoOrIgniteWithJsonFields(Map<String, String> fields, String cacheName, String tableName);
     int createTable(String cacheName, String tableName, Map<String, String> fieldMap);
     ResponseDto performSearch(String partnerId, String sql, List<String> tables) throws Exception;
    CompletableFuture<Void> updateRowAsync(String partnerId, List<String> listId, List<String> tables);

}
