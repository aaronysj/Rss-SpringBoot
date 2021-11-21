package top.aaronysj.rss.feed.sports.tencent.cba;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.aaronysj.rss.dto.JsonFeedDto;
import top.aaronysj.rss.feed.FeedTask;
import top.aaronysj.rss.feed.sports.tencent.BasketballCacheUtil;
import top.aaronysj.rss.feed.sports.tencent.TencentApiResultDto;
import top.aaronysj.rss.feed.sports.tencent.nba.TencentBallInfo;
import top.aaronysj.rss.utils.FeedUrlUtils;
import top.aaronysj.rss.utils.TimeUtils;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * cba
 *
 * @author aaronysj
 * @date 10/4/21
 */
@Slf4j
@Component("cba")
public class CbaTask implements FeedTask, InitializingBean {

    @Autowired
    @Qualifier("feedThreadPool")
    private ThreadPoolExecutor feedPool;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private BasketballCacheUtil basketballCacheUtil;

    /**
     * 每5分钟实时更新今天的内容
     */
    @Scheduled(cron = "0 0/1 9-23 * * ? ")
    public void cbaTaskEvery5Min() {
        Date date = new Date();
        // 超过15点就别跑今天的数据了
        if (checkTodayGamesOver(date)) {
            return;
        }
        execute(date);
    }

    /**
     * 每天下午 23 点
     * 归档今天的内容
     * 更新明天的内容
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void cbaTaskAt15() {
        log.info("cbaTaskAt23");
        Date date = new Date();
        execute(date);

        Date tomorrow = TimeUtils.getDaysAfter(1);
        execute(tomorrow);
    }

    /**
     * 9 - 23 点 每15分钟执行
     */
    @Override
    public JsonFeedDto task(Date date) {
        // 生成 feed 的主信息
        JsonFeedDto basketball = generateCbaJsonFeedDto();
        // 生成当天的赛程信息
        Optional<JsonFeedDto.Item> optionalItem = getOptionalItem(date);
        optionalItem.ifPresent(item -> basketball.setItems(Collections.singletonList(item)));
        basketballCacheUtil.update(date, basketball);
        return basketball;
    }

    @Override
    public ThreadPoolExecutor getPool() {
        return feedPool;
    }

    /**
     * 判断今天的比较结束没有
     * 当前的小时 是否大于15
     *
     * @param date 当前时间
     * @return true 0 - 9, 23 - 24 点；false 9 - 23 点
     */
    private boolean checkTodayGamesOver(Date date) {
        // 今天最后的一场比赛已经结束
        String hour = TimeUtils.dateFormat(date, TimeUtils.HOUR_ONLY_PATTERN);
        if (hour.compareTo("09") < 0) {
            return true;
        }
        Optional<Date> todayLastGame = basketballCacheUtil.getTodayLastGame(date);
        if (todayLastGame.isPresent()) {
            Date gameOverTime = TimeUtils.plusHours(todayLastGame.get(), 3);
            if (date.compareTo(gameOverTime) > 0) {
                return true;
            }
        }
        // 今天最后的一场比赛已经结束
        return hour.compareTo("23") >= 0;
    }

    @Override
    public JsonFeedDto restAdaptor() {
        Date nowTime = new Date();
        // 超过下午 15 点就不实时拿腾讯数据了，直接去 redis 里拿
        if (checkTodayGamesOver(nowTime)) {
            return basketballCacheUtil.getLatest10Days();
        }
        // 取当天的 redis
        Optional<JsonFeedDto> todayFeed = basketballCacheUtil.get(nowTime);
        // 白天时间直接刷新
        return todayFeed.orElseGet(() -> task(nowTime));
    }

    @Override
    public void init() {
        TimeUtils.getLatest3Date().forEach(date -> {
            // 判断是否已经存在
            Optional<JsonFeedDto> jsonFeedDto = basketballCacheUtil.get(date);
            if (jsonFeedDto.isPresent()) {
                log.info("{} init passed", TimeUtils.dateFormat(date));
            } else {
                log.info("init task {}", TimeUtils.dateFormat(date));
                execute(date);
            }
        });
    }

    private Optional<JsonFeedDto.Item> getOptionalItem(Date nowTime) {
        String today = TimeUtils.dateFormat(nowTime);
        // 当天的日期
        String url = FeedUrlUtils.getCbaScheduleUrl(today, today);
        String body = HttpUtil.get(url, 2000);
        TencentApiResultDto tencentApiResultDto = JSONUtil.toBean(body, TencentApiResultDto.class);
        Map<String, List<TencentBallInfo>> data = tencentApiResultDto.getData();
        if (CollectionUtils.isEmpty(data)) {
            // 今天没有比赛
            basketballCacheUtil.updateTodayLastGameTime(nowTime, TimeUtils.dateFormat(new Date()) + " 00:00:00");
            return Optional.empty();
        }
        List<TencentBallInfo> cbaInfos = data.get(today);

        // 记录下当天最后一场比赛
        if (TimeUtils.dateFormat(new Date()).equals(TimeUtils.dateFormat(nowTime))) {
            String lastGameStartTime = cbaInfos.get(cbaInfos.size() - 1).getStartTime();
            Optional<Date> todayLastGame = basketballCacheUtil.getTodayLastGame(nowTime);
            if (!todayLastGame.isPresent()) {
                // 存下今天的最后一场比赛
                basketballCacheUtil.updateTodayLastGameTime(nowTime, lastGameStartTime);
            }
        }

        StringBuilder contentBuilder = new StringBuilder();
        // 主要内容
        String content = cbaInfos.stream()
                .map(cbaInfo -> {
                    // 这里其实分为好个字段处理
                    // 1 （是否白嫖）开始时间 2 是否已结束（已结束；第4节 04:34） 3 客队头像 4 客队名称 5 客队比分 6 主队比分 7 主队名称 8 主队头像 9 集锦 10 数据 11 回放
                    StringBuilder sb = new StringBuilder();
                    String time = cbaInfo.getStartTime().substring(11, 16);
                    String mid = cbaInfo.getMid().split(":")[1];
                    // 比赛进展
                    String matchPeriod = parseMatchPeriod(cbaInfo);
                    int leftGoal = Integer.parseInt(cbaInfo.getLeftGoal());
                    int rightGoal = Integer.parseInt(cbaInfo.getRightGoal());
                    String leftName = cbaInfo.getLeftName();
                    String rightName = cbaInfo.getRightName();
                    // 比赛结束颁发奖杯
                    if ("2".equals(cbaInfo.getMatchPeriod())) {
                        if (leftGoal < rightGoal) { // 主队 win
                            rightName = " 🏆" + rightName;
                        } else if (leftGoal > rightGoal) {
                            leftName = leftName + "🏆 "; // 客队 win
                        }
                    }
                    String video = "1".equals(cbaInfo.getLivePeriod()) ? "直播" : "集锦";
                    String free = "0".equals(cbaInfo.getIsPay()) ? "😎" : "";
                    String connector = " vs ";
                    String firstColor = "#993366";
                    String secondColor = "##666633";
                    sb.append(free).append(time).append(" ").append(matchPeriod).append(" ")
                            .append("<font color=").append(firstColor).append(">").append(leftName).append("</font>")
                            .append(" ")
                            .append(cbaInfo.getLeftGoal())
                            .append(connector)
                            .append(cbaInfo.getRightGoal())
                            .append(" ")
                            .append("<font color=").append(secondColor).append(">").append(rightName).append("</font>")
                            .append(" <a href=\"").append(cbaInfo.getWebUrl()).append("\">").append(video).append("</a>")
                            .append(" <a href=\"https://sports.qq.com/kbsweb/game.htm?mid=100008:").append(mid).append("\">数据</a>");
//                            .append(" <a href=\"").append(cbaInfo.getWebUrl()).append("&replay=1").append("\">回放</a>");
                    return sb.toString();
                })
                .collect(Collectors.joining("<br />"));

        contentBuilder.append(content)
                .append("<br />")
                .append("<br />")
                .append("👉🏻")
                .append(" <a href=\"http://24zhiboba.com\">Welcome</a>")
                .append(" <a href=\"https://feisuzhibo.com\">to</a>")
                .append(" <a href=\"https://www.cnmysoft.com/\">Hangouts</a> ")
                .append("<br />");

        contentBuilder.append("✌🏻").append(" <a href=\"https://github.com/aaronysj\">@aaronysj</a>")
                .append("<br />");
        JsonFeedDto.Item item = new JsonFeedDto.Item();
        item.setId(today);
        item.setUrl("https://kbs.sports.qq.com/#cba");
        item.setTitle(today + " 比赛概况");
        item.setContentHtml(contentBuilder.toString());
        item.setDatePublished(TimeUtils.dateFormat(nowTime, TimeUtils.UTC_TIME_PATTERN));
        return Optional.of(item);
    }

    private String parseMatchPeriod(TencentBallInfo tencentNbaInfo) {
        String matchPeriod = "未知";
        if ("0".equals(tencentNbaInfo.getMatchPeriod())) {
            matchPeriod = "未开始";
        } else if ("1".equals(tencentNbaInfo.getMatchPeriod())) {
            matchPeriod = tencentNbaInfo.getQuarter() + " " + tencentNbaInfo.getQuarterTime();
        } else if ("2".equals(tencentNbaInfo.getMatchPeriod())) {
            matchPeriod = "已结束";
        } else if ("3".equals(tencentNbaInfo.getMatchPeriod())) {
            matchPeriod = "比赛延期";
        }
        return matchPeriod;
    }

    private JsonFeedDto generateCbaJsonFeedDto() {
        JsonFeedDto basketball = new JsonFeedDto();
        basketball.setTitle("CBA");
        basketball.setDescription("This is Why We Play");
        basketball.setHomePageUrl("https://kbs.sports.qq.com/#cba");
        basketball.setFeedUrl("http://aaronysj.top:10443/rss/feed/nba.json");
        basketball.setIcon("https://mat1.gtimg.com/sports/nba/logo/1602/9.png");
        basketball.setFavicon("https://mat1.gtimg.com/www/icon/favicon2.ico");
        return basketball;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.basketballCacheUtil = new BasketballCacheUtil(reactiveRedisTemplate, new CbaFeed());
    }
}
