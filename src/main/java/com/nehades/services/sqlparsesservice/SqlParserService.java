package com.nehades.services.sqlparsesservice;

import com.nehades.services.databases.MongoService;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SqlParserService {

    private final MongoService mongoService;

    public SqlParserService(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    public Map<String, Map<String, Map<String, Integer>>> parseSQLQuery(String sqlQuery) {
        Map<String, Map<String, Map<String, Integer>>> customerData = new HashMap<>();
        List<String> whereConditions = new ArrayList<>();

        try {
            // Initialize the SQL parser
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Statement statement = parserManager.parse(new StringReader(sqlQuery));

            // Check if the statement is a SELECT statement
            if (statement instanceof Select selectStatement) {
                PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

                // Extract select items
                List<String> selectFields = extractSelectFields(plainSelect.getSelectItems());
                System.out.println("Select Fields: " + selectFields);

                // Extract where conditions
                extractWhereConditions(plainSelect.getWhere(), whereConditions);
                System.out.println("Where Conditions: " + whereConditions);

            }

            Map<String, String> fields = extractFieldsFromConditions(whereConditions);

            // Extract customerID
            String customerID = extractCustomerID(fields);
            fields.remove("customerID");
            mongoService.executeMongoForQuery(customerID, fields);

        } catch (Exception e) {

        }
        return customerData;
    }

    private Map<String, String> extractFieldsFromConditions(List<String> conditions) {
        Map<String, String> fields = new HashMap<>();
        Pattern pattern = Pattern.compile("([\\w\\.]+)\\s*=\\s*'([^']*)'");
        for (String condition : conditions) {
            Matcher matcher = pattern.matcher(condition);
            while (matcher.find()) {
                fields.put(matcher.group(1), matcher.group(2));
            }
        }
        return fields;
    }

    // Method to extract customerID from fields
    private String extractCustomerID(Map<String, String> fields) {
        if (fields.containsKey("partnerID")) {
            return fields.get("partnerID");
        }
        return null;
    }

    private List<String> extractSelectFields(List<SelectItem> selectItems) {
        List<String> fields = new ArrayList<>();
        for (SelectItem item : selectItems) {
            if (item instanceof SelectExpressionItem) {
                fields.add(((SelectExpressionItem) item).getExpression().toString());
            }
        }
        return fields;
    }

    private void extractWhereConditions(Expression where, List<String> conditions) {
        if (where instanceof BinaryExpression binaryExpression) {
            // Recursively extract conditions for complex expressions
            extractWhereConditions(binaryExpression.getLeftExpression(), conditions);
            conditions.add(binaryExpression.toString());
            extractWhereConditions(binaryExpression.getRightExpression(), conditions);
        } else if (where instanceof Column column) {
            conditions.add(column.toString());
        }
    }

}
