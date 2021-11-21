package top.aaronysj.rss.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.aaronysj.rss.dto.JsonFeedDto;
import top.aaronysj.rss.feed.FeedException;

/**
 * @author shijie.ye
 * @version 0.0.1
 * @date 10/25/21
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(FeedException.class)
    public JsonFeedDto handleFeedException(FeedException fe) {
        return new JsonFeedDto();
    }

}
