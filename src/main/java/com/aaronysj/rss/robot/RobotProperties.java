package com.aaronysj.rss.robot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 11/7/21
 */
@Data
@ConfigurationProperties(prefix = "robot.ding-talk")
@Component
public class RobotProperties {

    /**
     * 打卡机器人
     */
    private List<String> clockInRobots;

}
