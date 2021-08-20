package com.shamo.gulimall.ware.service.impl;

import com.shamo.common.constant.WareConstant;
import com.shamo.gulimall.ware.entity.PurchaseDetailEntity;
import com.shamo.gulimall.ware.service.PurchaseDetailService;
import com.shamo.gulimall.ware.service.WareSkuService;
import com.shamo.gulimall.ware.vo.MergeVO;
import com.shamo.gulimall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.ware.dao.PurchaseDao;
import com.shamo.gulimall.ware.entity.PurchaseEntity;
import com.shamo.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单,采购单状态是新建或已分配
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils getUnreceivedPurchaseOrder(Map<String, Object> params) {


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);

    }


    /**
     * 合并采购需求至采购单
     *
     * @param mergeVO
     */
    @Override
    public void mergePurchaseDetail(MergeVO mergeVO) {

        // 1.采购单ID为null时，新建采购单
        if (mergeVO.getPurchaseId() == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            // 设置采购单的状态为新建
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            // 新建采购单
            this.save(purchaseEntity);
            // 采购单ID设置为新创建的采购单ID
            mergeVO.setPurchaseId(purchaseEntity.getId());
        }

        // 2.添加采购需求对象的采购单ID属性，修改状态为已分配
        List<Long> items = mergeVO.getItems();
        List<PurchaseDetailEntity> purchaseDetailEntityList = items.stream().map(detailId -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            // 设置采购需求的ID，采购单ID
            purchaseDetailEntity.setId(detailId);
            purchaseDetailEntity.setPurchaseId(mergeVO.getPurchaseId());
            // 采购需求的状态设置为已分配
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        // 3.批量修改采购状态的信息
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        // 4.更新采购单的更新时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(mergeVO.getPurchaseId());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

    /**
     * 采购人员领取采购单
     *
     * @param purchaseIdList
     */
    @Override
    public void receivedPurchase(List<Long> purchaseIdList) {
        // 1.只能领取状态为新建或已分配的采购单
        List<PurchaseEntity> purchaseEntityList = purchaseIdList.stream().map(purchaseId -> {
            PurchaseEntity purchaseEntity = this.getById(purchaseId);
            return purchaseEntity;
        }).filter(purchaseEntity -> {
            return purchaseEntity.getStatus() ==
                    WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
            // 修改采购单更新时间，状态为已领取
        }).map(purchaseEntity -> {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            purchaseEntity.setUpdateTime(new Date());
            return purchaseEntity;
        }).collect(Collectors.toList());

        // 2.批量更新采购单状态
        this.updateBatchById(purchaseEntityList);

        // 3.更新采购单关联的采购需求状态
        purchaseIdList.stream().forEach(purchaseId -> {
            // 获取采购单关联的采购需求集合
            List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.list(
                    new QueryWrapper<PurchaseDetailEntity>()
                            .eq("purchase_id", purchaseId));
            // 修改采购需求的状态
            List<PurchaseDetailEntity> updateStatusDetailList = purchaseDetailEntityList.stream().map(detailEntity -> {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());

            // 批量修改采购需求的状态
            purchaseDetailService.updateBatchById(updateStatusDetailList);
        });

    }

    /**
     * 完成采购
     *
     * @param purchaseDoneVO
     */
    @Transactional
    @Override
    public void purchaseDone(PurchaseDoneVO purchaseDoneVO) {
        // 采购单状态标志位
        Boolean statusFlag = true;
        List<PurchaseDetailEntity> purchaseDetailEntityList = new ArrayList<>();

        // 1.修改采购需求状态
        List<PurchaseDoneVO.PurchaseDetailItem> items = purchaseDoneVO.getItems();

        for (PurchaseDoneVO.PurchaseDetailItem purchaseDetailItem : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();

            // 如果有采购需求采购失败，将flag状态设置为采购失败
            if (purchaseDetailItem.getStatus() == WareConstant.PurchaseDetailStatusEnum.ERROR.getCode()) {
                statusFlag = false;
                purchaseDetailEntity.setStatus(purchaseDetailItem.getStatus());
            } else {
                purchaseDetailEntity.setStatus(purchaseDetailItem.getStatus());
                // 3.将采购成功的采购需求入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(purchaseDetailItem.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());

            }

            purchaseDetailEntity.setId(purchaseDetailItem.getItemId());
            purchaseDetailEntityList.add(purchaseDetailEntity);

        }

        // 批量更新采购需求状态
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        // 2.修改采购单状态
        Long id = purchaseDoneVO.getId();
        PurchaseEntity purchaseEntity = this.getById(id);
        // 标志位为true则修改采购单状态为完成，false则修改状态为有异常
        purchaseEntity.setStatus(statusFlag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);


    }


}