package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    /*
    * 需要管理第三方bean对象redisTemplate, 如果参数需要依赖其他bean对象，则直接在形参中注入即可，容器会自动分配
    * */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){  // 先在参数位置注入redis连接工厂对象
        log.info("开始创建redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        /*
        * 设置redis key的序列化器，为了在图形化界面中能够更直观地查看key
        * 默认的key序列化器为 JdkSerializationRedisSerializer，导致我们存到Redis中后的数据和原始数据有差别，
        * 故设置为StringRedisSerializer序列化器。
        * */
        redisTemplate.setKeySerializer(new StringRedisSerializer());  // 字符串类型的redis序列化器

        /*
        * 感觉不如直接使用 stringRedisTemplate，操作字符串类型的数据
        * 对于需要存储的对象数据，可以将其转换为JSON格式的字符串
        * */
        return redisTemplate;
    }
}
