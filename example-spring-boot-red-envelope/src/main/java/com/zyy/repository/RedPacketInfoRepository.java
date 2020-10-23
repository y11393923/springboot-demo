package com.zyy.repository;

import com.zyy.entity.RedPacketInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/14 11:34
 */
public interface RedPacketInfoRepository extends JpaRepository<RedPacketInfo, Integer> {

    /**
     * 扣减红包
     * @return
     */
    @Modifying
    @Query(value = "update red_packet_info " +
            "set remaining_packet = remaining_packet - 1, remaining_amount = remaining_amount - :amount, update_time = now() " +
            "where red_packet_id = :redPacketId ", nativeQuery = true)
    int modifyByRedPacketId(@Param("redPacketId") String redPacketId,@Param("amount") Float amount);

}
