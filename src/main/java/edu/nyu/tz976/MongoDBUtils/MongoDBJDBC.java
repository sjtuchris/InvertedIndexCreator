package edu.nyu.tz976.MongoDBUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MongoDBJDBC {
    public void getMongoCollection() {
        try {
            // Connect mongo
            MongoClient mongoClient = new MongoClient("localhost", 27017);

            // Connect db
            MongoDatabase mongoDatabase = mongoClient.getDatabase("WSE");
            System.out.println("Connect to database successfully");
//            mongoDatabase.createCollection("docIdContentTable");
            MongoCollection<Document> collection = mongoDatabase.getCollection("docIdContentTable");

            testSearchIndex(collection);
            System.out.println("Success");



        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void create(MongoCollection<Document> collection){
        List<String> data = new ArrayList<>();
        data.add("a");
        data.add("b");
        data.add("c");

        Document b1 = new Document("name", "000").
                append("info", data);
        Document b2 = new Document("name", "001").
                append("info", data);

        List<Document> documents = new ArrayList<Document>();
        documents.add(b1);
        documents.add(b2);

        collection.insertMany(documents);
        System.out.println("Inserted!");
    }

    public void testSearchIndex(MongoCollection<Document> collection) {
        Document searchObject = new Document("name", "000");
        collection.createIndex(Indexes.ascending("name"));

        Document doc = collection.find(searchObject).first();

        System.out.println(doc.toJson());



//        DBObject explainObject = MongoUtils.getCollection(DEFAULT_COLLECTION_NAME).find(searchObject).explain();
//        log.info("explainObject with Index--->"+explainObject);
    }
}
