package com.shamo.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.to.SkuHasStockTO;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:48:51
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据条件查询商品库存
     * @param params
     * @return
     */
    PageUtils getSkuWareInfoByCondition(Map<String, Object> params);

    /**
     * 为商品添加库存
     * @param skuId
     * @param wareId
     * @param stock
     */
    void addStock(Long skuId, Long wareId, Integer stock);

    /**
     * 批量查询sku集合是否有库存
     *
     * @param skuIdList
     * @return
     */
    List<SkuHasStockTO>

    skuHasStock(List<Long> skuIdList);
}

