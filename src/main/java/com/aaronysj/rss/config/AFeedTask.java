package com.aaronysj.rss.config;

import com.aaronysj.rss.feed.FeedTask;
import com.aaronysj.rss.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 10/31/21
 */
@Slf4j
public class AFeedTask implements Runnable{

    private final FeedTask feedTask;
    private final Date date;

    public AFeedTask(FeedTask feedTask, Date date) {
        this.feedTask = feedTask;
        this.date = date;
    }

    @Override
    public void run() {
        log.info(feedTask.getClassName() + " - " + TimeUtils.dateFormat(date));
        feedTask.task(date);
    }
}
