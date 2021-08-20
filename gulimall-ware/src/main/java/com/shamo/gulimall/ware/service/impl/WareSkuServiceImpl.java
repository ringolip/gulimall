package com.shamo.gulimall.ware.service.impl;

import com.shamo.common.to.SkuHasStockTO;
import com.shamo.common.utils.R;
import com.shamo.gulimall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.ware.dao.WareSkuDao;
import com.shamo.gulimall.ware.entity.WareSkuEntity;
import com.shamo.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据条件查询商品库存
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils getSkuWareInfoByCondition(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");

        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 为商品添加库存
     *
     * @param skuId
     * @param wareId
     * @param stock
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer stock) {
        // 1.如果之前没有商品库存记录，则新增
        WareSkuEntity entity = this.getOne(
                new QueryWrapper<WareSkuEntity>()
                        .eq("sku_id", skuId)
                        .eq("ware_id", wareId));
        if (entity == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(stock);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            // 远程调用获取Sku名称
            try {
                R skuInfo = productFeignService.info(skuId);
                if (skuInfo.getCode() == 0) {
                    Map<String, Object> map = (Map<String, Object>) skuInfo.get("skuInfo");
                    String skuName = (String) map.get("skuName");
                    wareSkuEntity.setSkuName(skuName);
                }
            } catch (Exception e) {

            }


            this.save(wareSkuEntity);
        } else {
            // 2.有库存记录，则更新库存
            // 查出原库存，加上先有库存
            Integer originalStock = entity.getStock();
            entity.setStock(originalStock + stock);
            this.updateById(entity);
        }

    }

    /**
     * 批量查询sku集合是否有库存
     *
     * @param skuIdList skuID集合
     * @return
     */
    @Override
    public List<SkuHasStockTO> skuHasStock(List<Long> skuIdList) {
        List<SkuHasStockTO> skuHasStockTOList = skuIdList.stream().map(skuId -> {
            // 获取sku的总库存（库存减去锁定库存）
            Integer stock = this.getBaseMapper().getStock(skuId);
            // 返回TO
            SkuHasStockTO skuHasStockTO = new SkuHasStockTO();
            skuHasStockTO.setSkuId(skuId);
            skuHasStockTO.setHasStock(stock == null ? false : stock > 0);
            return skuHasStockTO;
        }).collect(Collectors.toList());

        return skuHasStockTOList;
    }

}