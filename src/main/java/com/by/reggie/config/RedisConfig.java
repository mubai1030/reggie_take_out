package com.by.reggie.config;

/**
 * @author xiaobai
 * @create 2023-05-14 21:47
 */
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

     @Bean
     public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            //默认的Key序列化器为：JdkSerializationRedisSerializer
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            return redisTemplate;
     }
}
