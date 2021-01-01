package com.aaronysj.rss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class RssApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(RssApplication.class, args);
	}
}
