package com.example;

import com.example.entity.Person;
import com.example.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class ExampleSpringBootOracleApplicationTests {
    @Autowired
    private PersonMapper personMapper;

    @Test
    void selectAll() {
        List<Person> personList = personMapper.selectAll();
        for (Person person : personList) {
            System.out.println(person.toString());
        }
    }

    @Test
    void insert() {
        Person person = new Person();
        person.setPid(3);
        person.setName("abc");
        person.setGender(1);
        person.setBirthday(new Date());
        personMapper.insert(person);
    }

    @Test
    void update() {
        Person person = personMapper.findOne(3);
        person.setName("xxx");
        personMapper.update(person);
    }

    @Test
    void delete() {
        personMapper.delete(3);
    }

    @Test
    void selectAllByPage() {
        List<Person> personList = personMapper.selectAllByPage(1, 2);
        for (Person person : personList) {
            System.out.println(person.toString());
        }
    }

}
