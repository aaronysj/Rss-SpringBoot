package com.aaronysj.rss.contoller;

import com.aaronysj.rss.config.Constants;
import com.aaronysj.rss.dto.AjaxResultDto;
import com.aaronysj.rss.dto.JsonFeedDto;
import com.aaronysj.rss.dto.TencentNBAInfo;
import com.aaronysj.rss.task.NBATask;
import com.aaronysj.rss.utils.GsonUtils;
import com.aaronysj.rss.utils.TimeUtils;
import com.rometools.rome.feed.rss.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 考虑做一个适配器，
 * 不管是什么样的 URI，通过适配器过一道，做成我想要的数据结构之后，传到我的 RSS 生成器里
 */
@RequestMapping("/feed")
@RestController
@Slf4j
public class FeedController {

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    private NBATask nbaTask;

    @GetMapping("/nba.json")
    public JsonFeedDto getNBAJson() {
        // 当天的日期
        Date nowTime = new Date();
        String today = TimeUtils.dateFormat(nowTime, TimeUtils.DATE_PATTERN);
        String hour = TimeUtils.dateFormat(nowTime, TimeUtils.HOUR_ONLY_PATTERN);
        // 超过下午三点就不实时拿腾讯数据了，直接去 redis 里拿
        if(hour.compareTo("15") >= 0) {
            // get from redis
            // last 10 days of nba
//            String block = reactiveRedisTemplate.opsForValue().get(today).block();
            List<JsonFeedDto> jsonFeedDtos = reactiveRedisTemplate.opsForList()
                    .range(Constants.NBA_HISTORY_KEY, 0, -1)
                    .toStream()
                    .map(str -> GsonUtils.convertToBean(str, JsonFeedDto.class)).collect(Collectors.toList());
            // 不为空时
            if(!CollectionUtils.isEmpty(jsonFeedDtos)) {
                List<JsonFeedDto.Item> items = jsonFeedDtos.stream().flatMap(jsonFeedDto -> jsonFeedDto.getItems().stream()).collect(Collectors.toList());
                JsonFeedDto jsonFeedDto = jsonFeedDtos.get(0);
                jsonFeedDto.setItems(items);
                return jsonFeedDto;
            }
            return null;
        }
        // 白天时间直接刷新
        return nbaTask.getTodayNBAGameInfo();
    }

    /**
     * get the NBA live info from Tencent Sports and WeiLai Sports
     * 0:00 - 15:00 update per 10 min
     * save the result after 15:00
     *
     * @return channel
     */
    @GetMapping("/rss/basketball")
    public Channel getRss() {
        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle("aaronysj Feed");
        channel.setDescription("NBA DAILY");
        channel.setLink("https://mat1.gtimg.com/sports/nba/logo/1602/9.png");
        channel.setUri("https://mat1.gtimg.com/sports/nba/logo/1602/9.png");
        channel.setGenerator("aaronysj feed rss generator");

        // 当天的日期
        String date = "2020-12-28";
        String url = "https://matchweb.sports.qq.com/kbs/list?from=NBA_PC&columnId=100000&startTime=" + date + "&endTime=" + date + "&from=sporthp";
        AjaxResultDto ajaxResultDto = restTemplate.getForObject(url, AjaxResultDto.class);
        Map<String, Object> dateMap = GsonUtils.jsonToMaps(GsonUtils.convertToString(ajaxResultDto.getData()));
        List<TencentNBAInfo> tencentNBAInfoList = GsonUtils.jsonToList(GsonUtils.convertToString(dateMap.get(date)), TencentNBAInfo.class);
        System.out.println(tencentNBAInfoList);


        Image image = new Image();
        image.setUrl("https://mat1.gtimg.com/sports/nba/logo/1602/9.png");
        image.setTitle("Basketball feed");
        image.setHeight(32);
        image.setWidth(32);
        channel.setImage(image);

        Item item1 = new Item();
        item1.setAuthor("item1");
        item1.setLink("item1-link");
        item1.setTitle("item1-title");
        item1.setUri("item1-uri");
        item1.setComments("item1-comments");

        Content content = new Content();
        content.setValue("item1-content");
        content.setType(Content.TEXT);
        item1.setContent(content);

        Category category = new Category();
        category.setValue("basketball");
        item1.setCategories(Collections.singletonList(category));

        Description desc1 = new Description();
        desc1.setValue("hahahaha1");

        item1.setDescription(desc1);

        Date postDate = new Date();

        item1.setPubDate(postDate);

        channel.setPubDate(postDate);

        channel.setItems(Arrays.asList(item1));
        //Like more Entries here about different new topics
        return channel;
    }

}
