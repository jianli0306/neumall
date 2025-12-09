package com.hmall.api.client;

import com.hmall.common.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ID生成服务Feign客户端
 */
@FeignClient(value = "id-service")
public interface IdClient {
    
    @ApiOperation("生成单个分布式自增ID")
    @GetMapping("/ids")
    Long generateId(@RequestParam("businessType") String businessType);
    
    @ApiOperation("批量生成分布式自增ID")
    @GetMapping("/ids/batch")
    Long[] generateIds(@RequestParam("businessType") String businessType,
                      @RequestParam("count") int count);
}