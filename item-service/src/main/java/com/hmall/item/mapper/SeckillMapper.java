package com.hmall.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.po.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


public interface SeckillMapper extends BaseMapper<Seckill> {
    @Update("UPDATE seckill SET stock = stock -1 WHERE id = #{seckillId} and stock > 0")
    void updateStock(Long seckillId);
}
