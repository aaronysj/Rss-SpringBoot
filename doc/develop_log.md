# RSS 订阅项目开发日志

> by aaronysj

- [x] 简单列出了时间/球队/比分等信息，以及每场比赛是否免费 - 12127
- [x] 优化面板展示：可以一眼看出谁胜谁负；展示做成表格（参考腾讯赛程页面）- 1228
- [ ] 技术优化：定时拉数据做缓存？还是直接实时拉数据

## 20201229

<font color=#0099ff size=12 face="黑体">黑体</font>

## 20201228 v1.0 出炉了

今天晚上优化了一下展示，加了一些字段，效果如下

![image-20201229000626677](/Users/aaronysj/Library/Application Support/typora-user-images/image-20201229000626677.png)

感觉还行吧，下个阶段，想想怎么部署在服务器上。然后怎么控制只在0点-15点提供服务。

## 20201227

今天开始做一个 RSS Feed，需求来自于自己对于 NBA 比分动态的迫切需要。

每天的凌晨到下午 15 点之间，可能会有 NBA 的比赛，这样每分钟获取一次腾讯体育 [NBA 赛程页面](https://nba.stats.qq.com/schedule)的接口，分析其中数据，形成一个简单的比赛概况面板。

今天是个开始，目前效果如下。

![image-20201227225108842](/Users/aaronysj/Library/Application Support/typora-user-images/image-20201227225108842.png)



![image-20201227225447927](/Users/aaronysj/Library/Application Support/typora-user-images/image-20201227225447927.png)