package com.zyy;

import com.mongodb.client.MongoDatabase;
import com.zyy.dao.UserDao;
import com.zyy.entity.User;
import com.zyy.util.MongoHelper;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:52 2019/6/13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestUserDao {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MongoHelper mongoHelper;

    @Test
    public void testInsert(){
        User user = new User("1","张三","男",18);
        userDao.insert(user);
    }

    @Test
    public void testInsertAll(){
        List<User> list=new ArrayList<>();
        User user1 = new User("2","lisi","女",20);
        User user2 = new User("3","王五","男",25);
        User user3 = new User("4","老六","男",22);
        User user4 = new User("5","张四","男",23);
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
        userDao.batchInsert(list);
    }

    @Test
    public void testQueryByDoc(){
        MongoDatabase mongoDatabase = mongoHelper.getMongoDatabase(mongoHelper.getMongoClient());
        Document document = new Document();
        document.put("name","王五");
        List<Map<String, Object>> queryByDoc = userDao.queryByDoc(mongoDatabase, "users", document);
        for (Map<String, Object> map : queryByDoc){
            for (Map.Entry entry : map.entrySet()){
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
        }
    }

    @Test
    public void testQueryByPage(){
        User user = new User();
        user.setName("张");
        List<User> userList = userDao.queryByPage(user, 1, 5);
        userList.stream().forEach(item -> System.out.println(item.toString()));
    }

    @Test
    public void testUpdate(){
        User user = new User();
        user.setName("张wu");
        user.setSex("女");
        user.setAge(20);
        user.setId("5");
        userDao.update(user);
    }

    @Test
    public void testDelete(){
        userDao.delete("3");
    }
}
