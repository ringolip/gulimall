package com.shamo.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * ES需要保存的Sku信息
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/30 18:29
 */

@Data
public class EsSkuTO {
    /**
     * Sku基本信息
     */
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;

    /**
     * sku是否有库存，仓储服务
     */
    private boolean hasStock;

    /**
     * 热度评分
     */
    private Long hotScore;

    /**
     * 品牌分类信息
     */
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;

    /**
     * Spu属性信息
     */
    private List<Attr> attrs;

    /**
     * sku所属spu属性
     */
    @Data
    public static class Attr{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
