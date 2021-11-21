package top.aaronysj.rss.common;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.aaronysj.rss.dto.JsonFeedDto;
import top.aaronysj.rss.utils.TimeUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 机器人的cache
 *
 * @author aaronysj
 * @date 11/14/21
 */
@Component
public class CacheUtil {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    /**
     * string 类型
     */
    public static final String ROBOT_NBA_MARKDOWN = "robot:nba:markdown";
    public static final String RSS_NBA_HISTORY = "rss:nba:history";
    public static final String NBA_LAST_GAME_TIME_PREFIX = "rss:nba:lastGameTime:";

    public static String getLastGameTimeKey(String date) {
        return NBA_LAST_GAME_TIME_PREFIX + date;
    }

    public JsonFeedDto getLatestGames(String cacheKey) {
        List<Object> latestDate = TimeUtils.getLatestDate();
        List<Object> blockObjs = reactiveRedisTemplate.opsForHash()
                .multiGet(cacheKey, latestDate).block();
        if (CollectionUtils.isEmpty(blockObjs)) {
            return null;
        }
        // 不为空时
        List<JsonFeedDto> jsonFeedDtos = blockObjs.stream()
                .filter(Objects::nonNull)
                .map(String.class::cast)
                .map(str -> JSONUtil.toBean(str, JsonFeedDto.class)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(jsonFeedDtos)) {
            return null;
        }
        List<JsonFeedDto.Item> items = jsonFeedDtos.stream()
                .filter(jsonFeedDto -> !CollectionUtils.isEmpty(jsonFeedDto.getItems()))
                .flatMap(jsonFeedDto -> jsonFeedDto.getItems().stream())
                .collect(Collectors.toList());
        JsonFeedDto jsonFeedDto = jsonFeedDtos.get(0);
        jsonFeedDto.setItems(items);
        return jsonFeedDto;
    }

}
