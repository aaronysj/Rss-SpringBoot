package com.aaronysj.rss.robot.dingtalk;

import com.aaronysj.rss.robot.RobotMessage;
import com.aaronysj.rss.robot.RobotProperties;
import com.aaronysj.rss.utils.TimeUtils;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/7/21
 */
@Component("dingTalkTextRobot")
public class DingTalkRobotTextMsg implements RobotMessage {

    private static final String ROBOT_API_PREFIX = "https://oapi.dingtalk.com/robot/send?access_token=%s";

    @Resource
    private RobotProperties robotProperties;

    @Resource(name = "robotThreadPool")
    private ThreadPoolExecutor robotThreadPool;

    @Scheduled(cron = "0 0 9 * * ?")
    public void workReminder() {
        int weekNumOfDate = TimeUtils.getWeekNumOfDate(new Date());
        if(weekNumOfDate == 0 || weekNumOfDate == 6) {
            // 周六周日 不上班哦
            return;
        }
        robotThreadPool.submit(() -> this.send("小傻瓜，上班打卡了吗 ^_^"));
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void getOffWorkReminder1() {
        int weekNumOfDate = TimeUtils.getWeekNumOfDate(new Date());
        if(weekNumOfDate == 0 || weekNumOfDate == 6) {
            // 周六周日 不上班哦
            return;
        }
        robotThreadPool.submit(() -> this.send("辛苦一天，别忘了下班打卡哦 ^-^"));
//        if (weekNumOfDate == 3 || weekNumOfDate == 5) {
//            // 周三周五 6点下班
//        }
    }

    @Scheduled(cron = "0 30 20 * * ?")
    public void getOffWorkReminder2() {
        int weekNumOfDate = TimeUtils.getWeekNumOfDate(new Date());
        if (weekNumOfDate == 1 || weekNumOfDate == 2 || weekNumOfDate == 4) {
            // 周一 周二 周四 8点半
            robotThreadPool.submit(() -> this.send("加班辛苦，别忘了下班打卡哦 ^-^"));
        }
    }

    @Override
    public void send(String msg) {
        List<String> clockInRobots = robotProperties.getClockInRobots();
        if(isEmpty(clockInRobots)) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(msg);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//        at.setAtMobiles(Arrays.asList("14705282855"));
        at.setIsAtAll(true);
//        at.setAtUserIds(Arrays.asList("109929","32099"));
        request.setAt(at);

        clockInRobots.forEach(token -> {
            DingTalkClient client = new DefaultDingTalkClient(String.format(ROBOT_API_PREFIX, token));
            execute(client, request);
//            robotThreadPool.submit(() -> execute(client, request));
        });
    }

    private void execute(DingTalkClient client, OapiRobotSendRequest request) {
        try {
            client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
