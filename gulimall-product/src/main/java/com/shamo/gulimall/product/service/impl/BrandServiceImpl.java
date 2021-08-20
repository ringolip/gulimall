package com.shamo.gulimall.product.service.impl;

import com.shamo.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.product.dao.BrandDao;
import com.shamo.gulimall.product.entity.BrandEntity;
import com.shamo.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 1.获取查询参数中的查询关键字
        String key = (String) params.get("key");
        // 2.如果有关键字，返回带有关键字查询的分页数据
        if (!StringUtils.isEmpty(key)) {
            QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
            wrapper.like("brand_id", key).or().like("name", key);

            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    wrapper
            );

            return new PageUtils(page);
        } else {
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<BrandEntity>()
            );

            return new PageUtils(page);
        }

    }


    /**
     * 更新品牌信息，同时更新其他含有品牌信息的冗余字段
     *
     * @param brand
     */
    @Transactional // 多个表操作，使用事务
    @Override
    public void updateDetail(BrandEntity brand) {
        // 1.更新品牌信息
        this.updateById(brand);
        // 2.更新品牌分类关系表的冗余字段
        categoryBrandRelationService.updateBrandInfo(brand.getBrandId(), brand.getName());
        // TODO 更新其他表的品牌冗余字段
    }
}