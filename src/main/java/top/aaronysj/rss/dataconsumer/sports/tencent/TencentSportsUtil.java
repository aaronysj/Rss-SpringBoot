package top.aaronysj.rss.dataconsumer.sports.tencent;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import top.aaronysj.rss.feed.sports.tencent.nba.TencentBallInfo;
import top.aaronysj.rss.utils.TimeUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

import static top.aaronysj.rss.common.CacheUtil.getLastGameTimeKey;
import static top.aaronysj.rss.common.RssConstants.*;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/14/21
 */
@Component
public class TencentSportsUtil {

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public String parseMatchPeriod(TencentBallInfo gameInfo) {
        String matchPeriod = "未知";
        if (NUM_0.equals(gameInfo.getMatchPeriod())) {
            matchPeriod = "未开始";
        } else if (NUM_1.equals(gameInfo.getMatchPeriod())) {
            matchPeriod = gameInfo.getQuarter() + " " + gameInfo.getQuarterTime();
        } else if (NUM_2.equals(gameInfo.getMatchPeriod())) {
            matchPeriod = "已结束";
        } else if (NUM_3.equals(gameInfo.getMatchPeriod())) {
            matchPeriod = "比赛延期";
        }
        return matchPeriod;
    }

    public boolean isTodayLastGameOver() {
        Date date = new Date();
        Optional<Date> lastGameTime = reactiveRedisTemplate.opsForValue().get(getLastGameTimeKey(TimeUtils.dateFormat(date)))
                .blockOptional().map(x -> TimeUtils.dateParse(x, TimeUtils.DATE_TIME_PATTERN));
        if (lastGameTime.isPresent()) {
            Date gameOverTime = TimeUtils.plusHours(lastGameTime.get(), 3);
            return date.compareTo(gameOverTime) > 0;
        }
        return false;
    }
}
