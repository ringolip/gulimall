package com.shamo.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.shamo.common.to.SkuHasStockTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.ware.entity.WareSkuEntity;
import com.shamo.gulimall.ware.service.WareSkuService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 商品库存
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:48:51
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 批量查询sku是否有库存
     *
     * @param skuIdList skuID集合
     * @return
     */
    @RequestMapping("/hasstock")
    public R skuHasStock(@RequestBody List<Long> skuIdList) {

        /**
         * 查询sku集合是否有库存
         */
        List<SkuHasStockTO> skuHasStockTOList = wareSkuService.skuHasStock(skuIdList);
        return R.ok().put("data", skuHasStockTOList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        // 根据条件查询商品库存
        PageUtils page = wareSkuService.getSkuWareInfoByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
