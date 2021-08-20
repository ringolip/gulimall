package com.shamo.gulimall.product.vo;

import lombok.Data;

/**
 * SpuInfo检索VO
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/21 14:37
 */
@Data
public class SpuInfoRetrieveVO {
    /**
     * 检索关键字
     */
    private String key;

    /**
     * 三级分类id
     */
    private Long catelogId;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 商品状态
     */
    private Integer status;
}
