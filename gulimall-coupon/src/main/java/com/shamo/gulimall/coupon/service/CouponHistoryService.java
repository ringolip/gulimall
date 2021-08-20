package com.shamo.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:12:46
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

