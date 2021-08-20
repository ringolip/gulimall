package com.shamo.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.ware.entity.PurchaseEntity;
import com.shamo.gulimall.ware.vo.MergeVO;
import com.shamo.gulimall.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:48:51
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询未领取的采购单
     *
     * @param params
     * @return
     */
    PageUtils getUnreceivedPurchaseOrder(Map<String, Object> params);

    /**
     * 合并采购需求至采购单
     *
     * @param mergeVO
     */
    void mergePurchaseDetail(MergeVO mergeVO);

    /**
     * 采购人员领取采购单
     *
     * @param purchaseIdList
     */
    void receivedPurchase(List<Long> purchaseIdList);

    /**
     * 完成采购
     *
     * @param purchaseDoneVO
     */
    void purchaseDone(PurchaseDoneVO purchaseDoneVO);
}

