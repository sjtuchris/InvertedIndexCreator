package edu.nyu.tz976.MongoDBUtils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtil {
    public static MongoDatabase connectDataBase() {
        // Connect mongo
        MongoClient mongoClient = new MongoClient("localhost", 27017);

        // Connect db
        return mongoClient.getDatabase("WSE");
    }

    public static void clearMongoDBTable() {
        MongoDatabase mongoDatabase = MongoDBUtil.connectDataBase();

        boolean collectionExists = mongoDatabase.listCollectionNames()
                .into(new ArrayList<String>()).contains("docIdContentTable");

        if (collectionExists) {
            mongoDatabase.getCollection("docIdContentTable").drop();
        }
    }
    public static MongoCollection<Document> getMongoCollection() {
        try {
            MongoDatabase mongoDatabase = MongoDBUtil.connectDataBase();

            // Check if collection already exist, if not create one
            boolean collectionExists = mongoDatabase.listCollectionNames()
                    .into(new ArrayList<String>()).contains("docIdContentTable");
            if (!collectionExists) {
                mongoDatabase.createCollection("docIdContentTable");
            }

            // Get collections
            return mongoDatabase.getCollection("docIdContentTable");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertRecord(MongoCollection<Document> collection, List<String> contentList, int docId){
        Document record = new Document("docId", docId).
                append("info", contentList);

        collection.insertOne(record);
    }

    public static Document getRecord(MongoCollection<Document> collection, int docId) {
        Document searchObject = new Document("docId", docId);

        return collection.find(searchObject).first();
    }

    public static void createMongoIndex() {
        try {
            MongoDBUtil.getMongoCollection().createIndex(Indexes.ascending("docId"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
