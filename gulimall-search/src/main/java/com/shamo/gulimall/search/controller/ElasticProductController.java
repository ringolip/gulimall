package com.shamo.gulimall.search.controller;

import com.shamo.common.exception.BizCodeEnum;
import com.shamo.common.to.EsSkuTO;
import com.shamo.common.utils.R;
import com.shamo.gulimall.search.service.ElasticProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/4 11:05
 */
@Slf4j
@RestController
@RequestMapping("elastic/product")
public class ElasticProductController {

    @Autowired
    private ElasticProductService elasticProductService;

    /**
     * 在ES中保存上架的sku信息集合
     */
    @RequestMapping("/save")
    public R saveSkuList(@RequestBody List<EsSkuTO> esSkuTOList){
        boolean fail = false;

        try {
            // 在ES中保存sku信息集合
            fail = elasticProductService.saveSkuList(esSkuTOList);
        } catch (IOException e) {
            log.error("ElasticProductController商品上架错误，{}", e);
            return R.error(BizCodeEnum.PRODUCT_ADDED_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_ADDED_EXCEPTION.getMsg());
        }

        // Sku信息集合成功保存至ES
        if(!fail){
            return R.ok();
        }
        // 保存失败
        return R.error(BizCodeEnum.PRODUCT_ADDED_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_ADDED_EXCEPTION.getMsg());
    }
}
