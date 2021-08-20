package com.shamo.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.shamo.common.to.SkuDiscountTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.coupon.entity.SkuLadderEntity;
import com.shamo.gulimall.coupon.service.SkuLadderService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 商品阶梯价格
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:18:15
 */
@RestController
@RequestMapping("coupon/skuladder")
public class SkuLadderController {
    @Autowired
    private SkuLadderService skuLadderService;

    /**
     * 保存Sku所有优惠信息（打折信息，满减信息，会员价信息）
     *
     * @param skuDiscountTO
     * @return
     */
    @RequestMapping("/save/discount")
    public R saveAllDiscount(@RequestBody SkuDiscountTO skuDiscountTO) {
        skuLadderService.saveAllDiscount(skuDiscountTO);

        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuLadderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SkuLadderEntity skuLadder = skuLadderService.getById(id);

        return R.ok().put("skuLadder", skuLadder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuLadderEntity skuLadder) {
        skuLadderService.save(skuLadder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuLadderEntity skuLadder) {
        skuLadderService.updateById(skuLadder);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        skuLadderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
