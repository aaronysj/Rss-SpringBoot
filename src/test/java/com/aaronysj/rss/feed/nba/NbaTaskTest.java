package com.aaronysj.rss.feed.nba;

import com.aaronysj.rss.feed.sports.tencent.nba.NbaTask;
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
public class NbaTaskTest {

    @Autowired
    private NbaTask nbaTask;

    @Test
    void nbaTaskEvery5Min() {
        nbaTask.nbaTaskEvery5Min();
    }

    @Test
    void nbaTaskAt15() {
        nbaTask.nbaTaskAt15();
    }

    @Test
    void restAdaptor() {
        nbaTask.restAdaptor();
    }
}
