package com.hmall.trade.listener;

import com.hmall.api.client.ItemClient;
import com.hmall.trade.domain.po.SeckillOrder;
import com.hmall.trade.service.IOrderService;
import com.hmall.trade.service.ISeckillOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SeckillListener {

    private final ISeckillOrderService seckillOrderService;
    private final ItemClient itemClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.seckill.queue", durable = "true"),
            exchange = @Exchange(name = "seckill.direct"),
            key = "seckill.success"
    ))
    public void listenPaySuccess(Map<String, Object> messageMap){
        Long id = Long.valueOf(messageMap.get("id").toString());
        Long userId = Long.valueOf(messageMap.get("userId").toString());
        Long seckillId = Long.valueOf(messageMap.get("seckillId").toString());
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(id);
        seckillOrder.setUserId(userId);
        seckillOrder.setSeckillId(seckillId);
        seckillOrderService.save(seckillOrder);
        itemClient.deductSecKillStock(seckillId);


    }
}
