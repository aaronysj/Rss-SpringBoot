package com.aaronysj.rss.feed.cba;

import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.feed.FeedTask;

import java.util.Date;

/**
 * cba 与 nba 接口一致，但我不是很关注，先不做了
 *
 * @author aaronysj
 * @date 10/4/21
 */
public class CbaTask implements FeedTask {

    @Override
    public JsonFeedDto executeTask(Date date) {
        return null;
    }

    @Override
    public JsonFeedDto restAdaptor() {
        return null;
    }
}
