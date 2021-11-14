package top.aaronysj.rss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.aaronysj.rss.feed.FeedTaskExecutor;

/**
 * init feed
 *
 * @author aaronysj
 * @date 10/3/21
 */
@Component
public class RssInitRunner implements ApplicationRunner {

    @Autowired
    private FeedTaskExecutor feedTaskExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        feedTaskExecutor.initAll();
    }
}
