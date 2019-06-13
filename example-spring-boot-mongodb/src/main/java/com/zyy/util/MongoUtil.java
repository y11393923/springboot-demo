package com.zyy.util;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;


public enum MongoUtil {
    /**
     * 定义一个枚举的元素，它代表此类的一个实例
     */
    instance;

    private static MongoClient mongoClient;

    static {
        System.out.println("===============MongoDBUtil初始化========================");
        String ip = "192.168.1.75";
        int port =27017;
        instance.mongoClient = new MongoClient(ip, port);
        // 大部分用户使用mongodb都在安全内网下，但如果将mongodb设为安全验证模式，就需要在客户端提供用户名和密码：
        // boolean auth = db.authenticate(myUserName, myPassword);
        Builder options = new Builder();
        options.cursorFinalizerEnabled(true);
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(30000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.SAFE);//
        options.build();
    }

    // ------------------------------------共用方法---------------------------------------------------
    /**
     * 获取DB实例 - 指定DB
     *
     * @param dbName
     * @return
     */
    public MongoDatabase getDB(String dbName) {
        if (dbName != null && !"".equals(dbName)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }

    /**
     * 获取collection对象 - 指定Collection
     *
     * @param collName
     * @return
     */
    public MongoCollection<Document> getCollection(String dbName, String collName) {
        if (null == collName || "".equals(collName)) {
            return null;
        }
        if (null == dbName || "".equals(dbName)) {
            return null;
        }
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collName);
        return collection;
    }

    /**
     * 查询DB下的所有表名
     */
    public List<String> getAllCollections(String dbName) {
        MongoIterable<String> colls = getDB(dbName).listCollectionNames();
        List<String> _list = new ArrayList<String>();
        for (String s : colls) {
            _list.add(s);
        }
        return _list;
    }

    /**
     * 获取所有数据库名称列表
     *
     * @return
     */
    public MongoIterable<String> getAllDBNames() {
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }

    /**
     * 删除一个数据库
     */
    public void dropDB(String dbName) {
        getDB(dbName).drop();
    }

    /**
     * 查找对象 - 根据主键_id
     *
     * @param id
     * @return
     */
    public Document findById(MongoCollection<Document> coll, String id) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Document myDoc = coll.find(Filters.eq("_id", _idobj)).first();
        return myDoc;
    }

    /** 统计数 */
    public int getCount(MongoCollection<Document> coll) {
        int count = (int) coll.count();
        return count;
    }

    /** 条件查询 */
    public MongoCursor<Document> find(MongoCollection<Document> coll, Bson filter) {
        return coll.find(filter).iterator();
    }

    /** 分页查询 */
    public MongoCursor<Document> findByPage(MongoCollection<Document> coll, Bson filter, int pageNo, int pageSize) {
        Bson orderBy = new BasicDBObject("_id", 1);
        return coll.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }


    /**
     * 通过ID删除
     *
     * @param coll
     * @param id
     * @return
     */
    public int deleteById(MongoCollection<Document> coll, String id) {
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = coll.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }

    /**
     * FIXME
     *
     * @param coll
     * @param id
     * @param newdoc
     * @return
     */
    public Document updateById(MongoCollection<Document> coll, String id, Document newdoc) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        // coll.replaceOne(filter, newdoc); // 完全替代
        coll.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }

    public void dropCollection(String dbName, String collName) {
        getDB(dbName).getCollection(collName).drop();
    }

    /**
     * 关闭Mongodb
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * 测试入口
     *
     * @param args
     * @throws CloneNotSupportedException
     */
    public static void main(String[] args) {

        String dbName = "test";
        String collName = "wd_paper_scie";
        MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);
        //coll.createIndex(new Document("validata",1));//创建索引
        //coll.createIndex(new Document("id",1));
        // coll.createIndex(new Document("ut_wos",1),new IndexOptions().unique(true));//创建唯一索引
        //coll.dropIndexes();//删除索引
        //coll.dropIndex("validata_1");//根据索引名删除某个索引
        ListIndexesIterable<Document> list = coll.listIndexes();//查询所有索引
        for (Document document : list) {
            System.out.println(document.toJson());
        }
        coll.find(Filters.and(Filters.eq("x", 1), Filters.lt("y", 3)));
        coll.find(and(eq("x", 1), lt("y", 3)));
        coll.find().sort(ascending("title"));
        coll.find().sort(new Document("id",1));
        coll.find(new Document("$or", Arrays.asList(new Document("owner", "tom"), new Document("words", new Document("$gt", 350)))));
        coll.find().projection(fields(include("title", "owner"), excludeId()));
        MongoUtil.instance.close();
        // 插入多条

    }

}