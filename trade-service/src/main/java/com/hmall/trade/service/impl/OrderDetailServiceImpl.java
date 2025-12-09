package com.hmall.trade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.utils.BeanUtils;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.domain.po.OrderDetail;
import com.hmall.trade.domain.vo.ItemVO;
import com.hmall.trade.domain.vo.OrderDetailVO;
import com.hmall.trade.mapper.OrderDetailMapper;
import com.hmall.trade.service.IOrderDetailService;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author yinjianli
 * @since 2023-05-05
 */
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {
    @Override
    public OrderDetailVO getOrderDetail(Order order) {
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order,orderDetailVO);
        // 查询订单详情列表（可能多条）
        List<OrderDetail> orderDetails = list(
                Wrappers.<OrderDetail>lambdaQuery()
                        .eq(OrderDetail::getOrderId, order.getId())
        );
        // 转换OrderDetail列表为ItemVO列表
        List<ItemVO> itemVOS = orderDetails.stream()
                .map(this::convertToItemVO)
                .collect(Collectors.toList());
        orderDetailVO.setItems(itemVOS);
        return orderDetailVO;
    }
    private ItemVO convertToItemVO(OrderDetail orderDetail) {
        ItemVO itemVO = new ItemVO();
        itemVO.setItemId(orderDetail.getItemId());
        itemVO.setNum(orderDetail.getNum());
        itemVO.setName(orderDetail.getName());
        itemVO.setSpec(orderDetail.getSpec());
        itemVO.setPrice(orderDetail.getPrice());
        itemVO.setImage(orderDetail.getImage());
        return itemVO;
    }
}
