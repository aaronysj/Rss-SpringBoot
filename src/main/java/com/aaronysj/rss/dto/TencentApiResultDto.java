package com.aaronysj.rss.dto;

import lombok.*;

/**
 * 腾讯接口回调的数据类型
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
    private Object data;
    private String version;

    public static TencentApiResultDto ok(){
        return new TencentApiResultDto(200, null, null);
    }
}
