package com.shamo.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 满减信息TO，用于远程传输传输的对象
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/20 15:47
 */
@Data
public class SkuDiscountTO {
    /**
     * SKUID
     */
    private Long skuId;

    /**
     * Sku优惠信息
     */
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
