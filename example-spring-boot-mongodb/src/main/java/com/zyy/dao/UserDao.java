package com.zyy.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.zyy.entity.User;
import org.bson.Document;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @create 2018/9/18
 * @since 1.0.0
 */
public interface UserDao {
    List<Map<String,Object>> queryByDoc(MongoDatabase mongoDB, String table, Document doc);

    boolean insert(MongoDatabase mongoDB, String table, Document doc);

    boolean delete(MongoDatabase mongoDB, String table, Document doc);

    boolean update(MongoDatabase mongoDB, String table, BasicDBObject whereDoc, BasicDBObject updateDoc);

    void dropCollection(MongoDatabase mongoDB, String table);

    List<User> queryByPage(User user, int pageIndex, int pageSize);

    boolean batchInsert(List<User> users);

    boolean insert(User user);

    boolean update(User user);

    boolean delete(String id);
}
