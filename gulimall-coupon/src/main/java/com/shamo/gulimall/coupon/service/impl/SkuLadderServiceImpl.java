package com.shamo.gulimall.coupon.service.impl;

import com.shamo.common.to.MemberPrice;
import com.shamo.common.to.SkuDiscountTO;
import com.shamo.gulimall.coupon.entity.MemberPriceEntity;
import com.shamo.gulimall.coupon.entity.SkuFullReductionEntity;
import com.shamo.gulimall.coupon.service.MemberPriceService;
import com.shamo.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.coupon.dao.SkuLadderDao;
import com.shamo.gulimall.coupon.entity.SkuLadderEntity;
import com.shamo.gulimall.coupon.service.SkuLadderService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderDao, SkuLadderEntity> implements SkuLadderService {

    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuLadderEntity> page = this.page(
                new Query<SkuLadderEntity>().getPage(params),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存Sku所有优惠信息（打折信息，满减信息，会员价信息
     *
     * @param skuDiscountTO
     */
    @Transactional
    @Override
    public void saveAllDiscount(SkuDiscountTO skuDiscountTO) {
        // 1.保存Sku打折信息
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuDiscountTO, skuLadderEntity);
        skuLadderEntity.setAddOther(skuDiscountTO.getCountStatus());
        // 未设置的打折信息不保存
        if (skuDiscountTO.getFullCount() > 0) {
            this.save(skuLadderEntity);
        }


        // 2.保存Sku满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuDiscountTO, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuDiscountTO.getPriceStatus());
        // 未设置的满减信息不保存
        if (skuDiscountTO.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            skuFullReductionService.save(skuFullReductionEntity);
        }


        // 3.保存Sku会员价信息
        List<MemberPrice> memberPriceList = skuDiscountTO.getMemberPrice();
        if (memberPriceList != null && memberPriceList.size() > 0) {
            List<MemberPriceEntity> memberPriceEntityList = memberPriceList.stream().map(memberPrice -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setMemberLevelId(memberPrice.getId());
                memberPriceEntity.setMemberLevelName(memberPrice.getName());
                memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                memberPriceEntity.setAddOther(1);
                memberPriceEntity.setSkuId(skuDiscountTO.getSkuId());
                return memberPriceEntity;
            // 过滤掉未设置的会员价信息
            }).filter(priceEntity -> {
                return priceEntity.getMemberPrice().compareTo(BigDecimal.ZERO) == 1;
            }).collect(Collectors.toList());

            // 批量保存会员价格信息
            memberPriceService.saveBatch(memberPriceEntityList);
        }
    }

}