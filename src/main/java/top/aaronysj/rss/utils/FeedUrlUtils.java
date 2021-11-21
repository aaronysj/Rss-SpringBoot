package top.aaronysj.rss.utils;

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
    private static final String CBA_SCHEDULE_URL = "https://matchweb.sports.qq.com/matchUnion/list?startTime=%s&endTime=%s&columnId=100008";

    public static String getNbaScheduleUrl(String startTime, String endTime) {
        return String.format(NBA_SCHEDULE_URL, startTime, endTime);
    }

    public static String getCbaScheduleUrl(String startTime, String endTime) {
        return String.format(CBA_SCHEDULE_URL, startTime, endTime);
    }
}
