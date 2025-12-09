package com.hmall.trade.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SeckillLuaConfig {

    private RedisScript<Long> seckillScript;

    @PostConstruct
    public void init() {
        // 从文件加载Lua脚本
        seckillScript = RedisScript.of(
                new ClassPathResource("lua/seckill.lua"),
                Long.class
        );
    }

    public RedisScript<Long> getSeckillScript() {
        return seckillScript;
    }
}
