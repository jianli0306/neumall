package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.client.IdClient;
import com.hmall.common.utils.BeanUtils;
import com.hmall.item.domain.dto.SeckillDTO;
import com.hmall.item.domain.po.Seckill;
import com.hmall.item.mapper.SeckillMapper;
import com.hmall.item.service.ISeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class ISeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill> implements ISeckillService {
    private final StringRedisTemplate stringRedisTemplate;
    private final IdClient idClient;
    private final SeckillMapper seckillMapper;
    @Override
    public Long addTicket(SeckillDTO seckillDTO) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式（可以根据需要调整）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        Long id = idClient.generateId("sekcill");
        // 格式化日期并拼接
        String rid = "seckill:"+id;
        stringRedisTemplate.opsForValue().set(rid+":stock", String.valueOf(seckillDTO.getStock()));
        stringRedisTemplate.opsForSet().add(rid+":user","empty");
        Seckill seckill = BeanUtils.copyBean(seckillDTO, Seckill.class);
        seckill.setId(id);
        save(seckill);
        return id;
    }

    @Override
    public void deductStock(Long seckillId) {
        seckillMapper.updateStock(seckillId);
    }

}
