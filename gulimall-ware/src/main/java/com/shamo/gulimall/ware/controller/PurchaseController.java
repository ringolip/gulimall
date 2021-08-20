package com.shamo.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.shamo.gulimall.ware.vo.MergeVO;
import com.shamo.gulimall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.ware.entity.PurchaseEntity;
import com.shamo.gulimall.ware.service.PurchaseService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 采购信息
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:48:51
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 采购员完成采购
     *
     * @param purchaseDoneVO
     * @return
     */
    @RequestMapping("/done")
    public R purchaseDone(@RequestBody PurchaseDoneVO purchaseDoneVO){

        // 完成采购
        purchaseService.purchaseDone(purchaseDoneVO);

        return R.ok();
    }

    /**
     * 采购人员领取采购单
     *
     * @param purchaseIdList 领取的采购单ID集合
     * @return
     */
    @RequestMapping("/received")
    public R receivedPurchase(@RequestBody List<Long> purchaseIdList) {
        // 采购人员领取采购单
        purchaseService.receivedPurchase(purchaseIdList);

        return R.ok();
    }


    /**
     * 合并采购需求至采购单
     *
     * @param mergeVO
     * @return
     */
    @RequestMapping("/merge")
    public R mergePurchaseDetail(@RequestBody MergeVO mergeVO) {
        // 合并采购需求至采购单
        purchaseService.mergePurchaseDetail(mergeVO);

        return R.ok();
    }

    /**
     * 查询未领取的采购单
     */
    @RequestMapping("/unreceive/list")
    public R getUnreceivedPurchaseOrder(@RequestParam Map<String, Object> params) {
        // 查询未领取的采购单
        PageUtils page = purchaseService.getUnreceivedPurchaseOrder(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
