package com.aaronysj.rss.task;

import com.aaronysj.rss.config.Constants;
import com.aaronysj.rss.dto.AjaxResultDto;
import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.dto.TencentNBAInfo;
import com.aaronysj.rss.utils.GsonUtils;
import com.aaronysj.rss.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class NBATask {


    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    public RestTemplate restTemplate;

    /**
     * 每天 15 点定时跑
     */
    @Scheduled(cron = "0 0 15 * * ?")
//    @Scheduled(fixedRate = 1000000)
    public JsonFeedDto getTodayNBAGameInfo() {
        // 日期为空的话
        Date nowTime = new Date();
        String today = TimeUtils.dateFormat(nowTime, TimeUtils.DATE_PATTERN);
        String hour = TimeUtils.dateFormat(nowTime, TimeUtils.HOUR_ONLY_PATTERN);
        // 当天的日期
        JsonFeedDto basketball = new JsonFeedDto();
        basketball.setTitle("NBA");
        basketball.setDescription("This is Why We Play");
        basketball.setHome_page_url("https://nba.stats.qq.com/schedule");
        basketball.setFeed_url("http://localhost:8080/feed/nba.json");
        basketball.setIcon("https://mat1.gtimg.com/sports/nba/logo/1602/9.png");
        basketball.setFavicon("https://mat1.gtimg.com/www/icon/favicon2.ico");

        String url = "https://matchweb.sports.qq.com/kbs/list?from=NBA_PC&columnId=100000&startTime=" + today + "&endTime=" + today + "&from=sporthp";
        AjaxResultDto ajaxResultDto = restTemplate.getForObject(url, AjaxResultDto.class);
        Map<String, Object> dateMap = GsonUtils.jsonToMaps(GsonUtils.convertToString(ajaxResultDto.getData()));
        List<TencentNBAInfo> tencentNBAInfoList = GsonUtils.jsonToList(GsonUtils.convertToString(dateMap.get(today)), TencentNBAInfo.class);

        StringBuilder contentBuilder = new StringBuilder();
        // 主要内容
        String content = tencentNBAInfoList.stream()
                .map(tencentNBAInfo -> {
                    // 这里其实分为好个字段处理
                    // 1 （是否白嫖）开始时间 2 是否已结束（已结束；第4节 04:34） 3 客队头像 4 客队名称 5 客队比分 6 主队比分 7 主队名称 8 主队头像 9 集锦 10 数据 11 回放
                    StringBuilder sb = new StringBuilder();
                    String time = tencentNBAInfo.getStartTime().substring(11, 16);
                    String mid = tencentNBAInfo.getMid().split(":")[1];
                    // 比赛进展
                    String matchPeriod = "未知";
                    // 比赛是否已结束
                    boolean gameOver = false;
                    if("0".equals(tencentNBAInfo.getMatchPeriod())){
                        matchPeriod = "未开始";
                    }else if("1".equals(tencentNBAInfo.getMatchPeriod())) {
                        matchPeriod = tencentNBAInfo.getQuarter() + " " + tencentNBAInfo.getQuarterTime();
                    }else if("2".equals(tencentNBAInfo.getMatchPeriod())) {
                        matchPeriod = "已结束";
                        gameOver = true;
                    }else if("3".equals(tencentNBAInfo.getMatchPeriod())) {
                        matchPeriod = "比赛延期";
                    }
                    int leftGoal = Integer.parseInt(tencentNBAInfo.getLeftGoal());
                    int rightGoal = Integer.parseInt(tencentNBAInfo.getRightGoal());
                    String leftName = tencentNBAInfo.getLeftName();
                    String rightName = tencentNBAInfo.getRightName();
                    // 比赛结束颁发奖杯
                    if(gameOver) {
                        if(leftGoal < rightGoal) {
                            // 主队赢
                            rightName = " 🏆" + rightName;
                        }else if (leftGoal > rightGoal) {
                            leftName = tencentNBAInfo.getLeftName()+ "🏆 ";
                        }
                    }
                    String video = "集锦";
                    if("1".equals(tencentNBAInfo.getLivePeriod())) {
                        video = "直播";
                    }
                    boolean warriors = "勇士".equals(tencentNBAInfo.getLeftName()) || "勇士".equals(tencentNBAInfo.getRightName());
                    // 勇士的比赛要加粗！<span class="yangshi1">我被加粗</span>
                    String letsGo = "";
                    if(warriors) {
                        letsGo = "🏀";
                    }
                    String free = "";
                    if("0".equals(tencentNBAInfo.getIsPay())) {
                        free = "😎";
                    }
                    String connector = " vs ";

                    String firstColor = "#993366";
                    String secondColor = "##666633";
                    String thirdColor = "#666633";

                    sb.append(letsGo).append(free).append(time).append(" ").append(matchPeriod).append(" ")
//                            .append("<img style=\"width:36px; height: 36px;\" src=\"").append(tencentNBAInfo.getLeftBadge()).append("\" /> ")
                            .append("<font color=").append(firstColor).append(">").append(leftName).append("</font>")
                            .append(" ")
                            .append(tencentNBAInfo.getLeftGoal())
                            .append(connector)
                            .append(tencentNBAInfo.getRightGoal())
                            .append(" ")
                            .append("<font color=").append(secondColor).append(">").append(rightName).append("</font>")
//                            .append(" <img style=\"width:36px; height: 36px;\" src=\"").append(tencentNBAInfo.getRightBadge()).append("\" />")
                            .append(" <a href=\"").append(tencentNBAInfo.getWebUrl()).append("\">").append(video).append("</a>")
                            .append(" <a href=\"https://nba.stats.qq.com/nbascore/?mid=").append(mid).append("\">数据</a>")
                            .append(" <a href=\"").append(tencentNBAInfo.getWebUrl()).append("&replay=1").append("\">回放</a>");
//                            .append(" <a style=\"color:").append(thirdColor).append(";\" href=\" ").append(tencentNBAInfo.getWebUrl()).append("&replay=1").append("\">回放</a>");
                    return sb.toString();
                })
                .collect(Collectors.joining("<br />"));

        // 拼接一些尾部信息
        contentBuilder.append(content)
                .append("<br />")
                .append("<br />")
                .append("👉🏻")
                .append(" <a href=\"http://24zhiboba.com\">Welcome</a>")
                .append(" <a href=\"https://feisuzhibo.com\">to</a>")
                .append(" <a href=\"https://www.cnmysoft.com/\">Hangouts</a> ")
                .append("<br />");
        // 15点之后 加上十佳球
        if(hour.compareTo("15") >= 0) {
            contentBuilder.append("👉🏻")
                    .append(" <a href=\"https://sports.qq.com/nbavideo/topsk/\">十佳球</a> ")
                    .append("<br />");
        }
        contentBuilder.append("✌🏻").append(" <a href=\"https://github.com/aaronysj\">@aaronysj</a>")
                .append("<br />");

        JsonFeedDto.Item item = new JsonFeedDto.Item();
        item.setId(today);
        item.setUrl("https://nba.stats.qq.com/schedule");
        item.setTitle(today + " 比赛概况");
        item.setContent_html(contentBuilder.toString());

        item.setDate_published(TimeUtils.dateFormat(nowTime, TimeUtils.UTC_TIME_PATTERN));
        basketball.setItems(Collections.singletonList(item));
        if(hour.compareTo("15") >= 0) {
            // save to redis
            Long size = reactiveRedisTemplate.opsForList().size(Constants.NBA_HISTORY_KEY).block();
            // 当已经存了10天的之后，将最左的弹出
            if(size != null && size == 10) {
                reactiveRedisTemplate.opsForList().leftPop(Constants.NBA_HISTORY_KEY).block();
            }
            reactiveRedisTemplate.opsForList().rightPush(Constants.NBA_HISTORY_KEY, GsonUtils.convertToString(basketball)).block();
//            reactiveRedisTemplate.opsForValue().set(today, GsonUtils.convertToString(basketball)).block();
        }
        return basketball;
    }
}
