package top.aaronysj.rss.feed;

import top.aaronysj.rss.config.AFeedTask;
import top.aaronysj.rss.dto.JsonFeedDto;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * feed task interface
 *
 * @author aaronysj
 * @date 10/3/21
 */
public interface FeedTask {

    /**
     * 执行任务
     * @param date 每天的任务时间
     * @return json feed
     */
    JsonFeedDto task(Date date);

    /**
     * rest 接口适配
     * @return JsonFeedDto
     */
    JsonFeedDto restAdaptor();

    /**
     * 获取线程池
     * @return pool
     */
    ThreadPoolExecutor getPool();

    /**
     * 异步线程池执行
     * @param date 任务时间
     */
    default void execute(Date date) {
        getPool().submit(new AFeedTask(this, date));
    }

    /**
     * 每次项目启动初始化
     */
    default void init() {}

    /**
     * 获取实现类名称
     * @return class
     */
    default String getClassName() {
         return getClass().getName();
    }

}
