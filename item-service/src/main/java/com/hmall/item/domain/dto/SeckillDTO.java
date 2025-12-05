package com.hmall.item.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "秒杀优惠券实体")
public class SeckillDTO {
    @ApiModelProperty("秒杀商品id")
    private Long id;
    @ApiModelProperty("SKU名称")
    private String name;
    @ApiModelProperty("价格（分）")
    private Integer price;
    @ApiModelProperty("库存数量")
    private Integer stock;
    @ApiModelProperty("商品状态 1-正常，2-下架，3-删除")
    private Integer status;
}