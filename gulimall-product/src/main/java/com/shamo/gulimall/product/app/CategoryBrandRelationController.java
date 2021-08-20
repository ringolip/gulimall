package com.shamo.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.shamo.gulimall.product.vo.BrandRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.product.entity.CategoryBrandRelationEntity;
import com.shamo.gulimall.product.service.CategoryBrandRelationService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 14:40:07
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取分类关联的品牌
     *
     * @param catId 分类ID
     * @return
     */
    @RequestMapping("/brands/list")
    public R getBrandList(@RequestParam("catId") Long catId) {
        // 获取分类关联的品牌VO集合
        List<BrandRespVO> brandRespVOList = categoryBrandRelationService.getBrandList(catId);


        return R.ok().put("data", brandRespVOList);
    }

    /**
     * 获取品牌关联的分类
     *
     * @param brandId
     * @return
     */
    @RequestMapping("/catelog/list")
    public R list(@RequestParam("brandId") Long brandId) {
        // 获取品牌关联的分类集合
        List<CategoryBrandRelationEntity> categoriesList = categoryBrandRelationService.getCategoriesList(brandId);

        return R.ok().put("data", categoriesList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 新增品牌的分类关联关系，存储冗余字段
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        // 同时保存品牌名和分类名的冗余字段
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
