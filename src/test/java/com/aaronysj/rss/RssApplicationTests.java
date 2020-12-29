package com.aaronysj.rss;

import com.aaronysj.rss.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class RssApplicationTests {

	@Test
	void contextLoads() {
		Date date = new Date();
		String s = TimeUtils.dateFormat(date, TimeUtils.DATE_PATTERN);
		System.out.println(s);
		String utc = TimeUtils.dateFormat(date, TimeUtils.UTC_TIME_PATTERN);
		System.out.println(utc);
	}

}
