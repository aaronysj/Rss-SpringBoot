package com.aaronysj.rss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Json Feed Dto
 *
 * @author aaronysj
 * @date 10/2/21
 */
@Getter
@Setter
@NoArgsConstructor
public class JsonFeedDto {

    private String version = "https://jsonfeed.org/version/1";
    private String title;
    private String description;
    @JsonProperty("home_page_url")
    private String homePageUrl;
    @JsonProperty("feed_url")
    private String feedUrl;
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
        @JsonProperty("content_html")
        private String contentHtml;
        @JsonProperty("date_modified")
        private String dateModified;
        @JsonProperty("date_published")
        private String datePublished;
        @JsonProperty("external_url")
        private String externalUrl;
        @JsonProperty("content_text")
        private String contentText;
        private String summary;
        private String image;
        @JsonProperty("banner_image")
        private String bannerImage;
        private String tags;
    }

    @Data
    public static class Author {
        private String url;
        private String name;
        private String avatar;
    }

}
