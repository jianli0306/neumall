package com.hmall.id.service;

import org.springframework.stereotype.Service;

/**
 * ID生成服务接口
 */
@Service
public interface IdGeneratorService {
    
    /**
     * 生成分布式自增ID
     * @param businessType 业务类型
     * @return 64位分布式自增ID
     */
    long generateId(String businessType);
    
    /**
     * 批量生成分布式自增ID
     * @param businessType 业务类型
     * @param count 生成数量，最大1000
     * @return 批量生成的ID数组
     */
    long[] generateIds(String businessType, int count);
}