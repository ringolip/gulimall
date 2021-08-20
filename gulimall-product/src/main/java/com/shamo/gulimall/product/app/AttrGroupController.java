package com.shamo.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.shamo.gulimall.product.entity.AttrEntity;
import com.shamo.gulimall.product.service.CategoryService;
import com.shamo.gulimall.product.vo.AttrGroupRelationVO;
import com.shamo.gulimall.product.vo.AttrGroupRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.shamo.gulimall.product.entity.AttrGroupEntity;
import com.shamo.gulimall.product.service.AttrGroupService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 属性分组
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 14:40:07
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 获取分类下所有属性分组及其关联的属性集合
     *
     * @param catelogId
     * @return
     */
    @RequestMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttr(@PathVariable("catelogId") Long catelogId) {
        // 获取分类下所有属性分组及其关联的属性集合
        List<AttrGroupRespVO> attrGroupRespVOList = attrGroupService.getAttrGroupWithAttr(catelogId);

        return R.ok().put("data", attrGroupRespVOList);
    }


    /**
     * 添加属性分组与属性的关联关系
     *
     * @param relationVOList
     * @return
     */
    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody List<AttrGroupRelationVO> relationVOList) {
        attrGroupService.saveAttrRelation(relationVOList);
        return R.ok();
    }


    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
     *
     * @param attrgroupId 属性分组ID
     * @return
     */
    @RequestMapping("/{attrgroupId}/noattr/relation")
    public R getAttrNoRelation(@RequestParam Map<String, Object> params,
                               @PathVariable("attrgroupId") Long attrgroupId) {
        // 获取属性分组中未关联但属于当前分类的属性，返回分页对象
        PageUtils page = attrGroupService.getAttrNoRelation(attrgroupId, params);

        return R.ok().put("page", page);
    }

    /**
     * 获取属性分组的关联的所有属性
     *
     * @param attrgroupId
     * @return
     */
    @RequestMapping("/{attrgroupId}/attr/relation")
    public R getAttrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        // 获取属性分组关联的属性集合
        List<AttrEntity> attrEntityList = attrGroupService.getAttrRelation(attrgroupId);

        return R.ok().put("data", attrEntityList);
    }


    /**
     * 获取三级分类下的分类属性分组
     *
     * @param params    分页请求参数
     * @param catelogId 第三级分类ID
     * @return
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 获取属性分组详情
     * 更新属性分组数据时回显
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        // 获取三级分类路径数组
        Long[] paths = categoryService.findCateLogPath(attrGroup.getCatelogId());
        // 将路径属性加入分组属性entity
        attrGroup.setCatelogPath(paths);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }


    /**
     * 删除属性与分组的关联关系
     *
     * @param relationVOList
     * @return
     */
    @RequestMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody List<AttrGroupRelationVO> relationVOList) {
        attrGroupService.removeAttrRelation(relationVOList);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
