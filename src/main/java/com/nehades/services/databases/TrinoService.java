package com.nehades.services.databases;

import com.nehades.services.AbstractDatabaseOperations;
import com.nehades.services.databases.dto.ResponseDto;
import io.trino.jdbc.TrinoDriver;
import jakarta.annotation.PostConstruct;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.internal.client.thin.TcpIgniteClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class TrinoService extends AbstractDatabaseOperations implements InsertSearchService {

    private String url;
    private Properties properties;

    @PostConstruct
    private void init() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", "user");
        this.url =  "jdbc:trino://trino-coordinator:8080" +
                "/" + "iceberg" + "/";
        this.properties = properties;
//        this.connection = DriverManager.getConnection(url, properties);
    }

    private Connection getConnection() {
        Connection connection=null;
        try {
            connection =  DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    @Override
    public void insertToTrinoOrIgniteWithJsonFields(Map<String, String> fields, String schemaName, String tableName) {
        Properties properties = new Properties();
        properties.setProperty("user", "user");
        String url = "jdbc:trino://trino-coordinator:8080" +
                "/" + "iceberg" + "/" + schemaName;
        StringBuilder sql = new StringBuilder("INSERT INTO iceberg." + schemaName + "." + tableName + " (");
        StringBuilder placeholders = new StringBuilder();

        for (String fieldName : fields.keySet()) {
            sql.append(fieldName).append(", ");
            placeholders.append("?, ");
        }

        // Remove the trailing commas
        sql.setLength(sql.length() - 2);
        placeholders.setLength(placeholders.length() - 2);

        sql.append(") VALUES (").append(placeholders).append(")");
        // Connect to Trino and execute the insert
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object value : fields.values()) {
                statement.setObject(index++, value);  // Dynamically set values in prepared statement
            }

            // Execute the statement
            statement.executeUpdate();
            System.out.println("Data inserted successfully into table " + tableName);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int createTable(String cacheName, String tableName, Map<String, String> fields) {
        Properties properties = new Properties();
        properties.setProperty("user", "user");
        String url = "jdbc:trino://trino-coordinator:8080" +
                "/" + "iceberg" + "/";

//        trinoClient.sendAdminQuery("CREATE SCHEMA IF NOT EXISTS iceberg." + TRINO_SCHEMA + " WITH (location = 's3a://h3data/" + TRINO_SCHEMA + "/')");

//        trinoClient.sendAdminQuery("CREATE SCHEMA IF NOT EXISTS iceberg." + TRINO_SCHEMA + " WITH (location = 's3a://h3data/" + TRINO_SCHEMA + "/')");

        String sqlCreateSchema = String.format("CREATE SCHEMA IF NOT EXISTS %s.%s with (location = 's3a://h3data/%s/')", "iceberg", cacheName, cacheName, cacheName);
        properties.setProperty("SSL", "false");

        StringJoiner columns = new StringJoiner(", ");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String value = entry.getValue();
            if (entry.getValue().contains("PRIMARY KEY")) {
                value = entry.getValue().replace("PRIMARY KEY", "");
            }
            columns.add(entry.getKey() + " " + value); // Add column name and its data type.
        }

        String sql = String.format("CREATE TABLE IF NOT EXISTS iceberg.%s.%s (%s) WITH (format = 'ORC')", cacheName, tableName, columns.toString());

        // Execute the SQL statement to create the table.
        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            System.out.println(sqlCreateSchema);
            statement.execute(sqlCreateSchema);
            System.out.println(sql);
//            trinoClient.sendAdminQuery("CREATE SCHEMA IF NOT EXISTS iceberg." + TRINO_SCHEMA + " WITH (location = 's3a://h3data/" + TRINO_SCHEMA + "/')");

            connection.createStatement().execute(sql);
            System.out.println("Table created successfully: " + tableName);
        } catch (Exception e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();

        }


        return 200;
    }

    @Override
    public ResponseDto performSearch(String partnerId, String query, List<String> tables) throws Exception {

        ResponseDto responseDto = null;
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            responseDto = extractResult(resultSet);
        }
        updateRowAsync(partnerId, responseDto.idList(), tables);
        return responseDto;
    }

    @Async
    @Override
    public CompletableFuture<Void> updateRowAsync(String partnerId, List<String> listId, List<String> tables) {
        return CompletableFuture.runAsync(() -> {
            String inClause = String.join(",", listId.stream().map(id -> "?").toList());
            tables.forEach(table -> {
                String sql = String.format("UPDATE iceberg.%s.%s SET %s = %s + 1 WHERE %s IN (%s)", partnerId, table, "frequency", "frequency", "_id", listId);
                try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
                    ResultSet resultSet = statement.executeQuery();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            });
        });
    }
}
