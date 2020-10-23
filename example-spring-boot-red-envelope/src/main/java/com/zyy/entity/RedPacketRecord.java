package com.zyy.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/14 11:19
 */
@Data
@Entity(name = "red_packet_record")
public class RedPacketRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "red_packet_id")
    private String redPacketId;
    private BigDecimal amount;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "img_url")
    private String imgUrl;
    private Integer uid;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
