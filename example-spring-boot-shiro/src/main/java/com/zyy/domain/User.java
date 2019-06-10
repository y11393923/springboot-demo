package com.zyy.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 功能描述：用户表
 *
 */
@Data
public class User {

    /**
     * 字段描述：用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;
    /**
     * 字段描述：用户名
     */
    private String username;
    /**
     * 字段描述：密码
     */
    private String password;
    /**
     * 字段描述：年龄
     */
    private Integer age;

}
