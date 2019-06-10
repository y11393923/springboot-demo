package com.zyy.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 功能描述：角色表
 *
 */
@Data
public class Role {

    /**
     * 字段描述：角色ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rid;
    /**
     * 字段描述：角色描述
     */
    private String description;
    /**
     * 字段描述：关键字
     */
    private String keyword;
    /**
     * 字段描述：名称
     */
    private String name;

}
