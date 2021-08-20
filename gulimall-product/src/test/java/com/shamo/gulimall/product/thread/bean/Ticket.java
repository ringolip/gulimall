package com.shamo.gulimall.product.thread.bean;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/13 16:54
 */
public class Ticket {
    private Integer count = 100;

    public void saleticket(){
        if (count > 0){
            System.out.println(Thread.currentThread().getName() + "卖出票，剩余" + --count + "张票");

        }

    }
}
