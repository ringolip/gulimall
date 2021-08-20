package com.shamo.gulimall.product.feign;

import com.shamo.common.to.SkuDiscountTO;
import com.shamo.common.to.SpuBoundsTO;
import com.shamo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 优惠服务的远程调用
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/20 14:59
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 远程调用优惠服务，保存Spu的积分信息
     *
     * @param spuBoundsTO
     * @return
     */
    @RequestMapping("/coupon/spubounds/save")
    R saveBounds(@RequestBody SpuBoundsTO spuBoundsTO);

    /**
     * 远程调用优惠服务，保存所有Sku优惠信息
     *
     * @param skuDiscountTO
     * @return
     */
    @RequestMapping("/coupon/skuladder/save/discount")
    R saveDiscount(@RequestBody SkuDiscountTO skuDiscountTO);
}
