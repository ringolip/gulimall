package com.shamo.gulimall.product.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.product.entity.CategoryEntity;
import com.shamo.gulimall.product.service.CategoryService;
import com.shamo.common.utils.R;



/**
 * 商品三级分类
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 14:40:06
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类以及子分类，并返回json树形结构
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> entities = categoryService.listWithTree();

        return R.ok().put("data", entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改菜单，用于拖拽功能
     * @param categories
     * @return
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] categories){
        categoryService.updateBatchById(Arrays.asList(categories));
        return R.ok();
    }

    /**
     * 更新分类信息，同时更新其他表含有的分类信息字段
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		// 同时更新其他表的冗余分类字段
        categoryService.updateDetail(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
        // TODO 检查当前菜单是否被引用
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
