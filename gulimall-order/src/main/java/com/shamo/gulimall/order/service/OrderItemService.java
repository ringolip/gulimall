package com.shamo.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:42:34
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
