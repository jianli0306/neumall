package com.hmall.item.controller;
import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.dto.SeckillDTO;
import com.hmall.item.service.ISeckillService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {
    private final ISeckillService seckillService;
    @PostMapping
    public Long addSeck(@RequestBody SeckillDTO seckillDTO) {
        return seckillService.addTicket(seckillDTO);
    }

    @ApiOperation("扣减秒杀库存")
    @PutMapping("/stock/deduct/{seckillId}")
    public void deductSecKillStock(@PathVariable Long seckillId) {
        seckillService.deductStock(seckillId);
    }
}
