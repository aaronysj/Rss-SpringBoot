package com.aaronysj.rss.config;

import com.aaronysj.rss.feed.FeedTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * init feed
 *
 * @author aaronysj
 * @date 10/3/21
 */
@Component
public class RssInitRunner implements ApplicationRunner {

    @Autowired
    private FeedTaskExecutor feedTaskExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        feedTaskExecutor.initAll();
    }
}