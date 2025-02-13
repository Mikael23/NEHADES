package com.nehades.services.sqlparsesservice;

import com.nehades.services.databases.*;
import com.nehades.services.databases.dto.ResponseDto;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SqlParserService {

    private final MongoService mongoService;
    private final SqlService sqlService;
    private final TrinoService trinoService;
    private final IgniteService igniteService;


    public SqlParserService(MongoService mongoService, SqlService sqlService, TrinoService trinoService, IgniteService igniteService) {
        this.mongoService = mongoService;
        this.sqlService = sqlService;
        this.trinoService = trinoService;
        this.igniteService = igniteService;
    }

    public Publisher<ResponseDto> parseSQLQuery(String sqlQuery, List<String> hierarchy, String partnerId) throws Exception {
        Map<String, Map<String, Map<String, Integer>>> customerData = new HashMap<>();
        List<String> whereConditions = new ArrayList<>();
        List<String> tableNames = new ArrayList<>();
        Mono<Object> result = null;
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

            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            tableNames = tablesNamesFinder.getTableList(selectStatement);
            System.out.println("Table Names: " + tableNames);
        }

        Map<String, String> fields = extractFieldsFromConditions(whereConditions);
        List<String> combinations = generateCombinations(fields, hierarchy);

        List<String> finalTableNames = tableNames;

        ResponseDto responseDtoIgnite = null;
        ResponseDto responseDtoTrino = null;

        Mono<Set<Databases>> sqlResponse = Mono.fromCallable(() -> sqlService.getFromDatabasesResourcesType(combinations))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<ResponseDto> igniteResponseMono = Mono.fromCallable(() -> igniteService.performSearch(partnerId, sqlQuery, finalTableNames))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<ResponseDto> trinoResponse = Mono.fromCallable(() -> trinoService.performSearch(partnerId, sqlQuery, finalTableNames))
                .subscribeOn(Schedulers.boundedElastic());


        Mono<ResponseDto> responseDtoMono = Mono.zip(sqlResponse, igniteResponseMono)
                .flatMap(tuple -> {
                    Set<Databases> databases = tuple.getT1();
                    ResponseDto igniteResponse = tuple.getT2();

                    // If SQL result contains IGNITE, return Ignite's result immediately
                    if (databases.contains(Databases.IGNITE)) {
                        return Mono.just(igniteResponse);
                    }
                    // If SQL result contains TRINO, wait for Trino's result and return it
                    if (databases.contains(Databases.TRINO)) {
                        return trinoResponse;
                    }

                    // Default case: No specific database found, return Ignite's result
                    return Mono.just(igniteResponse);
                });


        // Extract customerID
        String customerID = extractCustomerID(fields);
        fields.remove("customerID");

//            mongoService.executeMongoForQuery(customerID, fields);


        return responseDtoMono;
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

    public static List<String> generateCombinations(Map<String, String> dataMap, List<String> hierarchy) {
        List<String> results = new ArrayList<>();

        // Create a queue for breadth-first combination generation
        Queue<String> combinationsQueue = new LinkedList<>();
        combinationsQueue.add(""); // Start with an empty combination

        // Process the hierarchy in order
        for (String hierarchyKey : hierarchy) {
            int queueSize = combinationsQueue.size();

            // Process all existing combinations in the queue
            for (int i = 0; i < queueSize; i++) {
                String currentCombination = combinationsQueue.poll();

                // Check if the current hierarchy key exists in the map
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    if (entry.getKey().toLowerCase().contains(hierarchyKey.toLowerCase())) {
                        // Add this value to the current combination
                        String newCombination = currentCombination.isEmpty()
                                ? entry.getValue()
                                : currentCombination + "," + entry.getValue();

                        combinationsQueue.add(newCombination);
                    }
                }
            }
        }

        // Collect all combinations into the results list
        results.addAll(combinationsQueue);
        return results;
    }

}
