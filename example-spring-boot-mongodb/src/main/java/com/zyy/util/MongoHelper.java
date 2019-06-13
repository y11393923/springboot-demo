package com.zyy.util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @create 2018/9/14
 * @since 1.0.0
 */
@Component
public class MongoHelper {

    @Value("${spring.data.mongodb.database}")
    private String dbName="test";
    @Value("${spring.data.mongodb.host}")
    private String serverAddress;
    @Value("${spring.data.mongodb.port}")
    private int port=27017;


    public MongoClient getMongoClient(){
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(serverAddress,port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase(MongoClient mongoClient){
        MongoDatabase mongoDatabase=null;
        try {
            if (mongoClient!=null) {
                mongoDatabase=mongoClient.getDatabase(dbName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mongoDatabase;
    }

    public void closeMongoClient(MongoDatabase mongoDatabase,MongoClient mongoClient){
        if (mongoDatabase!=null){
            mongoDatabase=null;
        }
        if (mongoClient!=null){
            mongoClient.close();
        }
    }
}
