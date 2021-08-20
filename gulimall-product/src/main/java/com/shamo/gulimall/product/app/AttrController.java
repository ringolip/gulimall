package com.shamo.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.shamo.gulimall.product.entity.ProductAttrValueEntity;
import com.shamo.gulimall.product.vo.AttrRespVO;
import com.shamo.gulimall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.product.service.AttrService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 商品属性
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 14:40:07
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;


    /**
     * 修改Spu的基本属性
     *
     * @param spuId
     * @param attrValueEntityList
     * @return
     */
    @RequestMapping("/update/{spuId}")
    public R updateSpuBaseAttrs(@PathVariable("spuId") Long spuId,
                                @RequestBody List<ProductAttrValueEntity> attrValueEntityList) {

        // 修改Spu的基本属性
        attrService.updateSpuBaseAttrs(spuId, attrValueEntityList);

        return R.ok();
    }

    /**
     * 获取Spu的规格参数
     *
     * @param spuId
     * @return
     */
    @RequestMapping("/base/listforspu/{spuId}")
    public R getSpuBaseAttr(@PathVariable("spuId") Long spuId) {

        // 获取spu的规格参数
        List<ProductAttrValueEntity> productAttrValueEntityList = attrService.getSpuBaseAttr(spuId);
        return R.ok().put("data", productAttrValueEntityList);
    }

    /**
     * 获取分类规格参数列表
     *
     * @param params
     * @param catelogId
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId,
                  @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryPage(params, catelogId, attrType);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 获取属性详情，用于更新属性时回显属性信息
     *
     * @param attrId
     * @return
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {

        AttrRespVO attrRespVO = attrService.getAttrVO(attrId);

        return R.ok().put("attr", attrRespVO);
    }

    /**
     * 新增属性，同时保存属性分组ID
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attrVO) {
        // 新增属性，同时保存属性分组ID
        attrService.save(attrVO);

        return R.ok();
    }

    /**
     * 修改属性详情，同步修改属性分组关系表的属性分组ID
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attrVO) {
        // 修改属性详情，同步修改属性分组关系表的属性分组ID
        attrService.updateAttrVO(attrVO);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
