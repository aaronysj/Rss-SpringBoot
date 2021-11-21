package top.aaronysj.rss.robot.dingtalk;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import top.aaronysj.rss.feed.BaseTest;

import javax.annotation.Resource;

class DingTalkRobotMarkdownMsgTest extends BaseTest {

    @Resource
    private DingTalkRobotMarkdownMsg dingTalkRobotMarkdownMsg;

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Test
    void send() {
        dingTalkRobotMarkdownMsg.send("08:30 未开始 雄鹿 0 vs 0 76人 [集锦](https://sports.qq.com/kbsweb/game.htm?mid=100000:55400748) [数据](https://nba.stats.qq.com/nbascore/?mid=55400748) [回放](https://sports.qq.com/kbsweb/game.htm?mid=100000:55400748&replay=1)\n\n" +
                "10:00 未开始 老鹰 0 vs 0 爵士 [集锦](https://sports.qq.com/kbsweb/game.htm?mid=100000:55400749) [数据](https://nba.stats.qq.com/nbascore/?mid=55400749) [回放](https://sports.qq.com/kbsweb/game.htm?mid=100000:55400749&replay=1)\n\n" +
                "11:00 未开始 开拓者 0 vs 0 快船 [集锦](https://sports.qq.com/kbsweb/game.htm?mid=100000:55400750) [数据](https://nba.stats.qq.com/nbascore/?mid=55400750) [回放](https://sports.qq.com/kbsweb/game.htm?mid=100000:55400750&replay=1)\n\n" +
                "\n\n" +
                "\uD83D\uDC49\uD83C\uDFFB [schedule](https://nba.stats.qq.com/schedule) [standings](https://nba.stats.qq.com/standings)\n\n" +
                "\uD83D\uDC49\uD83C\uDFFB [Welcome](http://24zhiboba.com) [to](https://feisuzhibo.com) [Hangouts](https://www.cnmysoft.com/)");
    }

    @Test
    public void send2() {
        Object block = reactiveRedisTemplate.opsForHash().get("rss:nba:history:markdown", "2021-11-10").block();
        dingTalkRobotMarkdownMsg.send(block.toString());
    }
}