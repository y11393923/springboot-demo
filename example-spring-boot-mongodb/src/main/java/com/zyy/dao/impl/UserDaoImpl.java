package com.zyy.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.zyy.dao.UserDao;
import com.zyy.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * @create 2018/9/18
 * @since 1.0.0
 */
@Repository
public class UserDaoImpl implements UserDao {
    private static Logger log=LogManager.getLogger(UserDaoImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<User> queryByPage(User user, int pageIndex, int pageSize){
        Query query=new Query();
        query.skip((pageIndex-1)*pageSize);
        query.limit(pageSize);
        query.with(Sort.by(new Sort.Order(Sort.Direction.ASC,"_id")));
        if (StringUtils.isNotBlank(user.getName())){
            //模糊查询
            Pattern pattern = Pattern.compile("^.*" + user.getName() + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
        }
        if(StringUtils.isNotBlank(user.getSex())){
            //精确查询
            query.addCriteria(Criteria.where("sex").is(user.getSex()));
        }
        //范围查询    大于等于10并且小于等于30
        query.addCriteria(Criteria.where("age").lte(30).gte(10));
        List<User> userList = mongoTemplate.find(query,User.class);
        long count = mongoTemplate.count(query, User.class);
        log.info("执行查询=====pageIndex:"+pageIndex+",pageSize:"+pageSize);
        System.out.println("=================="+count);
        return userList;
    }

    @Override
    public boolean batchInsert(List<User> users) {
        boolean flag=true;
        try {
            mongoTemplate.insertAll(users);
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean insert(User user) {
        boolean flag=true;
        try {
            mongoTemplate.insert(user);
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean update(User user) {
        boolean flag=true;
        try {
            Query query = new Query(new Criteria("_id").is(user.getId()));
            Update update = new Update().set("age",user.getAge()).set("name",user.getName()).set("sex",user.getSex());
            UpdateResult updateResult = mongoTemplate.updateMulti(query, update, User.class);
            long count = updateResult.getModifiedCount();
            if (count <= 0){
                flag=false;
            }
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean delete(String id) {
        boolean flag=true;
        try {
            Query query = new Query(Criteria.where("id").is(id));
            mongoTemplate.remove(query,User.class);
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Map<String, Object>> queryByDoc(MongoDatabase mongoDB, String table, Document doc) {
        MongoCollection<Document> collection = mongoDB.getCollection(table);
        FindIterable<Document> iterable = collection.find(doc);
        List<Map<String,Object>> list=new ArrayList<Map<String, Object>>();
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()){
            Document next = cursor.next();
            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll(next);
            list.add(map);
        }
        System.out.println("查询完毕");
        return list;
    }

    @Override
    public boolean insert(MongoDatabase mongoDB, String table, Document doc) {
        MongoCollection<Document> collection=mongoDB.getCollection(table);
        collection.insertOne(doc);
        long count = collection.count(doc);
        if (count==1) {
            System.out.println("文档插入成功");
            return true;
        }else{
            System.out.println("文档插入失败");
            return false;
        }
    }

    @Override
    public boolean delete(MongoDatabase mongoDB, String table, Document doc) {
        MongoCollection<Document> collection = mongoDB.getCollection(table);
        DeleteResult deleteResult = collection.deleteOne(doc);
        long count = deleteResult.getDeletedCount();
        if (count == 1) {
            System.out.println("文档删除成功");
            return  true;
        }else{
            System.out.println("文档删除失败");
            return false;
        }
    }

    @Override
    public boolean update(MongoDatabase mongoDB, String table, BasicDBObject whereDoc, BasicDBObject updateDoc) {
        MongoCollection<Document> collection = mongoDB.getCollection(table);
        UpdateResult result = collection.updateOne(whereDoc, new Document("$set", updateDoc));
        long count = result.getModifiedCount();
        System.out.println(count+"个文档成功更新");
        if (count==0){
            System.out.println("文档更新失败");
            return false;
        }
        return true;
    }

    @Override
    public void dropCollection(MongoDatabase mongoDB, String table) {
        mongoDB.getCollection(table).drop();
        System.out.println("集合删除成功");
    }


}
