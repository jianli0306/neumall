package com.hmall.id.controller;

import com.hmall.common.domain.R;
import com.hmall.id.service.IdGeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ID生成服务控制器
 */
@Api(tags = "ID生成相关接口")
@RestController
@RequestMapping("/ids")
@RequiredArgsConstructor
public class IdController {
    
    private final IdGeneratorService idGeneratorService;
    
    @ApiOperation("生成单个分布式自增ID")
    @ApiImplicitParam(name = "businessType", value = "业务类型", required = true, example = "order")
    @GetMapping
    public Long generateId(@RequestParam("businessType") String businessType) {
        long id = idGeneratorService.generateId(businessType);
        return id;
    }
    
    @ApiOperation("批量生成分布式自增ID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessType", value = "业务类型", required = true, example = "order"),
            @ApiImplicitParam(name = "count", value = "生成数量，1-1000", required = true, example = "10")
    })
    @GetMapping("/batch")
    public long[] generateIds(@RequestParam("businessType") String businessType,
                                @RequestParam("count") int count) {
        long[] ids = idGeneratorService.generateIds(businessType, count);
        return ids;
    }
    
    @ApiOperation("生成单个分布式自增ID（POST方式）")
    @ApiImplicitParam(name = "businessType", value = "业务类型", required = true, example = "order")
    @PostMapping
    public Long generateIdPost(@RequestParam("businessType") String businessType) {
        long id = idGeneratorService.generateId(businessType);
        return id;
    }
}