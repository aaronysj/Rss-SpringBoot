package com.aaronysj.rss.feed.nba;

import com.aaronysj.rss.feed.sports.tencent.BasketballCacheUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
