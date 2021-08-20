package com.shamo.gulimall.product.feign;

import com.shamo.common.to.EsSkuTO;
import com.shamo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 远程调用商品服务
 *
 * @author ringo
 * @version 1.0
 * @date 2021/8/4 12:09
 */
@FeignClient("gulimall-search")
public interface ElasticFeignService {

    /**
     * 将上架的sku集合保存至ES中
     * @param esSkuTOList
     * @return
     */
    @RequestMapping("/elastic/product/save")
    public R saveSkuList(@RequestBody List<EsSkuTO> esSkuTOList);
}
