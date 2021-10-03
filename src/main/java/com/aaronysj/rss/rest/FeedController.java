package com.aaronysj.rss.rest;

import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.feed.FeedTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 订阅类的入口
 *
 * @author aaronysj
 * @date 10/1/21
 */
@RequestMapping("/feed")
@RestController
@Slf4j
public class FeedController {

    private final Map<String, FeedTask> feedTaskMap;

    @Autowired
    public FeedController(Map<String, FeedTask> feedTaskMap) {
        this.feedTaskMap = feedTaskMap;
    }

    @GetMapping("/{module}.json")
    public JsonFeedDto getRssJsonByModule(@PathVariable("module") String module) {
        FeedTask feedTask = feedTaskMap.get(module);
        if(feedTask == null) {
            log.info("invalid module: {}", module);
            return null;
        }
        return feedTask.restAdaptor();
    }

}
