package com.aaronysj.rss.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AjaxResultDto {

    private int code;
    private Object data;

    public static AjaxResultDto ok(){
        return new AjaxResultDto(200, null);
    }
}
