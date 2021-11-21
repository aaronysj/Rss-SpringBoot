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
import top.aaronysj.rss.feed.sports.tencent.TencentApiResultDto;
import top.aaronysj.rss.feed.sports.tencent.nba.TencentBallInfo;
import top.aaronysj.rss.utils.TimeUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.aaronysj.rss.common.CacheUtil.ROBOT_NBA_MARKDOWN;
import static top.aaronysj.rss.common.RssConstants.*;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/14/21
 */
@Component
@Slf4j
public class RobotNbaDataConsumer {

    @Autowired
    private TencentSportsUtil tencentSportsUtil;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "robot"),
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
        String today = TimeUtils.dateFormat(new Date());
        List<TencentBallInfo> tencentBallInfos = data.get(today);
        if (CollectionUtils.isEmpty(tencentBallInfos)) {
            return;
        }
        updateTodayMarkdown(tencentBallInfos);
    }

    /**
     * 只更新今天的内容
     *
     * @param tencentNbaInfos nba
     */
    private void updateTodayMarkdown(List<TencentBallInfo> tencentNbaInfos) {
        String nbaGameContent = tencentNbaInfos.stream()
                .map(this::getNbaGameMarkdown)
                .collect(Collectors.joining("\n\n"));
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(nbaGameContent);
        appendLinks(contentBuilder);
        reactiveRedisTemplate.opsForValue().set(ROBOT_NBA_MARKDOWN, contentBuilder.toString()).block();
    }

    private void appendLinks(StringBuilder contentBuilder) {
        contentBuilder.append("\n\n");
        contentBuilder.append("👉🏻");
        buildUrl(contentBuilder, "schedule", "https://nba.stats.qq.com/schedule");
        contentBuilder.append(" ");
        buildUrl(contentBuilder, "standings", "https://nba.stats.qq.com/standings");
        contentBuilder.append("\n\n");
        contentBuilder.append("👉🏻");
        buildUrl(contentBuilder, "Welcome", "http://24zhiboba.com");
        contentBuilder.append(" ");
        buildUrl(contentBuilder, "to", "https://feisuzhibo.com");
        contentBuilder.append(" ");
        buildUrl(contentBuilder, "Hangouts", "https://www.cnmysoft.com/");
        contentBuilder.append("\n\n");
        contentBuilder.append("👉🏻");
        buildUrl(contentBuilder, "十佳球", "https://sports.qq.com/nbavideo/topsk/");
        contentBuilder.append("\n\n");
        contentBuilder.append("✌🏻");
        buildUrl(contentBuilder, "@aaronysj", "https://github.com/aaronysj");
    }

    private void buildUrl(StringBuilder sb, String name, String url) {
        sb.append("[").append(name).append("]").append("(").append(url).append(")");
    }

    public String getNbaGameMarkdown(TencentBallInfo tencentNbaInfo) {
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
                .append("  ")
                .append("[").append(video).append("]").append("(").append(tencentNbaInfo.getWebUrl()).append(")").append("  ")
                .append("[").append("数据").append("]").append("(").append("https://nba.stats.qq.com/nbascore/?mid=").append(mid).append(")").append("  ")
                .append("[").append("回放").append("]").append("(").append(tencentNbaInfo.getWebUrl()).append("&replay=1").append(")")
        ;
        return sb.toString();
    }
}
