package com.aaronysj.rss.utils;

/**
 * 处理 url 参数
 *
 * @author aaronysj
 * @date 10/4/21
 */
public class FeedUrlUtils {

    private FeedUrlUtils() {
    }

    private static final String NBA_SCHEDULE_URL = "https://matchweb.sports.qq.com/kbs/list?from=NBA_PC&columnId=100000&startTime=%s&endTime=%s&from=sporthp";

    public static String getNbaScheduleUrl(String startTime, String endTime) {
        return String.format(NBA_SCHEDULE_URL, startTime, endTime);
    }

}
