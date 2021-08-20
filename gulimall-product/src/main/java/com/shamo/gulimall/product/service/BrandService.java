package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface BrandService extends IService<BrandEntity> {

    /**
     * 增加模糊查询功能
     *
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 更新品牌信息，同时更新其他含有品牌信息的冗余字段
     *
     * @param brand
     */
    void updateDetail(BrandEntity brand);

}

