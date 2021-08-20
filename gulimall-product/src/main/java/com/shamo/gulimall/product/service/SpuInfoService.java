package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.SpuInfoEntity;
import com.shamo.gulimall.product.vo.SpuInfoRetrieveVO;
import com.shamo.gulimall.product.vo.SpuSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存所有SpuVO信息
     *
     * @param spuSaveVO
     */
    void saveSpuInfo(SpuSaveVO spuSaveVO);

    /**
     * 保存Spu基本信息
     *
     * @param spuInfoEntity
     */
    void saveSpuBaseInfo(SpuInfoEntity spuInfoEntity);

    /**
     * 根据条件检索SPU信息
     *
     * @param params
     * @return
     */
    PageUtils getSpuInfoByCondition(Map<String, Object> params);

    /**
     * 商品上架
     *
     * @param spuId
     */
    void spuAdd(Long spuId);
}

