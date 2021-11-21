package top.aaronysj.rss.datasource.sports.tencent.nba;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.aaronysj.rss.dataconsumer.sports.tencent.TencentSportsUtil;
import top.aaronysj.rss.datasource.DataSource;
import top.aaronysj.rss.utils.FeedUrlUtils;
import top.aaronysj.rss.utils.TimeUtils;

import java.util.Date;

/**
 * nba 数据源 task
 *
 * @author aaronysj
 * @date 11/14/21
 */
@Component
@Slf4j
public class NbaDataTask implements DataSource, ApplicationRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    private TencentSportsUtil tencentSportsUtil;

    /**
     * 0 - 15 点 每分钟实时更新今天的内容
     * 如果今天比赛都已经结束，则跳过
     */
    @Scheduled(cron = "0 0/1 0-15 * * ?")
    public void nbaTaskEveryMin() {
        if (tencentSportsUtil.isTodayLastGameOver()) {
            return;
        }
        String today = TimeUtils.dateFormat(new Date());
        produceData(getTencentNbaInfo(today, today));
    }

    /**
     * 每天下午 15 点
     * 归档今天的内容
     * 更新明天的内容
     */
    @Scheduled(cron = "0 0 15 * * ?")
    public void nbaTaskAt15() {
        log.info("archive {} nba", TimeUtils.dateFormat(new Date()));
        Date today = new Date();
        Date tomorrow = TimeUtils.getDaysAfter(1);
        String data = getTencentNbaInfo(TimeUtils.dateFormat(today), TimeUtils.dateFormat(tomorrow));
        produceData(data);
    }

    private String getTencentNbaInfo(String startDate, String endDate) {
        String dataUrl = FeedUrlUtils.getNbaScheduleUrl(startDate, endDate);
        try {
            return HttpUtil.get(dataUrl, 2000);
        } catch (Exception e) {
            log.warn("getNbaScheduleException", e);
            return "";
        }
    }

    @Override
    public void produceData(String data) {
        if (StringUtils.isEmpty(data)) {
            return;
        }
        rabbitTemplate.convertAndSend("NBA", "#", data);
    }

    @Override
    public void init() {
        Date yesterday = TimeUtils.getDaysAfter(-1);
        Date tomorrow = TimeUtils.getDaysAfter(1);
        String data = getTencentNbaInfo(TimeUtils.dateFormat(yesterday), TimeUtils.dateFormat(tomorrow));
        produceData(data);
    }

    @Override
    public void run(ApplicationArguments args) {
        this.init();
    }
}
