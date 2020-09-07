package com.example.entity;

import java.util.Date;

/**
 * @Author: zhouyuyang
 * @Date: 2020/8/28 11:02
 */
public class Person {
    private Integer pid;
    private String name;
    private Integer gender;
    private Date birthday;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    @Override
    public String toString() {
        return "Person{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                '}';
    }
}
