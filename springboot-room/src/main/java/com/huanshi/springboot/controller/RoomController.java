package com.huanshi.springboot.controller;

import com.huanshi.springboot.common.Result;
import com.huanshi.springboot.component.WebSocketServer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Resource
    WebSocketServer webSocketServer;


    @ResponseBody
    @GetMapping("/link/{roomId}")
    public Result linkRoom(@PathVariable String roomId){
        return Result.success(webSocketServer.selectRoom(roomId));
    }
}
