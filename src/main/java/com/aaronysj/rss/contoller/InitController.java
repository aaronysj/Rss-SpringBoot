package com.aaronysj.rss.contoller;

import com.aaronysj.rss.config.Constants;
import com.aaronysj.rss.dto.AjaxResultDto;
import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/init")
//public class InitController {
//
//    @Autowired
//    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
//
//    @PostMapping("/nba")
//    public AjaxResultDto initNbaHistory() {
//        reactiveRedisTemplate.keys("2021*").toStream()
//                .map(key -> reactiveRedisTemplate.opsForValue().get(key).block())
//                .map(str -> GsonUtils.convertToBean(str, JsonFeedDto.class))
//                .forEach(jsonFeedDto ->
//                        reactiveRedisTemplate.opsForList().
//                                rightPush(Constants.NBA_HISTORY_KEY, GsonUtils.convertToString(jsonFeedDto)).block()
//                );
//
//        return AjaxResultDto.ok();
//    }
//}
