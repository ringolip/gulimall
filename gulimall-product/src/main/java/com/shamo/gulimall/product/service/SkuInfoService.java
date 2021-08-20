package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.SkuInfoEntity;
import com.shamo.gulimall.product.vo.SpuInfoRetrieveVO;

import java.util.Map;

/**
 * sku信息
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据条件检索Spu信息
     *
     * @param params
     * @return
     */
    PageUtils getSkuInfoByCondition(Map<String, Object> params);
}

