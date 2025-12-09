package com.hmall.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.client.IdClient;
import com.hmall.api.client.ItemClient;
import com.hmall.common.utils.UserContext;
import com.hmall.trade.config.SeckillLuaConfig;
import com.hmall.trade.domain.po.SeckillOrder;
import com.hmall.trade.mapper.SeckillOrderMapper;
import com.hmall.trade.service.ISeckillOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper,SeckillOrder> implements ISeckillOrderService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ItemClient itemClient;
    private final IdClient idClient;
    private final SeckillLuaConfig seckillLuaConfig;
    private final RabbitTemplate rabbitTemplate;
    @Override
    @GlobalTransactional
    public String createSeckillOrder(Long seckillId) {
        Long user = UserContext.getUser();
        String ridStock="seckill"+":"+seckillId+":"+"stock";
        String ridUser="seckill"+":"+seckillId+":"+"user";
        // 使用Lua脚本原子性执行秒杀操作
        Long luaResult = stringRedisTemplate.execute(
                seckillLuaConfig.getSeckillScript(),
                Arrays.asList(ridStock, ridUser),
                String.valueOf(user)
        );
        // 处理Lua脚本返回结果
        if (luaResult == null) {
            log.error("Lua脚本执行返回null, seckillId: {}, userId: {}");
            return "系统错误，请重试";
        }
        if (luaResult ==-1l) {
            return "库存不足";
        }
        if (luaResult ==0l) {
            return "用户已参与过秒杀";
        }
        Long seckillOrderId = idClient.generateId("seckillOrderId");
        // 使用Map发送
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", seckillOrderId);
        messageMap.put("userId", user);
        messageMap.put("seckillId", seckillId);
        rabbitTemplate.convertAndSend("seckill.direct", "seckill.success",messageMap);
        return String.valueOf(seckillOrderId);

    }


}
