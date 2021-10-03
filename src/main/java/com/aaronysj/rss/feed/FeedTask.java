package com.aaronysj.rss.feed;

import com.aaronysj.rss.dto.JsonFeedDto;

import java.util.Date;

/**
 * feed task interface
 *
 * @author aaronysj
 * @date 10/3/21
 */
public interface FeedTask {

    /**
     * 执行任务
     * @return json feed
     */
    JsonFeedDto executeTask(Date date);


    /**
     * rest 接口适配
     * @return JsonFeedDto
     */
    JsonFeedDto restAdaptor();

    /**
     * 每次项目启动初始化
     */
    void init();

}
