package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.CategoryEntity;
import com.shamo.gulimall.product.vo.CategoryLevel2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取所有一级分类以及子分类
     *
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * 获取分类完整路径数组
     *
     * @param catelogId
     * @return
     */
    Long[] findCateLogPath(Long catelogId);

    /**
     * 更新分类信息，同时更新其他表含有的分类信息字段
     *
     * @param category
     */
    void updateDetail(CategoryEntity category);

    /**
     * 获取所有一级分类集合
     *
     * @return
     */
    List<CategoryEntity> getLevel1Categories();

    /**
     * 获取所有key为一级分类ID，value为二级分类VO集合的Map
     *
     * @return
     */
    Map<String, List<CategoryLevel2VO>> getLevel2Categories();
}

