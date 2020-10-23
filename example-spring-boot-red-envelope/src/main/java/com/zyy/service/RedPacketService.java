package com.zyy.service;

import com.zyy.entity.RedPacketInfo;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/14 11:44
 */
public interface RedPacketService {
    /**
     * 发红包
     * @param redPacketInfo
     */
    String addRedPacket(RedPacketInfo redPacketInfo);


    /**
     * 抢红包
     * @param redPacketId
     * @param uid
     * @return
     */
    String getRedPacket(String redPacketId, Integer uid);
}
