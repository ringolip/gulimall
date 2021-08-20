package com.shamo.gulimall.product.vo;

import lombok.Data;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/10 16:04
 */
@Data
public class AttrGroupRelationVO {

    /**
     * 属性ID
     */
    private Long attrId;

    /**
     * 属性分组ID
     */
    private Long attrGroupId;
}
