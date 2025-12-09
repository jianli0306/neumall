package com.hmall.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.dto.SeckillDTO;
import com.hmall.item.domain.po.Seckill;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ISeckillService extends IService<Seckill> {
    Long addTicket(SeckillDTO seckillDTO);
    void deductStock(Long seckillId);
}
