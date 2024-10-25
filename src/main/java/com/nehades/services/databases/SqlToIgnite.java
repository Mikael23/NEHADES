package com.nehades.services.databases;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlToIgnite {

    private void initIgnite() {
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();

        // Configure a data region with persistence enabled.
        DataRegionConfiguration regionCfg = new DataRegionConfiguration();

        regionCfg.setPersistenceEnabled(true);
        regionCfg.setName("persistentDataRegion");

        // Apply the data region configuration to storage.
        storageCfg.setDefaultDataRegionConfiguration(regionCfg);

        // Ignite configuration.
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDataStorageConfiguration(storageCfg);
        Ignite ignite = Ignition.start(cfg);
        ignite.cluster().active(true);
        // Cache configuration with SQL enabled.
        CacheConfiguration<Integer, Object> cacheCfg = new CacheConfiguration<>("PersonCache");
        cacheCfg.setIndexedTypes(Integer.class, Object.class); // Enable SQL indexing for the Person class.

        // Create or get the cache.
        IgniteCache<Integer, Object> cache = ignite.getOrCreateCache(cacheCfg);

    }


    private void executeQuery(IgniteCache cache) {
        // Create SQL table (if it doesn't exist).
        cache.query(new SqlFieldsQuery(
                "CREATE TABLE IF NOT EXISTS Person (" +
                        "id INT PRIMARY KEY, " +
                        "name VARCHAR, " +
                        "age INT) " +
                        "WITH \"template=replicated\""
        )).getAll();
        // Insert data using SQL.
        cache.query(new SqlFieldsQuery("INSERT INTO Person (id, name, age) VALUES (?, ?, ?)")
                .setArgs(1, "John Doe", 30)).getAll();

        cache.query(new SqlFieldsQuery("INSERT INTO Person (id, name, age) VALUES (?, ?, ?)")
                .setArgs(2, "Jane Doe", 25)).getAll();

        // Query data using SQL.
        FieldsQueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery("SELECT id, name, age FROM Person"));

        // Display query results.
        for (List<?> row : cursor) {
            System.out.println("ID: " + row.get(0) + ", Name: " + row.get(1) + ", Age: " + row.get(2));
        }

        // Update data using SQL.
        cache.query(new SqlFieldsQuery("UPDATE Person SET age = ? WHERE id = ?")
                .setArgs(35, 1)).getAll();

        // Delete data using SQL.
        cache.query(new SqlFieldsQuery("DELETE FROM Person WHERE id = ?")
                .setArgs(2)).getAll();

        // Verify data after update and delete.
        FieldsQueryCursor<List<?>> updatedCursor = cache.query(new SqlFieldsQuery("SELECT id, name, age FROM Person"));
        for (List<?> row : updatedCursor) {
            System.out.println("After Update - ID: " + row.get(0) + ", Name: " + row.get(1) + ", Age: " + row.get(2));
        }

    }
}

// Define a Person class to be used with SQL.
//public static class Person implements java.io.Serializable {
//    private int id;
//    private String name;
//    private int age;
//
//    // Constructor, getters, and setters.
//    public Person(int id, String name, int age) {
//        this.id = id;
//        this.name = name;
//        this.age = age;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//}

