package com.hmall.id.service.impl;

import com.hmall.common.exception.BadRequestException;
import com.hmall.id.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IdGeneratorServiceImpl implements IdGeneratorService {
    
    private static final DateTimeFormatter KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 第1位固定为0，中间31位是时间戳（天），最后32位是自增计数器
    private static final long TIMESTAMP_BITS = 31L;
    private static final long COUNTER_BITS = 32L;
    private static final long MAX_COUNTER = (1L << COUNTER_BITS) - 1;
    
    // 批量生成ID的最大数量
    private static final int MAX_BATCH_COUNT = 1000;
    
    private final StringRedisTemplate stringRedisTemplate;
    
    // 缓存当天的时间戳和日期，避免频繁计算
    private volatile LocalDate cachedDate;
    private volatile long cachedTimestamp;
    
    /**
     * 生成分布式自增ID
     * @param businessType 业务类型
     * @return 64位分布式自增ID
     */
    @Override
    public long generateId(String businessType) {
        // 1. 校验业务类型参数
        validateBusinessType(businessType);
        
        try {
            // 2. 生成当天的Redis key
            String key = generateKey(businessType);
            
            // 3. 使用Redis INCR获取自增计数器
            long counter = stringRedisTemplate.opsForValue().increment(key);
            
            // 4. 检查计数器是否溢出
            if (counter > MAX_COUNTER) {
                log.error("ID counter overflow for business type: {}, key: {}", businessType, key);
                throw new RuntimeException("ID counter overflow");
            }
            
            // 5. 设置key的过期时间为2天，确保当天的key不会一直占用内存
            if (counter == 1) {
                // 只有在第一次生成key时设置过期时间
                stringRedisTemplate.expire(key, 2, TimeUnit.DAYS);
                log.debug("Set expire time for key: {} to 2 days", key);
            }
            
            // 6. 获取当天的时间戳（缓存优化）
            long timestamp = getCachedTimestamp();
            
            // 7. 组装ID：第1位0 + 31位时间戳 + 32位计数器
            long id = (timestamp << COUNTER_BITS) | counter;
            
            log.debug("Generated ID: {} for business type: {}, key: {}, timestamp: {}, counter: {}", 
                    id, businessType, key, timestamp, counter);
            
            return id;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure when generating ID for business type: {}", businessType, e);
            throw new RuntimeException("Failed to generate ID due to Redis connection issue", e);
        } catch (Exception e) {
            log.error("Failed to generate ID for business type: {}", businessType, e);
            throw new RuntimeException("Failed to generate ID", e);
        }
    }
    
    /**
     * 生成当天的Redis key
     * @param businessType 业务类型
     * @return Redis key
     */
    private String generateKey(String businessType) {
        String date = getCachedDate().format(KEY_FORMATTER);
        return String.format("id:generator:%s:%s", businessType, date);
    }
    
    /**
     * 获取当前时间戳（以天为单位），并缓存当天的时间戳和日期
     * 计算方式：当前时间距离1970-01-01的天数
     * @return 31位时间戳（天）
     */
    private long getCachedTimestamp() {
        LocalDate now = LocalDate.now();
        // 检查缓存是否过期（跨天）
        if (cachedDate == null || !cachedDate.equals(now)) {
            synchronized (this) {
                if (cachedDate == null || !cachedDate.equals(now)) {
                    cachedDate = now;
                    cachedTimestamp = now.toEpochDay();
                    log.debug("Updated cached date: {}, timestamp: {}", cachedDate, cachedTimestamp);
                }
            }
        }
        return cachedTimestamp;
    }
    
    /**
     * 获取当前日期（缓存优化）
     * @return 当前日期
     */
    private LocalDate getCachedDate() {
        // 调用getCachedTimestamp()会自动更新缓存
        getCachedTimestamp();
        return cachedDate;
    }
    
    /**
     * 批量生成分布式自增ID
     * @param businessType 业务类型
     * @param count 生成数量
     * @return 批量生成的ID数组
     */
    @Override
    public long[] generateIds(String businessType, int count) {
        // 1. 校验业务类型参数
        validateBusinessType(businessType);
        
        // 2. 校验批量生成数量
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            log.error("Invalid batch count: {} for business type: {}", count, businessType);
            throw new BadRequestException(String.format("Batch count must be between 1 and %d", MAX_BATCH_COUNT));
        }
        
        try {
            // 3. 生成当天的Redis key
            String key = generateKey(businessType);
            
            // 4. 使用Redis INCRBY获取自增计数器的起始值
            long start = stringRedisTemplate.opsForValue().increment(key, count);
            long end = start + count - 1;
            
            // 5. 检查计数器是否溢出
            if (end > MAX_COUNTER) {
                log.error("ID counter overflow for batch generation, business type: {}, key: {}, start: {}, end: {}", 
                        businessType, key, start, end);
                throw new RuntimeException("ID counter overflow");
            }
            
            // 6. 设置key的过期时间为2天
            if (start == count) {
                // 只有在第一次生成key时设置过期时间
                stringRedisTemplate.expire(key, 2, TimeUnit.DAYS);
                log.debug("Set expire time for key: {} to 2 days", key);
            }
            
            // 7. 获取当天的时间戳（缓存优化）
            long timestamp = getCachedTimestamp();
            
            // 8. 组装ID数组
            long[] ids = new long[count];
            for (int i = 0; i < count; i++) {
                long counter = start + i;
                ids[i] = (timestamp << COUNTER_BITS) | counter;
            }
            
            log.debug("Generated {} IDs for business type: {}, key: {}, timestamp: {}, start: {}, end: {}", 
                    count, businessType, key, timestamp, start, end);
            
            return ids;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure when batch generating IDs for business type: {}", businessType, e);
            throw new RuntimeException("Failed to batch generate IDs due to Redis connection issue", e);
        } catch (BadRequestException e) {
            // 重新抛出参数校验异常
            throw e;
        } catch (Exception e) {
            log.error("Failed to batch generate IDs for business type: {}", businessType, e);
            throw new RuntimeException("Failed to batch generate IDs", e);
        }
    }
    
    /**
     * 校验业务类型参数
     * @param businessType 业务类型
     */
    private void validateBusinessType(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            log.error("Invalid business type: null or empty");
            throw new BadRequestException("Business type cannot be null or empty");
        }
        
        // 业务类型长度限制
        if (businessType.length() > 50) {
            log.error("Invalid business type: too long (max 50 characters)");
            throw new BadRequestException("Business type cannot exceed 50 characters");
        }
        
        // 业务类型只能包含字母、数字、下划线和连字符
        if (!businessType.matches("[a-zA-Z0-9_-]+") ) {
            log.error("Invalid business type: contains illegal characters");
            throw new BadRequestException("Business type can only contain letters, numbers, underscores and hyphens");
        }
    }
}