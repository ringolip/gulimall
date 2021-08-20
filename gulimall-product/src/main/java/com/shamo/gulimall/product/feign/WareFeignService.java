package com.shamo.gulimall.product.feign;

import com.shamo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/3 16:12
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 远程调用仓储服务，获取sku是否还有库存
     *
     * @param skuIdList
     * @return
     */
    @RequestMapping("/ware/waresku/hasstock")
    R skuHasStock(@RequestBody List<Long> skuIdList);

}
