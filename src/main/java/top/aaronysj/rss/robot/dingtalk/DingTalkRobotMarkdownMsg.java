package top.aaronysj.rss.robot.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.aaronysj.rss.feed.sports.tencent.BasketballCacheUtil;
import top.aaronysj.rss.feed.sports.tencent.nba.NbaFeed;
import top.aaronysj.rss.robot.DingTalkMsgType;
import top.aaronysj.rss.robot.RobotMessage;
import top.aaronysj.rss.robot.RobotProperties;
import top.aaronysj.rss.utils.TimeUtils;

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
@Component("dingTalkMarkdownRobot")
public class DingTalkRobotMarkdownMsg implements RobotMessage, InitializingBean {

    private static final String ROBOT_API_PREFIX = "https://oapi.dingtalk.com/robot/send?access_token=%s";

    @Resource
    private RobotProperties robotProperties;

    @Resource(name = "robotThreadPool")
    private ThreadPoolExecutor robotThreadPool;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private BasketballCacheUtil basketballCacheUtil;

    @Scheduled(cron = "0 0/30 9-12 * * ?")
    public void getOffWorkReminder2() {
        int weekNumOfDate = TimeUtils.getWeekNumOfDate(new Date());
        if (weekNumOfDate == 0 || weekNumOfDate == 6) {
            return;
        }
        String markdown = basketballCacheUtil.getMarkdown(new Date());
        if (StringUtils.isEmpty(markdown)) {
            return;
        }
        robotThreadPool.submit(() -> this.send(markdown));
    }

    @Override
    public void send(String msg) {
        List<String> clockInRobots = robotProperties.getClockInRobots();
        if(isEmpty(clockInRobots)) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(DingTalkMsgType.markdown.name());
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("今日 NBA");
        markdown.setText(msg);
        request.setMarkdown(markdown);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setIsAtAll(false);
        request.setAt(at);

        clockInRobots.stream().skip(1).forEach(token -> {
            DingTalkClient client = new DefaultDingTalkClient(String.format(ROBOT_API_PREFIX, token));
            execute(client, request);
        });
    }

    private void execute(DingTalkClient client, OapiRobotSendRequest request) {
        try {
            client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.basketballCacheUtil = new BasketballCacheUtil(reactiveRedisTemplate, new NbaFeed());
    }
}
