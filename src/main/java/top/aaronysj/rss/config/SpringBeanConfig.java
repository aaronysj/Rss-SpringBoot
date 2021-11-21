package top.aaronysj.rss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * spring bean 配置类
 *
 * @author aaronysj
 * @date 10/2/21
 */
@Configuration
@Slf4j
public class SpringBeanConfig {

    @Bean("rabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(5);
        factory.setConcurrentConsumers(4);
        factory.setMaxConcurrentConsumers(4);
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        /*@SuppressWarnings("all")
        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
                .newSerializationContext()
                .key((RedisSerializer) new StringRedisSerializer())
                .value(new StringRedisSerializer())
                .hashKey(StringRedisSerializer.UTF_8)
                .hashValue(new StringRedisSerializer())
                .build();*/

        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }

    @Bean("feedThreadPool")
    public ThreadPoolExecutor feedThreadPool() {
        return new ThreadPoolExecutor(4, 4,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Bean("robotThreadPool")
    public ThreadPoolExecutor robotThreadPool() {
        return new ThreadPoolExecutor(4, 4,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
