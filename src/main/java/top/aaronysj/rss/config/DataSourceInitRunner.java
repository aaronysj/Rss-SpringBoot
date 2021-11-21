package top.aaronysj.rss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.aaronysj.rss.datasource.DataSource;
import top.aaronysj.rss.feed.FeedTaskExecutor;

import java.util.List;

/**
 * init feed
 *
 * @author aaronysj
 * @date 10/3/21
 */
@Component
@Slf4j
public class DataSourceInitRunner implements ApplicationRunner {

    @Autowired
    private FeedTaskExecutor feedTaskExecutor;

    @Autowired
    private List<DataSource> dataSourceList;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        feedTaskExecutor.initAll();
        if(!CollectionUtils.isEmpty(dataSourceList)) {
            dataSourceList.forEach(DataSource::init);
        }
    }
}
