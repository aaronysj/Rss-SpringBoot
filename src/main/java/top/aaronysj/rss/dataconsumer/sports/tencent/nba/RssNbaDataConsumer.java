package top.aaronysj.rss.dataconsumer.sports.tencent.nba;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.aaronysj.rss.dataconsumer.sports.tencent.TencentSportsUtil;
import top.aaronysj.rss.dto.JsonFeedDto;
import top.aaronysj.rss.feed.sports.tencent.TencentApiResultDto;
import top.aaronysj.rss.feed.sports.tencent.nba.TencentBallInfo;
import top.aaronysj.rss.utils.TimeUtils;

import java.util.*;
import java.util.stream.Collectors;

import static top.aaronysj.rss.common.CacheUtil.RSS_NBA_HISTORY;
import static top.aaronysj.rss.common.CacheUtil.getLastGameTimeKey;
import static top.aaronysj.rss.common.RssConstants.*;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/14/21
 */
@Component
@Slf4j
public class RssNbaDataConsumer {

    public static final String BREAK_LINE = "<br />";

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    private TencentSportsUtil tencentSportsUtil;

    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "rss"),
            exchange = @Exchange(value = "NBA", type = ExchangeTypes.TOPIC),
            key = "#",
            ignoreDeclarationExceptions = "true"),
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumerNbaData(String message) {
        if (StringUtils.isEmpty(message)) {
            return;
        }
        log.debug("nba data - {}", message);
        TencentApiResultDto tencentApiResultDto = JSONUtil.toBean(message, TencentApiResultDto.class);
        if (tencentApiResultDto == null) {
            return;
        }
        Map<String, List<TencentBallInfo>> data = tencentApiResultDto.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        data.entrySet().stream()
                .filter(entry -> !CollectionUtils.isEmpty(entry.getValue()))
                .forEach(entry -> {
                    JsonFeedDto jsonFeedDto = generateNbaJsonFeedDto();
                    JsonFeedDto.Item item = getItem(entry.getKey(), entry.getValue());
                    jsonFeedDto.setItems(Collections.singletonList(item));
                    reactiveRedisTemplate.opsForHash().put(RSS_NBA_HISTORY, entry.getKey(), JSONUtil.toJsonStr(jsonFeedDto)).block();
                });
        saveTodayLastGameTime(data);
    }

    private void saveTodayLastGameTime(Map<String, List<TencentBallInfo>> data) {
        String today = TimeUtils.dateFormat(new Date());
        List<TencentBallInfo> tencentBallInfos = data.get(today);
        if (CollectionUtils.isEmpty(tencentBallInfos)) {
            return;
        }
        String todayLastGameKey = getLastGameTimeKey(today);
        Optional<String> lastGameTime = reactiveRedisTemplate.opsForValue().get(todayLastGameKey).blockOptional();
        if (lastGameTime.isPresent()) {
            return;
        }
        String lastGameStartTime = tencentBallInfos.get(tencentBallInfos.size() - 1).getStartTime();
        reactiveRedisTemplate.opsForValue().set(todayLastGameKey, lastGameStartTime).block();
    }

    private JsonFeedDto generateNbaJsonFeedDto() {
        JsonFeedDto basketball = new JsonFeedDto();
        basketball.setTitle("NBA");
        basketball.setDescription("This is Why We Play");
        basketball.setHomePageUrl("https://nba.stats.qq.com/schedule");
        basketball.setFeedUrl("http://localhost:8080/rss/feed/nba.json");
        basketball.setIcon("https://mat1.gtimg.com/sports/nba/logo/1602/9.png");
        basketball.setFavicon("https://mat1.gtimg.com/www/icon/favicon2.ico");
        return basketball;
    }

    private JsonFeedDto.Item getItem(String date, List<TencentBallInfo> tencentNbaInfos) {
        StringBuilder contentBuilder = new StringBuilder();
        // 主要内容
        String content = tencentNbaInfos.stream()
                .map(this::getNbaGame)
                .collect(Collectors.joining(BREAK_LINE));

        contentBuilder.append(content)
                .append(BREAK_LINE)
                .append(BREAK_LINE);

        contentBuilder
                .append("👉🏻")
                .append(" <a href=\"https://nba.stats.qq.com/schedule\">schedule</a>")
                .append(" <a href=\"https://nba.stats.qq.com/standings\">standings</a>")
                .append(BREAK_LINE);

        contentBuilder
                .append("👉🏻")
                .append(" <a href=\"http://24zhiboba.com\">Welcome</a>")
                .append(" <a href=\"https://feisuzhibo.com\">to</a>")
                .append(" <a href=\"https://www.cnmysoft.com/\">Hangouts</a> ")
                .append(BREAK_LINE);
        // 加上十佳球
        contentBuilder.append("👉🏻")
                .append(" <a href=\"https://sports.qq.com/nbavideo/topsk/\">十佳球</a> ")
                .append(BREAK_LINE);
        contentBuilder.append("✌🏻").append(" <a href=\"https://github.com/aaronysj\">@aaronysj</a>")
                .append(BREAK_LINE);
        JsonFeedDto.Item item = new JsonFeedDto.Item();
        item.setId(date);
        item.setUrl("https://nba.stats.qq.com/schedule");
        item.setTitle(date + " 比赛概况");
        item.setContentHtml(contentBuilder.toString());
        item.setDatePublished(TimeUtils.dateFormat(new Date(), TimeUtils.UTC_TIME_PATTERN));
        return item;
    }

    private String getNbaGame(TencentBallInfo tencentNbaInfo) {
        // 这里其实分为好个字段处理
        // 1 （是否白嫖）开始时间 2 是否已结束（已结束；第4节 04:34） 3 客队头像 4 客队名称 5 客队比分 6 主队比分 7 主队名称 8 主队头像 9 集锦 10 数据 11 回放
        StringBuilder sb = new StringBuilder();
        String time = tencentNbaInfo.getStartTime().substring(11, 16);
        String mid = tencentNbaInfo.getMid().split(":")[1];
        // 比赛进展
        String matchPeriod = tencentSportsUtil.parseMatchPeriod(tencentNbaInfo);
        int leftGoal = Integer.parseInt(tencentNbaInfo.getLeftGoal());
        int rightGoal = Integer.parseInt(tencentNbaInfo.getRightGoal());
        String leftName = tencentNbaInfo.getLeftName();
        String rightName = tencentNbaInfo.getRightName();
        // 比赛结束颁发奖杯
        if (NUM_2.equals(tencentNbaInfo.getMatchPeriod())) {
            // 主队 win
            if (leftGoal < rightGoal) {
                rightName = " 🏆" + rightName;
            } else if (leftGoal > rightGoal) {
                // 客队 win
                leftName = leftName + "🏆 ";
            }
        }
        String video = NUM_1.equals(tencentNbaInfo.getLivePeriod()) ? "直播" : "集锦";
        boolean warriors = "勇士".equals(tencentNbaInfo.getLeftName()) || "勇士".equals(tencentNbaInfo.getRightName());
        // 勇士的比赛要加粗！
        String letsGo = warriors ? "🏀" : "";
        String free = NUM_0.equals(tencentNbaInfo.getIsPay()) ? "😎" : "";
        String connector = " vs ";
        String firstColor = "#993366";
        String secondColor = "##666633";
        sb.append(letsGo).append(free).append(time).append(" ").append(matchPeriod).append(" ")
//                            .append("<img style=\"width:36px; height: 36px;\" src=\"").append(TencentNbaInfo.getLeftBadge()).append("\" /> ")
                .append("<font color=").append(firstColor).append(">").append(leftName).append("</font>")
                .append(" ")
                .append(tencentNbaInfo.getLeftGoal())
                .append(connector)
                .append(tencentNbaInfo.getRightGoal())
                .append(" ")
                .append("<font color=").append(secondColor).append(">").append(rightName).append("</font>")
//                            .append(" <img style=\"width:36px; height: 36px;\" src=\"").append(TencentNbaInfo.getRightBadge()).append("\" />")
                .append(" <a href=\"").append(tencentNbaInfo.getWebUrl()).append("\">").append(video).append("</a>")
                .append(" <a href=\"https://nba.stats.qq.com/nbascore/?mid=").append(mid).append("\">数据</a>")
                .append(" <a href=\"").append(tencentNbaInfo.getWebUrl()).append("&replay=1").append("\">回放</a>");
        return sb.toString();
    }
}
