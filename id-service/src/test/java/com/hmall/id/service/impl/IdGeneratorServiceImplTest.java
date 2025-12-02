package com.hmall.id.service.impl;

import com.hmall.id.service.IdGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ID生成服务测试类
 */
@SpringBootTest
class IdGeneratorServiceImplTest {
    
    @Autowired
    private IdGeneratorService idGeneratorService;
    
    @Test
    void testGenerateId() {
        // 测试生成单个ID
        long id = idGeneratorService.generateId("order");
        System.out.println("Generated ID: " + id);
        
        // 验证ID不为0
        assertNotEquals(0, id);
        
        // 验证ID结构：第1位固定为0，中间31位是时间戳，最后32位是自增计数器
        long firstBit = (id >> 63) & 1;
        assertEquals(0, firstBit, "First bit should be 0");
        
        // 测试不同业务类型
        long id2 = idGeneratorService.generateId("user");
        System.out.println("Generated ID for user: " + id2);
        assertNotEquals(0, id2);
        
        // 测试同一业务类型的ID是否递增
        long id3 = idGeneratorService.generateId("order");
        System.out.println("Generated another ID for order: " + id3);
        assertNotEquals(0, id3);
        assertTrue(id3 > id, "ID should be incremental for same business type");
    }
    
    @Test
    void testGenerateIds() {
        // 测试批量生成ID
        int count = 10;
        long[] ids = idGeneratorService.generateIds("order", count);
        System.out.println("Generated " + count + " IDs: ");
        for (long id : ids) {
            System.out.println("  - " + id);
            assertNotEquals(0, id);
        }
        
        // 验证生成的ID数量正确
        assertEquals(count, ids.length);
        
        // 验证ID是否递增
        for (int i = 1; i < ids.length; i++) {
            assertTrue(ids[i] > ids[i-1], "IDs should be incremental in batch generation");
        }
        
        // 测试最大批量数量
        long[] maxIds = idGeneratorService.generateIds("order", 1000);
        assertEquals(1000, maxIds.length);
    }
    
    @Test
    void testValidateBusinessType() {
        // 测试空业务类型
        assertThrows(Exception.class, () -> {
            idGeneratorService.generateId(null);
        });
        
        assertThrows(Exception.class, () -> {
            idGeneratorService.generateId("");
        });
        
        assertThrows(Exception.class, () -> {
            idGeneratorService.generateId("   ");
        });
        
        // 测试非法字符
        assertThrows(Exception.class, () -> {
            idGeneratorService.generateId("order#type");
        });
        
        // 测试过长的业务类型
        String longType = "a".repeat(51);
        assertThrows(Exception.class, () -> {
            idGeneratorService.generateId(longType);
        });
        
        // 测试合法的业务类型
        long id = idGeneratorService.generateId("order-type_123");
        assertNotEquals(0, id);
    }
    
    @Test
    void testPerformance() {
        // 测试性能：生成1000个ID所需时间
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            idGeneratorService.generateId("performance");
        }
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        System.out.println("Generated 1000 IDs in " + elapsed + " ms");
        
        // 验证性能：1000个ID生成时间应该小于1秒
        assertTrue(elapsed < 1000, "1000 IDs should be generated in less than 1 second");
        
        // 测试批量生成性能
        startTime = System.currentTimeMillis();
        idGeneratorService.generateIds("batch-performance", 1000);
        endTime = System.currentTimeMillis();
        elapsed = endTime - startTime;
        System.out.println("Batch generated 1000 IDs in " + elapsed + " ms");
        assertTrue(elapsed < 1000, "Batch generation of 1000 IDs should be completed in less than 1 second");
    }
}