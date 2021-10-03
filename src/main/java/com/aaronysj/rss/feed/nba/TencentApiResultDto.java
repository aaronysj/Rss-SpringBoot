package com.aaronysj.rss.feed.nba;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 腾讯接口回调的数据类型
 *
 * {
 *     "code": "0",
 *     "version": "",
 *     "data": {
 *         "2021-10-03" : [
 *              {
 *                  @see com.aaronysj.rss.feed.nba.TencentNbaInfo
 *              }，
 *              {
 *                  @see com.aaronysj.rss.feed.nba.TencentNbaInfo
 *              }
 *         ]
 *     }
 * }
 *
 * @author aaronysj
 * @date 10/2/21
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TencentApiResultDto {

    private int code;
    private Map<String, List<TencentNbaInfo>> data;
    private String version;

    public static TencentApiResultDto ok(){
        return new TencentApiResultDto(200, null, null);
    }
}
