package com.shamo.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.to.SkuDiscountTO;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:12:46
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存Sku所有优惠信息（打折信息，满减信息，会员价信息
     *
     * @param skuDiscountTO
     */
    void saveAllDiscount(SkuDiscountTO skuDiscountTO);
}

