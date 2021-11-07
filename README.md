# Rss-SpringBoot

It is a json feed server based on spring boot.

> Rss 软件推荐 [NetNewsWire](https://github.com/Ranchero-Software/NetNewsWire)
> 
## TODO
- [x] task 做成 Runnable 
- [ ] 调用接口，生成待办！
- [ ] 重点比赛信息，通过钉钉/微信机器人推送

| done | doing   | plan         |
| ---------- | ---------- | ---------- |
|  ✅ NBA 赛程 ✅ CBA 赛程 |   |钉钉机器人接入 |

## Rss 接口

`/rss/feed/{module}.json`

### NBA 赛程
`/rss/feed/nba.json`

每天 0 - 15 点，每间隔 5 分钟，去 [腾讯 NBA 赛程](https://nba.stats.qq.com/schedule/) 拉取数据  
超过 15 点之后直接从`redis`取前9天和明天共10天的数据

![rss 效果](https://z3.ax1x.com/2021/10/03/4L7f6U.png)
![rss 效果](https://z3.ax1x.com/2021/10/04/4X2XIf.png)

### CBA 赛程
`/rss/feed/cba.json`

每天 9 -23 点，每间隔 5 分钟，去 [腾讯 CBA 赛程](https://kbs.sports.qq.com/#cba) 拉取数据  
23 点 - 次日9点 直接从`redis`取前9天和明天共10天的数据
![rss 效果](https://z3.ax1x.com/2021/10/25/55p5p4.jpg)
## Docker 部署

```shell
cd Rss-SpringBoot
./gradlew clean bootJar
docker-compose up -d
```

### 更新

```shell
sh update.sh
```


