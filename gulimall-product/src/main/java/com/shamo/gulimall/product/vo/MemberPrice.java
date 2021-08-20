/**
 * Copyright 2021 json.cn
 */
package com.shamo.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2021-07-16 16:48:56
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class MemberPrice {

    /**
     * 会员等级ID
     */
    private Long id;

    private String name;
    private BigDecimal price;

}