/**
 * Copyright 2021 json.cn
 */
package com.shamo.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-07-16 16:48:56
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class Skus {

    /**
     * 销售属性集合
     */
    private List<Attr> attr;

    /**
     * Sku基本信息
     */
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;

    /**
     * Sku图集
     */
    private List<Images> images;

    private List<String> descar;

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