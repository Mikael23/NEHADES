package com.nehades.services.databases;


import com.nehades.services.AbstractDatabaseOperations;
import com.nehades.services.databases.dto.ResponseDto;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.client.thin.TcpIgniteClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import java.util.Map;
import java.util.StringJoiner;

@Service
public class IgniteService extends AbstractDatabaseOperations implements InsertSearchService {

    private static String igniteServerAddress = "127.0.0.1:10800";
    private final SqlService sqlService;
    private final IgniteClient igniteClient;

    public IgniteService(SqlService sqlService) {
        super();
        this.sqlService = sqlService;
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(igniteServerAddress);
        igniteClient = TcpIgniteClient.start(cfg);
    }

    @Override
    public int createTable(String cacheName, String tableName, Map<String, String> fieldMap) {
        // Configure the client
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(igniteServerAddress);

        // Connect to the Ignite cluster
        try (IgniteClient igniteClient = TcpIgniteClient.start(cfg)) {
            System.out.println("Connected to Ignite!");

            // Example of accessing or creating a cache
            createTableFromMap(igniteClient, cacheName, tableName, fieldMap);
            System.out.println("Cache Value: " + cacheName);
        } catch (Exception e) {
            System.err.println("Ignite connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public ResponseDto performSearch(String partnerId, String sql, List<String> tables) throws Exception {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(igniteServerAddress);
        JSONArray jsonArray = new JSONArray();
        List<String> listId = new ArrayList<>();
        System.out.println("Connected to Ignite!");
        ClientCache<Object, Object> cache = igniteClient.getOrCreateCache(partnerId);
        FieldsQueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery(sql).setSchema("PUBLIC"));
        for (List<?> row : cursor) {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < cursor.getColumnsCount(); i++) {
                String value = (String) row.get(i);
                jsonObject.put(cursor.getFieldName(i), value);
                if (cursor.getFieldName(i).equalsIgnoreCase("_id")) {
                    listId.add(value);
                }
            }
            jsonArray.add(jsonObject);
        }


        updateRowAsync(partnerId, listId, tables);
        return new ResponseDto(jsonArray, listId);
    }

    @Override
    @Async
    public CompletableFuture<Void> updateRowAsync(String cacheName, List<String> ids, List<String> tables) {
        return CompletableFuture.runAsync(() -> {
            ClientConfiguration cfg = new ClientConfiguration().setAddresses(igniteServerAddress);
            try (IgniteClient igniteClient = TcpIgniteClient.start(cfg)) {
                ClientCache<Object, Object> cache = igniteClient.getOrCreateCache(cacheName);
                String inClause = String.join(",", String.join(",", ids));
                // Construct the SQL query
                tables.forEach(table -> {
                    String sql = String.format("UPDATE %s SET %s = %s + 1 WHERE %s IN (%s)", table, "frequency", "frequency", "_id", inClause);
                    SqlFieldsQuery query = new SqlFieldsQuery(sql).setSchema("PUBLIC");
                    cache.query(query);
                });
            }
        });
    }

    private void createTableFromMap(IgniteClient igniteClient, String schemaName, String
            tableName, Map<String, String> fields) {
        StringBuilder sql = new StringBuilder();

        // Start building the SQL CREATE TABLE statement
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        // Append each field and its type to the SQL statement
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            sql.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
        }

        // Remove the trailing comma and space
        sql.setLength(sql.length() - 2);

        // Complete the SQL statement
        sql.append(") WITH \"template=partitioned\"");

        // Execute the SQL query to create the table
        executeSql(igniteClient, sql.toString(), schemaName);
        System.out.println("Table created with fields: " + fields);
    }

    // Utility function to execute SQL without returning results
    private static void executeSql(IgniteClient igniteClient, String sql, String cacheName) {
        ClientCache<Object, Object> cache = igniteClient.getOrCreateCache(cacheName);
        cache.query(new SqlFieldsQuery(sql).setSchema("PUBLIC")).getAll();
    }

    @Override
    public void insertToTrinoOrIgniteWithJsonFields(Map<String, String> fields, String cacheName, String
            tableName) {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(igniteServerAddress);

        // Connect to the Ignite cluster
        try (IgniteClient igniteClient = TcpIgniteClient.start(cfg)) {
            System.out.println("Connected to Ignite!");
            ClientCache<Object, Object> cache = igniteClient.getOrCreateCache(cacheName);
            StringJoiner columns = new StringJoiner(", ");
            StringJoiner valuesPlaceholder = new StringJoiner(", ");

            // Collect the values to insert in the SQL query.
            Object[] values = new Object[fields.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                columns.add(entry.getKey()); // Add column names to the query.
                valuesPlaceholder.add("?"); // Add placeholders for values.
                values[i++] = entry.getValue(); // Add values to the array.
            }

            // Create the dynamic SQL query.
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columns.toString(), valuesPlaceholder.toString());

            // Execute the query with the provided values.
            cache.query(new SqlFieldsQuery(sql).setSchema("PUBLIC").setArgs(values)).getAll();
            System.out.println("Data inserted successfully into table: " + tableName);
            // Example of accessing or creating a cache
        } catch (Exception e) {
            System.err.println("Ignite connection failed: " + e.getMessage());
            e.printStackTrace();
        }


//        Ignite ignite, IgniteCache<Long, Object> cache, String tableName, Map<String, Object> fields
    }

    private void insertToIgniteWithSqlQuery(String query, String cacheName, String tableName) {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(igniteServerAddress);
        try (IgniteClient igniteClient = TcpIgniteClient.start(cfg)) {
            System.out.println("Connected to Ignite!");
            ClientCache<Object, Object> cache = igniteClient.getOrCreateCache(cacheName);
            cache.query(new SqlFieldsQuery(query)).getAll();
            System.out.println("Data inserted successfully into table: " + tableName);
        }
    }


}


//
//CREATE TABLE PUBLIC.auditlog_audit_tmp (
//        _del TIMESTAMP,
//        _index VARCHAR,
//        _type VARCHAR,
//        _id VARCHAR PRIMARY KEY,
//        _dayofmonth INT,
//        _hourofday INT,
//        _source VARCHAR,
//        _ttl INT,
//        _version BIGINT,
//        _timestamp TIMESTAMP,
//        siteid VARCHAR,
//        partnerid VARCHAR,
//        endpoint VARCHAR,
//        callid VARCHAR,
//        errcode INT,
//        errmessage VARCHAR,
//        uid VARCHAR,
//        authtype VARCHAR,
//        ip VARCHAR,
//        httpreq_sdk VARCHAR,
//        params_x_provider VARCHAR,
//        params_uid VARCHAR,
//        params_loginid VARCHAR,
//        userkey VARCHAR,
//        apikey VARCHAR
//) WITH "template=partitioned, backups=1, CACHE_NAME=auditlogAuditTmpCache";