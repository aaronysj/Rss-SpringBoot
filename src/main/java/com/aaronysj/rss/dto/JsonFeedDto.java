package com.aaronysj.rss.dto;

import lombok.Data;

import java.util.List;

@Data
public class JsonFeedDto {

    private String version = "https://jsonfeed.org/version/1";
    private String title;
    private String description;
    private String home_page_url;
    private String feed_url;
    private String icon;
    private String favicon;
    private List<Item> items;

    @Data
    public static class Item {
        // required
        private String id;
        private String url;
        private Author author;
        private String title;
        private String content_html;
        private String date_modified;
        private String date_published;

        private String external_url;
        private String content_text;
        private String summary;
        private String image;
        private String banner_image;
        private String tags;
    }

    @Data
    public static class Author {
        private String url;
        private String name;
        private String avatar;
    }

}
