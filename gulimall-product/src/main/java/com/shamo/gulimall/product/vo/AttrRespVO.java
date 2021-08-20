package com.shamo.gulimall.product.vo;

import lombok.Data;

/**
 * 获取分类规则参数列表时，返回的VO
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/9 14:34
 */
@Data
public class AttrRespVO extends AttrVO {
    // 所属分类名字
    private String catelogName;

    // 所属分组名字
    private String groupName;

    // 属性所属分类完整路径
    private Long[] catelogPath;

}
