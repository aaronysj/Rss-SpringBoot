package com.aaronysj.rss.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjaxResultDto {

    private int code;
    private Object data;
    private String version;

    public static AjaxResultDto ok(){
        return new AjaxResultDto(200, null, null);
    }
}
