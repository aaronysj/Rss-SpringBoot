package top.aaronysj.rss.robot.dingtalk;

import org.junit.jupiter.api.Test;
import top.aaronysj.rss.feed.BaseTest;

import javax.annotation.Resource;

class DingTalkRobotTextMsgTest extends BaseTest {

    @Resource
    private DingTalkRobotTextMsg dingTalkRobotTextMsg;

    @Test
    void send() {
        dingTalkRobotTextMsg.send("测试测试");
    }
}