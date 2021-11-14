package top.aaronysj.rss.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import top.aaronysj.rss.dto.JsonFeedDto;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 执行器
 *
 * @author aaronysj
 * @date 10/3/21
 */
@Component
@Slf4j
public class FeedTaskExecutor {

    @Autowired
    @Qualifier("feedThreadPool")
    private ThreadPoolExecutor feedPool;

    private final Map<String, FeedTask> feedTaskMap;

    @Autowired
    public FeedTaskExecutor(Map<String, FeedTask> feedTaskMap) {
        this.feedTaskMap = feedTaskMap;
    }

    public JsonFeedDto rest(String module) {
        FeedTask feedTask = feedTaskMap.get(module);
        if(feedTask == null) {
            log.info("invalid module: {}", module);
            throw new FeedException();
        }
        return feedTask.restAdaptor();
    }

    public void initAll() {
        feedTaskMap.forEach((key, feedTask) -> {
            log.info("init {} feed", key);
            feedPool.submit(feedTask::init);
        });
    }

}
