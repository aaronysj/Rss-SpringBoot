package com.aaronysj.rss.feed.nba;

import cn.hutool.json.JSONUtil;
import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author aaronysj
 * @date 10/3/21
 */
@Component
@Slf4j
public class NbaCacheUtils {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    /**
     * map
     * key        value
     * 2021-10-03 JsonFeedDto
     * 2021-10-02 JsonFeedDto
     */
    public static final String NBA_HISTORY_KEY = "rss:nba:history";

    /**
     * string  今天最后一场比赛时间
     * key                               value
     * rss:nba:todayLastGame:2021-10-17  2021-10-17 15:00:00
     */
    public static final String TODAY_LAST_GAME_PREFIX = "rss:nba:todayLastGame:";

    /**
     * 获取今天最后一场比赛
     * @param date date
     * @return Optional<Date>
     */
    public Optional<Date> getTodayLastGame(Date date) {
        return reactiveRedisTemplate.opsForValue().get(TODAY_LAST_GAME_PREFIX + TimeUtils.dateFormat(date)).blockOptional().map(x -> TimeUtils.dateParse(x, TimeUtils.DATE_TIME_PATTERN));
    }

    public void updateTodayLastGameTime(Date date, String startTime) {
        reactiveRedisTemplate.opsForValue().set(TODAY_LAST_GAME_PREFIX + TimeUtils.dateFormat(date), startTime, Duration.ofHours(24)).block();
    }

    public void update(String date, JsonFeedDto nba) {
        reactiveRedisTemplate.opsForHash().put(NBA_HISTORY_KEY, date, JSONUtil.toJsonStr(nba)).block();
    }

    public void update(Date date, JsonFeedDto nba) {
        update(TimeUtils.dateFormat(date), nba);
    }

    public Optional<JsonFeedDto> get(String date) {
        Optional<Object> todayStr = reactiveRedisTemplate.opsForHash().get(NBA_HISTORY_KEY, date).blockOptional();
        return todayStr.map(o -> JSONUtil.toBean((String) o, JsonFeedDto.class));
    }

    public Optional<JsonFeedDto> get(Date date) {
        return get(TimeUtils.dateFormat(date));
    }

    /**
     * 返回最近十天的 nba 赛程
     */
    public JsonFeedDto getLatest10Days() {
        List<Object> latest10Days = TimeUtils.getLast9DaysAndTomorrow();
        List<Object> blockObjs = reactiveRedisTemplate.opsForHash()
                .multiGet(NBA_HISTORY_KEY, latest10Days).block();
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
