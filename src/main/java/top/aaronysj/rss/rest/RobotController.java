package top.aaronysj.rss.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aaronysj.rss.robot.dingtalk.DingTalkRobotTextMsg;

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
    private DingTalkRobotTextMsg dingTalkRobotTextMsg;

    @PostMapping("/clockIn")
    public String doClockIn() {
        dingTalkRobotTextMsg.send("测试测试！");
        return "OK";
    }

}
