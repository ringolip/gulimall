package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.CategoryBrandRelationEntity;
import com.shamo.gulimall.product.vo.BrandRespVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取品牌关联的分类
     *
     * @param brandId
     * @return
     */
    List<CategoryBrandRelationEntity> getCategoriesList(Long brandId);

    /**
     * 新增品牌的分类关联关系，存储冗余字段
     *
     * @param categoryBrandRelation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新品牌信息
     *
     * @param brandId
     * @param name
     */
    void updateBrandInfo(Long brandId, String name);

    /**
     * 更新分类信息
     *
     * @param catId
     * @param name
     */
    void updateCategoryInfo(Long catId, String name);

    /**
     * 获取分类关联的品牌VO集合
     *
     * @param catId
     * @return
     */
    List<BrandRespVO> getBrandList(Long catId);

}

