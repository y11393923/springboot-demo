package com.zyy.controller;

import com.zyy.entity.RedPacketInfo;
import com.zyy.service.RedPacketService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/14 11:42
 */
@Api(tags = "红包接口")
@RestController
public class RedPacketController {
    @Autowired
    private RedPacketService redPacketService;

    @ApiOperation(value = "发红包接口", notes = "发红包接口", httpMethod = "POST",
            produces = MediaType.APPLICATION_JSON_VALUE, extensions = {
            @Extension(name = "version", properties = @ExtensionProperty(name = "since", value = "1.0"))})
    @PostMapping("addRedPacket")
    public String addRedPacket(@RequestBody RedPacketInfo redPacketInfo){
        return redPacketService.addRedPacket(redPacketInfo);
    }

    @ApiOperation(value = "抢红包接口", notes = "抢红包接口", httpMethod = "GET",
            produces = MediaType.APPLICATION_JSON_VALUE, extensions = {
            @Extension(name = "version", properties = @ExtensionProperty(name = "since", value = "1.0"))})
    @GetMapping("getRedPacket")
    public String getRedPacket(@RequestParam("redPacketId") @ApiParam("红包Id") String redPacketId,
                               @RequestParam("uid") @ApiParam("用户Id") Integer uid){
        return redPacketService.getRedPacket(redPacketId, uid);
    }

}
