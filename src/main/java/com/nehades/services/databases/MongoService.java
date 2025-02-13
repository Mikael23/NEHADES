package com.nehades.services.databases;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.hibernate.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;


@Service
public class MongoService {

    private final MongoClient mongoClient;

    public MongoService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void executeMongoForQuery(String customerID, Map<String, String> fields) {
        // Get a collection
        MongoCollection<Document> collection = mongoClient.getDatabase("myDatabase").getCollection("QueryParameters");
        Document filter = new Document("partnerID", customerID);
        Document existingDocument = collection.find(filter).first();

        Map<String, Map<String, Integer>> fieldOccurrences;

        if (existingDocument != null) {
            fieldOccurrences = (Map<String, Map<String, Integer>>) existingDocument.get("fields_occurrences");
        } else {
            fieldOccurrences = new HashMap<>();
        }

        // Update field occurrences
        for (Map.Entry<String, String> field : fields.entrySet()) {
            String fieldName = field.getKey();
            String fieldValue = field.getValue();

            // Get or create field occurrences map
            Map<String, Integer> valueOccurrences = fieldOccurrences.getOrDefault(fieldName, new HashMap<>());

            // Increment occurrence count
            valueOccurrences.put(fieldValue, valueOccurrences.getOrDefault(fieldValue, 0) + 1);

            // Update field occurrences
            fieldOccurrences.put(fieldName, valueOccurrences);
        }

        // Prepare update document
        Document update = new Document("$set", new Document("fields_occurrences", fieldOccurrences));

        // Update or insert the document in MongoDB
        UpdateResult updateResult = collection.updateOne(filter, update, new UpdateOptions().upsert(true));
        System.out.printf("", updateResult.getModifiedCount());

        System.out.println("Updated field occurrences for customerID: " + customerID);
    }

    public Map<String, Integer> getFieldOccurrencesInMongo(String customerID, Map<String, String> fields) {
        Map<String, Integer> fieldOccurrences = new HashMap<>();
        MongoCollection<Document> collection = mongoClient.getDatabase("myDatabase").getCollection("QueryParameters");

        // Query to find the document by customerID
        Document query = new Document("partnerID", customerID);

        // Fetch the existing document for the customerID
        Document existingDocument = collection.find(query).first();

        // If the document exists, update occurrences; otherwise, create a new document
        if (existingDocument != null) {
            // Loop through the extracted fieldOccurrences
            for (Map.Entry<String, String> field : fields.entrySet()) {
                if (existingDocument.containsKey("fields_occurrences") && ((Document) existingDocument.get("fields_occurrences")).containsKey(field.getKey())) {
                    Object o = ((Document) ((Document) existingDocument.get("fields_occurrences")).get(field.getKey())).get(field.getValue());
                    fieldOccurrences.put(field.getKey()+"_"+field.getValue(), (Integer) o);
                }
            }
        }
        return fieldOccurrences;
    }


    public void updateAccessLogAsync(String partnerId, List<String> idList) {
        // Generate the key based on the current date
        String today = LocalDate.now().toString();

        MongoCollection<Document> collection = mongoClient
                .getDatabase("myDatabase")
                // Update the count for today's date, incrementing by 1
                .getCollection(partnerId);
        idList.forEach(id->{
            collection.updateOne(
                    Filters.eq("_id", id),                             // Match the document by car ID
                    Updates.inc("accessCounts." + today, 1),              // Increment the count for today's date
                    new com.mongodb.client.model.UpdateOptions().upsert(true)  // Upsert option to create if not exists
            );
        });
    }
}


