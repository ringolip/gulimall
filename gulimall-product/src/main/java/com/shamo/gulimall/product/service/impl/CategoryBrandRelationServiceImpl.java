package com.shamo.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shamo.gulimall.product.entity.CategoryEntity;
import com.shamo.gulimall.product.service.BrandService;
import com.shamo.gulimall.product.service.CategoryService;
import com.shamo.gulimall.product.vo.BrandRespVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.product.dao.CategoryBrandRelationDao;
import com.shamo.gulimall.product.entity.CategoryBrandRelationEntity;
import com.shamo.gulimall.product.service.CategoryBrandRelationService;

import javax.validation.constraints.NotBlank;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取品牌关联的分类
     *
     * @param brandId
     * @return
     */
    @Override
    public List<CategoryBrandRelationEntity> getCategoriesList(Long brandId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = this.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return categoryBrandRelationEntityList;
    }

    /**
     * 新增品牌的分类关联关系，存储冗余字段
     *
     * @param categoryBrandRelation
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        // 1.为对象保存品牌名
        Long brandId = categoryBrandRelation.getBrandId();
        String brandName = brandService.getById(brandId).getName();
        categoryBrandRelation.setBrandName(brandName);
        // 2.为对象保存分类名
        Long catelogId = categoryBrandRelation.getCatelogId();
        String categoryName = categoryService.getById(catelogId).getName();
        categoryBrandRelation.setCatelogName(categoryName);
        // 3.保存附带冗余字段值的分类关联关系对象
        this.save(categoryBrandRelation);
    }

    /**
     * 更新品牌信息
     *
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrandInfo(Long brandId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);
        // 更新品牌信息
        this.update(relationEntity,
                new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    /**
     * 更新分类信息
     *
     * @param catId
     * @param name
     */
    @Override
    public void updateCategoryInfo(Long catId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setCatelogId(catId);
        relationEntity.setCatelogName(name);
        // 更新分类信息
        this.update(relationEntity,
                new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
    }

    /**
     * 获取分类关联的品牌VO集合
     *
     * @param catId
     * @return
     */
    @Override
    public List<BrandRespVO> getBrandList(Long catId) {
        // 1.获取所有分类为此分类的对象集合
        List<CategoryBrandRelationEntity> relationEntityList = this.list(
                new QueryWrapper<CategoryBrandRelationEntity>()
                        .eq("catelog_id", catId));

        // 2.获取此分类关联的VO集合
        if(relationEntityList!=null && relationEntityList.size()>0){
            List<BrandRespVO> brandRespVOList = relationEntityList.stream().map(relationEntity -> {
                BrandRespVO brandRespVO = new BrandRespVO();
                BeanUtils.copyProperties(relationEntity, brandRespVO);
                return brandRespVO;
            }).collect(Collectors.toList());
            return brandRespVOList;
        }

        return new ArrayList<>();
    }


}