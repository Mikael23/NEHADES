//package com.nehades.services.databases;
//
//import org.apache.calcite.sql.SqlDialect;
//import org.apache.calcite.sql.SqlDialectFactoryImpl;
//import org.apache.calcite.sql.parser.SqlParseException;
//import org.apache.calcite.sql.parser.SqlParser;
//import org.apache.calcite.sql.SqlNode;
//import org.apache.calcite.sql.pretty.SqlPrettyWriter;
//
//
//public class CalciteService {
//
//    public void converterTest() throws SqlParseException {
//        String sql = "SELECT name, age FROM users WHERE age > 30";
//
//        // Parse the SQL
//        SqlParser parser = SqlParser.create(sql);
//        SqlNode sqlNode = parser.parseQuery();
//        // Define target dialect (e.g., PostgreSQL)
//        SqlDialect dialect = SqlDialectFactoryImpl
//                .INSTANCE
//                .create()
//                .INSTANCE
//                .create(SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(SqlDialect.DatabaseProduct.POSTGRESQL));
//
//        // Convert SQL to target dialect
//        SqlPrettyWriter writer = new SqlPrettyWriter(dialect);
//        writer.setIndentation(2);
//        writer.setSingleLine(true);
//        String translatedSql = writer.formatSql(sqlNode);
//
//        System.out.println("Translated SQL: " + translatedSql);
//
//        SqlParser parser1 = SqlParser.create("select * from users where age > 30");
//        SqlNode parsed = parser.parseQuery();
//        parsed.getParserPosition().
//    }
//}
