package top.aaronysj.rss.datasource;

/**
 * 数据源接口
 *
 * @author aaronysj
 * @date 11/14/21
 */
public interface DataSource {

    /**
     * 获取数据，并通过 mq 发送出去
     * @param data 数据时间
     */
    void produceData(String data);

    /**
     * 初始化
     */
    default void init(){}

}
