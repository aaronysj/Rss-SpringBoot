package com.aaronysj.rss.feed.nba;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 腾讯 nba 的数据结构
 *
 * @author aaronysj
 * @date 10/3/21
 */
@Setter
@Getter
@NoArgsConstructor
public class TencentNbaInfo {
    /**
     * about game
     */
    private String matchType;
    private String mid;
    private String webUrl;
    private String matchDesc;
    /**
     * 0 为开始 1 正在 2 已结束
     */
    private String matchPeriod;

    /**
     * guest team
     */
    private String leftId;
    private String leftName;
    private String leftBadge;
    private String leftGoal;
    private String leftHasUrl;

    /**
     * home team
     */
    private String rightId;
    private String rightName;
    private String rightBadge;
    private String rightGoal;
    private String rightHasUrl;

    private String startTime;

    /**
     * 0 为开始 1 正在 2 已结束
     */
    private String livePeriod;
    private String liveType;
    private String liveId;

    private String quarter;
    private String quarterTime;
    private String programId;
    /**
     * 0 free, 1 VIP
     */
    private String isPay;

    private String groupName;
    private String competitionId;
    private String tvLiveId;
    /**
     * playback
     */
    private String ifHasPlayback;
    private String url;
    private String categoryId;
    private String scheduleId;
    private String roseNewsId;
    private String latestNews;

}
