package com.aaronysj.rss.feed;

import com.aaronysj.rss.dto.JsonFeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 执行器
 *
 * @author aaronysj
 * @date 10/3/21
 */
@Component
@Slf4j
public class FeedTaskExecutor {

    private final Map<String, FeedTask> feedTaskMap;

    @Autowired
    public FeedTaskExecutor(Map<String, FeedTask> feedTaskMap) {
        this.feedTaskMap = feedTaskMap;
    }

    public JsonFeedDto rest(String module) {
        FeedTask feedTask = feedTaskMap.get(module);
        if(feedTask == null) {
            log.info("invalid module: {}", module);
            return null;
        }
        return feedTask.restAdaptor();
    }

    public void initAll() {
        feedTaskMap.forEach((key, feedTask) -> {
            log.info("init {} feed", key);
            feedTask.init();
        });
    }

}