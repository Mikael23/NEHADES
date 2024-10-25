package com.nehades.services.databases;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.binary.BinaryMarshaller;
import org.apache.ignite.internal.client.thin.TcpIgniteClient;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class IgniteService {

    private static  String igniteServerAddress = "127.0.0.1:10800";

    public int createTable(String cacheName, String tableName, Map<String, String> fieldMap) throws ClassNotFoundException, SQLException {

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

//    client.query(new SqlFieldsQuery(String.format(
//            "CREATE TABLE IF NOT EXISTS Person (id INT PRIMARY KEY, name VARCHAR) WITH \"VALUE_TYPE=%s\"",
//                                    Person.class.getName())).setSchema("PUBLIC")).getAll();


    private void createTableFromMap(IgniteClient igniteClient, String schemaName, String tableName, Map<String, String> fields) {
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

    public void insertToIgniteWithJsonFields(Map<String, String> fields,String cacheName,String tableName) {
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
            cache.query(new SqlFieldsQuery(sql).setArgs(values)).getAll();
            System.out.println("Data inserted successfully into table: " + tableName);
            // Example of accessing or creating a cache
        } catch (Exception e) {
            System.err.println("Ignite connection failed: " + e.getMessage());
            e.printStackTrace();
        }


//        Ignite ignite, IgniteCache<Long, Object> cache, String tableName, Map<String, Object> fields
    }

    private void insertToIgniteWithSqlQuery(String query,String cacheName,String tableName) {
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