package com.shamo.gulimall.product.thread.bean;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/13 17:42
 */
public class ReentrantTicket {
    private Integer count = 100;
    private final ReentrantLock lock = new ReentrantLock(true); // 公平锁


    public void saleticket(){
        lock.lock();
        try {
            if(count>0){
                System.out.println(Thread.currentThread().getName() + "卖出票，剩余" + --count + "张票");
            }
        } finally {
            lock.unlock();
        }

    }
}
