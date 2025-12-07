package com.hmall.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hmall.common.utils.BeanUtils;

import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.domain.po.OrderDetail;
import com.hmall.trade.domain.vo.OrderDetailVO;
import com.hmall.trade.domain.vo.OrderVO;
import com.hmall.trade.service.IOrderDetailService;
import com.hmall.trade.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;

    @ApiOperation("根据id查询订单")
    @GetMapping("{id}")
    public OrderDetailVO queryOrderById(@Param ("订单id")@PathVariable("id") Long orderId) {
        Order order = orderService.getById(orderId);
        // 使用Lambda方式查询订单详情
        OrderDetail orderDetail = orderDetailService.getOne(
                Wrappers.<OrderDetail>lambdaQuery()
                        .eq(OrderDetail::getOrderId, orderId)
        );
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order,orderDetailVO);
        BeanUtils.copyProperties(orderDetail,orderDetailVO);
        return orderDetailVO;
    }

    @ApiOperation("创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}
