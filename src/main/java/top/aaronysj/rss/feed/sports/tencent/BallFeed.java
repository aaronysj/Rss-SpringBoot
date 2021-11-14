package top.aaronysj.rss.feed.sports.tencent;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 10/18/21
 */
public interface BallFeed {
    /**
     * 今天最后一场比赛开始 key 前缀
     *
     * @return str
     */
    String getTodayLastGameKeyPrefix();

    /**
     * 归档记录的 key
     * @return str
     */
    String getHistoryKey();
}
