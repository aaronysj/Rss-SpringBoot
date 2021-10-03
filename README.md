# Rss-SpringBoot

a personal feed server based on spring boot

## Rss 接口

`/rss/feed/{module}.json`

## Rss 软件推荐

[NetNewsWire](https://github.com/Ranchero-Software/NetNewsWire)

### NBA 赛程
`/rss/feed/nba.json`

该`feed`设计为：每天 0 - 15 点，每间隔 15 分钟，去 [腾讯 NBA 赛程](https://nba.stats.qq.com/schedule/) 拉取数据;超过 15 点之后，数据全部入 redis

![示例](https://z3.ax1x.com/2021/10/03/4L7f6U.png)

### 纪念日

### CBA 赛程
`/rss/feed/cba.json`

### 英超赛程

## Docker 部署

```shell
cd Rss-SpringBoot
./gradlew clean bootJar
dcoker-compose up -d
```


