package com.shamo.common.constant;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/22 09:56
 */
public class WareConstant {
    /**
     * 采购单状态枚举类
     */
    public enum PurchaseStatusEnum {
        CREATED(0, "新建"), ASSIGNED(1, "已分配"),
        RECEIVED(2, "已领取"), FINISH(3, "已完成"),
        ERROR(4, "有异常");


        private final Integer code;
        private final String msg;

        // 私有化构造方法
        private PurchaseStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        // getter方法，供外部调用
        public Integer getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }
    }

    /**
     * 采购需求状态枚举类
     */
    public enum PurchaseDetailStatusEnum {
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISH(3,"已完成"),
        ERROR(4,"采购失败");

        private final Integer code;
        private final String msg;

        private PurchaseDetailStatusEnum(Integer code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
