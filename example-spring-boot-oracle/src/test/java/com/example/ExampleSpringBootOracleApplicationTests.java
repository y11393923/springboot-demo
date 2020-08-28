package com.example;

import com.example.entity.Person;
import com.example.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ExampleSpringBootOracleApplicationTests {
    @Autowired
    private PersonMapper personMapper;

    @Test
    void contextLoads() {
        List<Person> personList = personMapper.selectAll();
        for (Person person : personList) {
            System.out.println(person.toString());
        }
    }

}
