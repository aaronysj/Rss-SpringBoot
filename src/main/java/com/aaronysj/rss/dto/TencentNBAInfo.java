package com.aaronysj.rss.dto;

import lombok.Data;

@Data
public class TencentNBAInfo{
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

    @Deprecated
    private String title;
    @Deprecated
    private String logo;

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
