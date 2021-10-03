package com.aaronysj.rss.feed.nba;

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
public class NbaCacheUtilsTest {

    @Autowired
    private NbaCacheUtils nbaCacheUtils;

    @Test
    void getLatest10Days() {
        nbaCacheUtils.getLatest10Days();
    }
}
