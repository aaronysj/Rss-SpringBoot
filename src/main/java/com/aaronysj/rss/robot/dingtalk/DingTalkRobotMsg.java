package com.aaronysj.rss.robot.dingtalk;

import com.aaronysj.rss.robot.RobotMessage;
import com.aaronysj.rss.robot.RobotProperties;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/7/21
 */
@Component("dingTalkRobot")
public class DingTalkRobotMsg implements RobotMessage {

    private static final String ROBOT_API_PREFIX = "https://oapi.dingtalk.com/robot/send?access_token=%s";

    @Resource
    private RobotProperties robotProperties;

    @Resource(name = "robotThreadPool")
    private ThreadPoolExecutor robotThreadPool;

    @Scheduled(cron = "0 0 9,18 * * ?")
    public void workerReminder() {
        robotThreadPool.submit(this::send);
    }

    @Override
    public void send() {
        List<String> clockInRobots = robotProperties.getClockInRobots();
        if(isEmpty(clockInRobots)) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("小傻瓜，打卡了吗");
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
