package com.aaronysj.rss.rest;

import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.feed.FeedTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private FeedTaskExecutor feedTaskExecutor;
    @GetMapping("/{module}.json")
    public JsonFeedDto getRssJsonByModule(@PathVariable("module") String module) {
        return feedTaskExecutor.rest(module);
    }

}