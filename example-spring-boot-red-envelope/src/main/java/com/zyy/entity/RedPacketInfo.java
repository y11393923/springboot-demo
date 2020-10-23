package com.zyy.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/14 11:26
 */
@Data
@Entity(name = "red_packet_info")
public class RedPacketInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiModelProperty("红包id，采⽤timestamp+5位随机数")
    @Column(name = "red_packet_id")
    private String redPacketId;
    @ApiModelProperty("红包总⾦额")
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    @ApiModelProperty("红包总个数")
    @Column(name = "total_packet")
    private Integer totalPacket;
    @ApiModelProperty("剩余红包⾦额")
    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount;
    @ApiModelProperty("剩余红包个数")
    @Column(name = "remaining_packet")
    private Integer remainingPacket;
    @ApiModelProperty("新建红包⽤户的⽤户标识")
    private Integer uid;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
