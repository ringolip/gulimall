package com.shamo.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/21 18:54
 */
@Data
public class MergeVO {
    /**
     * 采购单ID
     */
    private Long purchaseId;

    /**
     * 采购需求ID集合
     */
    private List<Long> items;
}
