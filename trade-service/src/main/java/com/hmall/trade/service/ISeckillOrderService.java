package com.hmall.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.SeckillOrder;

public interface ISeckillOrderService extends IService<SeckillOrder> {
    String createSeckillOrder(Long seckillId);
}
