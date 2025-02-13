package com.nehades.services.databases;

import com.nehades.services.AbstractDatabaseOperations;
import com.nehades.services.databases.dto.ResponseDto;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class SqlService extends AbstractDatabaseOperations implements InsertSearchService {

    private String url;
    private Properties properties;
    private HikariDataSource ds;
    private final static String createCompositePerformanceTableQuery = "CREATE TABLE metadata.composite_performance_keys (id VARCHAR(255) PRIMARY KEY,hierarchy VARCHAR(255),source VARCHAR(255));";
    private final static String createStorePropertiesQuery = "CREATE TABLE metadata.store_properties (id VARCHAR(255) PRIMARY KEY,source VARCHAR(255));";
    private final static String insertToCompositePerformanceTableQuery = "INSERT INTO metadata.composite_performance_keys (id, hierarchy, source) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE hierarchy = VALUES(hierarchy), source = VALUES(source);";
    private final static String insertToStorePropertiesQuery = "INSERT INTO metadata.store_properties (id,source) VALUES (?, ?) ON DUPLICATE KEY UPDATE source = VALUES(source);";
    private final static String selectFromStorePropertiesQuery = "SELECT * FROM metadata.store_properties WHERE id IN (";
    private final static String createDatabaseSQL = "CREATE schema IF NOT EXISTS metadata;";
    private final static String insertToDatabaseHierarchy = "INSERT INTO metadata.store_properties (id, source) " +
            "SELECT '%s', 'IGNITE' " +
            "WHERE NOT EXISTS (" +
            "    SELECT 1 FROM metadata.store_properties WHERE id = '%s'" +
            ");";

    @Override
    public void insertToTrinoOrIgniteWithJsonFields(Map<String, String> fields, String cacheName, String tableName) {
        if (ds == null) {
            createPerformanceKeysTable();
        }
        int code = 200;
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(cacheName).append(".").append(tableName).append(" (");

        // Add column names
        StringBuilder placeholders = new StringBuilder();
        for (String fieldName : fields.keySet()) {
            sql.append(fieldName).append(", ");
            placeholders.append("?, ");
        }

        // Remove trailing comma and space, and complete SQL statement
        sql.setLength(sql.length() - 2);
        placeholders.setLength(placeholders.length() - 2);
        sql.append(") VALUES (").append(placeholders).append(");");

        // Connect to Aurora and execute the insert
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object value : fields.values()) {
                statement.setObject(index++, value); // Set each value in the prepared statement
            }

            statement.executeUpdate();
            System.out.println("Data inserted successfully into table " + cacheName + "." + tableName);

        } catch (SQLException e) {
            e.printStackTrace();
            code = 500;
        }

    }

    @Override
    public int createTable(String cacheName, String tableName, Map<String, String> fieldMap) {
        if (ds == null) {
            createPerformanceKeysTable();
        }
        int code = 200;
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(cacheName).append(".").append(tableName).append(" (");

        String value = null;
        String primaryKey = null;
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            value = entry.getValue();
            if (entry.getValue().contains("PRIMARY KEY")) {
                primaryKey = entry.getKey();
                value = entry.getValue().replace("PRIMARY KEY", "");
            }
            String fieldName = entry.getKey();

            sql.append(fieldName).append(" ").append(value).append(" (255), ");
        }
        sql.append("PRIMARY KEY(").append(primaryKey).append(")").append(", ");

        // Remove the last comma and space
        sql.setLength(sql.length() - 2);
        sql.append(");");
        String sqlCreateSchema = String.format("CREATE SCHEMA IF NOT EXISTS %s  ", cacheName);

        // Execute the SQL statement
        try (Connection connection = ds.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sqlCreateSchema);
            statement.execute(sql.toString());
            System.out.println("Table " + cacheName + "." + tableName + " created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            code = 500;
        }
        return code;
    }

    @Override
    public ResponseDto performSearch(String partnerId, String query, List<String> tables) throws SQLException {
        ResponseDto responseDto = null;
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                responseDto = extractResult(resultSet);
            }
        }
        return responseDto;
    }

    @Override
    public CompletableFuture<Void> updateRowAsync(String partnerId, List<String> listId, List<String> tables) {
        return null;
    }

    @PostConstruct
    public void createPerformanceKeysTable() {
        HikariConfig hikariConfig = new HikariConfig();
        String host = "localhost";
        String port = "3306";
        String database = "mydb";
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        hikariConfig.setJdbcUrl(this.url);
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("admin123");
        this.ds = new HikariDataSource(hikariConfig);

        try (Connection connection = ds.getConnection();
             Statement statement = connection.createStatement()) {
            boolean createSchema = statement.execute(createDatabaseSQL);
            boolean createCompositePerformanceTable = statement.execute(createCompositePerformanceTableQuery);
            boolean createStoreProperties = statement.execute(createStorePropertiesQuery);
            System.out.println("Table 'system.my_table' created successfully! " + createSchema + " " + createStoreProperties + " " + createStoreProperties);
        } catch (Exception e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public void insertToStoreProperties(String key) {
        String query = String.format(insertToDatabaseHierarchy, key, key);
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            boolean execute = statement.execute();
            System.out.println("RDS result " + execute);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public Set<Databases> getFromDatabasesResourcesType(List<String> keys) {
        String query = selectFromStorePropertiesQuery +
                String.join(",", keys.stream().map(id -> "?").toArray(String[]::new)) + ")";

        Set<Databases> results = new HashSet<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the IDs dynamically in the prepared statement
            for (int i = 0; i < keys.size(); i++) {
                preparedStatement.setString(i + 1, keys.get(i));
            }

            // Execute the query and process the results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(Databases.valueOf(resultSet.getString("source")));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

    public int createHierarchyWithFields(String partnerId, List<String> fields, Databases database) {
        int rowsAffected = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertToCompositePerformanceTableQuery)) {
            String fieldsSplitted = String.join(",", fields);
            statement.setString(1, partnerId);
            statement.setString(2, fieldsSplitted);
            statement.setString(3, database.name());
            rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " row(s) upserted successfully!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }


}
