package com.shamo.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * ้่ดงๅๅ 
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:42:34
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

