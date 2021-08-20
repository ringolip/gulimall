package com.shamo.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级分类VO，用于首页显示
 *
 * @author ringo
 * @version 1.0
 * @date 2021/8/5 16:41
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryLevel2VO {
    /**
     * 一级分类ID
     */
    private String catalog1Id;

    /**
     * 三级分类集合
     */
    private List<CategoryLevel3VO> catalog3List;

    /**
     * 二级分类ID
     */
    private String id;

    /**
     * 二级分类名称
     */
    private String name;


    /**
     * 三级分类VO
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class CategoryLevel3VO {
        /**
         * 二级分类ID
         */
        private String catalog2Id;

        /**
         * 三级分类ID
         */
        private String id;

        /**
         * 三级分类名称
         */
        private String name;
    }
}
