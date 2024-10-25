package com.nehades.services.databases;

import com.github.vincentrussell.query.mongodb.sql.converter.MongoDBQueryHolder;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
public class SqlToMongoService {

    public void translate() throws ParseException {
        QueryConverter queryConverter = new QueryConverter.Builder().sqlString("select column1 from my_table where value NOT IN ('dcd')").build();
        MongoDBQueryHolder mongoDBQueryHolder = queryConverter.getMongoQuery();
        String collection = mongoDBQueryHolder.getCollection();
        Document query = mongoDBQueryHolder.getQuery();
        Document projection = mongoDBQueryHolder.getProjection();
        Document sort = mongoDBQueryHolder.getSort();
    }
}
