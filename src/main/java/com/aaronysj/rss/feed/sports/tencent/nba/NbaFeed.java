package com.aaronysj.rss.feed.sports.tencent.nba;

import com.aaronysj.rss.feed.sports.tencent.BallFeed;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 10/18/21
 */
public class NbaFeed implements BallFeed {

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


    @Override
    public String getHistoryKey() {
        return NBA_HISTORY_KEY;
    }

    @Override
    public String getTodayLastGameKeyPrefix() {
        return TODAY_LAST_GAME_PREFIX;
    }
}
