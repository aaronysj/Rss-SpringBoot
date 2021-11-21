package top.aaronysj.rss.feed.nba;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.aaronysj.rss.feed.sports.tencent.BasketballCacheUtil;

/**
 * test
 *
 * @author aaronysj
 * @date 10/3/21
 */
@SpringBootTest
public class BasketballCacheUtilTest {

    @Autowired
    private BasketballCacheUtil basketballCacheUtil;

    @Test
    void getLatest10Days() {
        basketballCacheUtil.getLatest10Days();
    }
}
