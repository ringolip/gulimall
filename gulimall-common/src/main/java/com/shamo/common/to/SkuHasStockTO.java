package com.shamo.common.to;

import lombok.Data;

/**
 * 判断SKU是否有库存的TO
 *
 * @author ringo
 * @version 1.0
 * @date 2021/8/3 15:47
 */
@Data
public class SkuHasStockTO {

    /**
     * SKUID
     */
    private Long skuId;
    /**
     * SKU是否有库存
     */
    private boolean hasStock;

}
