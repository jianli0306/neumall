package com.hmall.trade.controller;

import com.hmall.trade.service.ISeckillOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillOrders")
@RequiredArgsConstructor
public class SeckillOrderController {
    private final ISeckillOrderService iSeckillOrderService;
    @PostMapping("/{seckillId}")
    String createSeckillOrder(@PathVariable("seckillId") Long seckillId){
        return iSeckillOrderService.createSeckillOrder(seckillId);

    }
}
