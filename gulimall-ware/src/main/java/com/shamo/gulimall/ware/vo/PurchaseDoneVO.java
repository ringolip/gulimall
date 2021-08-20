package com.shamo.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/22 13:17
 */
@Data
public class PurchaseDoneVO {
    /**
     * 采购单ID
     */
    private Long id;

    /**
     * 采购需求集合
     */
    private List<PurchaseDetailItem> items;

    @Data
    public static class PurchaseDetailItem{

        /**
         * 采购需求ID
         */
        private Long itemId;

        /**
         * 采购状态
         */
        private Integer status;

        /**
         * 状态的原因
         */
        private String reason;
    }

}
