package com.shamo.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品积分TO，用于远程调用传输的对象
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/20 15:05
 */
@Data
public class SpuBoundsTO {
    /**
     * SPUID
     */
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
}
