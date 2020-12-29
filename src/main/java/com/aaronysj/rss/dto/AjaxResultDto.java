package com.aaronysj.rss.dto;

import lombok.Data;

@Data
public class AjaxResultDto {

    private int code;
    private Object data;
    private String version;
}
