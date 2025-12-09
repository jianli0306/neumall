package com.hmall.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisConfiguration {

    /**
     * 配置支持存储对象的RedisTemplate（解决乱码问题）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 创建JSON序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 使用UTF-8字符集的字符串序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);

        // 设置序列化器
        template.setKeySerializer(stringSerializer);              // String key
        template.setValueSerializer(jsonSerializer);             // Object value
        template.setHashKeySerializer(stringSerializer);         // Hash key
        template.setHashValueSerializer(jsonSerializer);         // Hash value

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置StringRedisTemplate（纯字符串操作）
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);

        // 明确设置UTF-8序列化器
        StringRedisSerializer serializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}