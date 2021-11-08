package com.aaronysj.rss.rest;

import com.aaronysj.rss.robot.dingtalk.DingTalkRobotMsg;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/8/21
 */
@RestController
@RequestMapping("/robot")
public class RobotController {

    @Resource
    private DingTalkRobotMsg dingTalkRobotMsg;

    @PostMapping("/clockIn")
    public String doClockIn() {
        dingTalkRobotMsg.send();
        return "OK";
    }

}
