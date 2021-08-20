package com.shamo.gulimall.member.feign;

import com.shamo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/1 10:40
 */
@FeignClient("gulimall-coupon") // 远程服务名称
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list") // 远程方法名称
    public R memberCoupons();
}
