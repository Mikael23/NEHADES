package com.nehades.services.databases;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Properties;

@Service
public class SqlToElasticSearch {

    public void connectToElasticSearch() throws SQLException {
        String address = "jdbc:es://" + "elasticsearchAddress";
        Properties properties = new Properties();
        properties.put("user", "test_admin");
        properties.put("password", "x-pack-test-password");
        Connection connection = DriverManager.getConnection(address, properties);
        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     " SELECT name, page_count"
                             + "    FROM library"
                             + " ORDER BY page_count DESC"
                             + " LIMIT 1")) {
        }
    }
}
