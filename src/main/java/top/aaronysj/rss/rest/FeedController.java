package top.aaronysj.rss.rest;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aaronysj.rss.common.CacheUtil;
import top.aaronysj.rss.dataconsumer.sports.tencent.TencentSportsUtil;
import top.aaronysj.rss.dto.JsonFeedDto;
import top.aaronysj.rss.feed.FeedTaskExecutor;
import top.aaronysj.rss.utils.TimeUtils;

import java.util.Date;
import java.util.Optional;

/**
 * 订阅类的入口
 *
 * @author aaronysj
 * @date 10/1/21
 */
@RequestMapping("/rss/feed")
@RestController
@Slf4j
public class FeedController {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    private TencentSportsUtil tencentSportsUtil;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private FeedTaskExecutor feedTaskExecutor;
    @GetMapping("/cba.json")
    public JsonFeedDto getRssJsonByModule(@PathVariable("module") String module) {
        return feedTaskExecutor.rest(module);
    }

    @RequestMapping("/nba.json")
    public JsonFeedDto nbaFeed() {
        if (tencentSportsUtil.isTodayLastGameOver()) {
            // 给最近的
            return cacheUtil.getLatestGames(CacheUtil.RSS_NBA_HISTORY);
        }
        // 给今天的
        Optional<JsonFeedDto> todayFeed = reactiveRedisTemplate.opsForHash()
                .get(CacheUtil.RSS_NBA_HISTORY, TimeUtils.dateFormat(new Date()))
                .blockOptional().map(o -> JSONUtil.toBean((String) o, JsonFeedDto.class));
        return todayFeed.orElse(null);
    }

}
