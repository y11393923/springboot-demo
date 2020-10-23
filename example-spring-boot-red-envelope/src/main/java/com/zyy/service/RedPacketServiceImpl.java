package com.zyy.service;

import com.zyy.entity.RedPacketInfo;
import com.zyy.entity.RedPacketRecord;
import com.zyy.repository.RedPacketInfoRepository;
import com.zyy.repository.RedPacketRecordRepository;
import com.zyy.util.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/14 11:45
 */
@Service
public class RedPacketServiceImpl implements RedPacketService  {
    @Autowired
    private RedPacketInfoRepository redPacketInfoRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedPacketRecordRepository redPacketRecordRepository;

    private final String RED_PACKET_NUMBER = "RedPacket:Number:%s";
    private final String RED_PACKET_AMOUNT = "RedPacket:Amount:%s";
    private final int SCALE = 100;
    private final String PATTERN = "0.00";

    @Override
    public String addRedPacket(RedPacketInfo redPacketInfo) {
        Date date = new Date();
        redPacketInfo.setCreateTime(date);
        redPacketInfo.setUpdateTime(date);
        redPacketInfo.setRemainingAmount(redPacketInfo.getTotalAmount());
        redPacketInfo.setRemainingPacket(redPacketInfo.getTotalPacket());
        redPacketInfo.setRedPacketId(SnowflakeIdUtils.nextId());
        redPacketInfoRepository.save(redPacketInfo);
        String numberKey = String.format(RED_PACKET_NUMBER, redPacketInfo.getRedPacketId());
        redisTemplate.opsForValue().set(numberKey, String.valueOf(redPacketInfo.getTotalPacket()));
        String amountKey = String.format(RED_PACKET_AMOUNT, redPacketInfo.getRedPacketId());
        redisTemplate.opsForValue().set(amountKey, String.valueOf((long)(redPacketInfo.getTotalAmount().floatValue() * SCALE)));
        return redPacketInfo.getRedPacketId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getRedPacket(String redPacketId, Integer uid) {
        String numberKey = String.format(RED_PACKET_NUMBER, redPacketId);
        if (redisTemplate.hasKey(numberKey)){
            int number = Integer.parseInt(redisTemplate.opsForValue().get(numberKey));
            if (number <= 0){
                return "红包抢光了";
            }
            redisTemplate.opsForValue().decrement(numberKey);
            String amountKey = String.format(RED_PACKET_AMOUNT, redPacketId);
            if (redisTemplate.hasKey(amountKey)){
                String amount = redisTemplate.opsForValue().get(amountKey);
                BigDecimal totalAmount = new BigDecimal(Float.parseFloat(amount) / SCALE);
                BigDecimal bigDecimal = totalAmount.divide(new BigDecimal(number), 2, RoundingMode.HALF_UP);
                DecimalFormat decimalFormat = new DecimalFormat(PATTERN);
                String value;
                do{
                    value = decimalFormat.format((Math.random() * (bigDecimal.floatValue() * 2)));
                }while (PATTERN.equals(value));
                BigDecimal randomAmount = number == 1 ? totalAmount : new BigDecimal(value);
                redisTemplate.opsForValue().decrement(amountKey, (long) (randomAmount.floatValue() * SCALE));
                RedPacketRecord redPacketRecord = new RedPacketRecord();
                redPacketRecord.setRedPacketId(redPacketId);
                redPacketRecord.setAmount(randomAmount);
                redPacketRecord.setUid(uid);
                Date date = new Date();
                redPacketRecord.setCreateTime(date);
                redPacketRecord.setUpdateTime(date);
                redPacketRecord.setNickName("abc");
                redPacketRecord.setImgUrl("http://www.baidu.com");
                redPacketRecordRepository.save(redPacketRecord);
                redPacketInfoRepository.modifyByRedPacketId(redPacketId, randomAmount.floatValue());
                return "抢到" + randomAmount + "元";
            }
        }
        return "没有红包";
    }
}
