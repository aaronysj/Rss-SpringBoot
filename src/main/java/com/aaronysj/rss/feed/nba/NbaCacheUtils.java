package com.aaronysj.rss.feed.nba;

import cn.hutool.json.JSONUtil;
import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    public void update(String date, JsonFeedDto nba) {
        reactiveRedisTemplate.opsForHash().put(NBA_HISTORY_KEY, date, JSONUtil.toJsonStr(nba)).block();
    }

    public void update(Date date, JsonFeedDto nba) {
        update(TimeUtils.dateFormat(date), nba);
    }

    public Optional<JsonFeedDto> get(String date) {
        String today = (String) reactiveRedisTemplate.opsForHash().get(NBA_HISTORY_KEY, date).block();
        if (today == null) {
            return Optional.empty();
        }
        return Optional.of(JSONUtil.toBean(today, JsonFeedDto.class));
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
        List<JsonFeedDto.Item> items = jsonFeedDtos.stream().flatMap(jsonFeedDto -> jsonFeedDto.getItems().stream()).collect(Collectors.toList());
        JsonFeedDto jsonFeedDto = jsonFeedDtos.get(0);
        jsonFeedDto.setItems(items);
        return jsonFeedDto;
    }

}
