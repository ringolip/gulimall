package com.shamo.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shamo.common.constant.ProductConstant;
import com.shamo.common.to.EsSkuTO;
import com.shamo.common.to.SkuDiscountTO;
import com.shamo.common.to.SkuHasStockTO;
import com.shamo.common.to.SpuBoundsTO;
import com.shamo.common.utils.R;
import com.shamo.gulimall.product.entity.*;
import com.shamo.gulimall.product.feign.CouponFeignService;
import com.shamo.gulimall.product.feign.ElasticFeignService;
import com.shamo.gulimall.product.feign.WareFeignService;
import com.shamo.gulimall.product.service.*;
import com.shamo.gulimall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ElasticFeignService elasticFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * ????????????SpuVO??????
     *
     * @param spuSaveVO
     */
    @Transactional // ??????????????????????????????
    @Override
    public void saveSpuInfo(SpuSaveVO spuSaveVO) {
        // 1.??????Spu????????????
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveSpuBaseInfo(spuInfoEntity);

        // 2.??????Spu?????????????????????????????????
        List<String> decript = spuSaveVO.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        // ???????????????????????????????????????
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        // 3.??????Spu?????????????????????
        List<String> images = spuSaveVO.getImages();
        // ??????VO???image?????????SpuImagesEntity???????????????
        if (images != null && images.size() > 0) {
            List<SpuImagesEntity> spuImagesEntityList = images.stream().map(image -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setImgUrl(image);
                spuImagesEntity.setSpuId(spuInfoEntity.getId());
                return spuImagesEntity;
            }).collect(Collectors.toList());
            // ????????????SpuImagesEntity???????????????
            spuImagesService.saveBatch(spuImagesEntityList);
        }

        // 4.??????Spu????????????????????????
        List<BaseAttrs> baseAttrs = spuSaveVO.getBaseAttrs();
        // ???VO???????????????????????????????????????ProductAttrValueEntity???????????????
        if (baseAttrs != null && baseAttrs.size() > 0) {
            List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                String attrName = attrService.getById(baseAttr.getAttrId()).getAttrName();
                productAttrValueEntity.setAttrName(attrName);
                productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            // ????????????ProductAttrValueEntity???????????????
            productAttrValueService.saveBatch(productAttrValueEntityList);
        }

        // 8.??????Spu?????????????????????
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        Bounds bounds = spuSaveVO.getBounds();
        BeanUtils.copyProperties(bounds, spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfoEntity.getId());
        // ???????????????????????????????????????????????????
        R saveBoundsResult = couponFeignService.saveBounds(spuBoundsTO);
        if (saveBoundsResult.getCode() != 0) {
            log.error("??????????????????????????????????????????");
        }


        // 5.??????Sku????????????
        List<Skus> skus = spuSaveVO.getSkus();
        if (skus != null && skus.size() > 0) {
            // ??????Sku??????
            skus.stream().forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());

                // ??????Sku?????????????????????
                List<Images> skuImages = sku.getImages();
                String defalutImage = "";
                if (skuImages != null && skuImages.size() > 0) {
                    for (Images skuImage : skuImages) {
                        if (skuImage.getDefaultImg() == 1) {
                            defalutImage = skuImage.getImgUrl();
                        }
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defalutImage);
                // ????????????Sku??????
                skuInfoService.save(skuInfoEntity);

                // ??????SKUID
                Long skuId = skuInfoEntity.getSkuId();

                // 6.??????Sku??????????????????
                if (skuImages != null && skuImages.size() > 0) {
                    // ???VO??????Sku?????????????????????skuImagesEntity???????????????
                    List<SkuImagesEntity> skuImagesEntityList = skuImages.stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(image, skuImagesEntity);
                        skuImagesEntity.setSkuId(skuId);
                        return skuImagesEntity;
                        // ?????????Sku????????????????????????
                    }).filter(imageEntity -> {
                        return !StringUtils.isEmpty(imageEntity.getImgUrl());
                    }).collect(Collectors.toList());

                    // ????????????Sku??????????????????
                    skuImagesService.saveBatch(skuImagesEntityList);
                }

                // 7.??????Sku??????????????????
                List<Attr> attrList = sku.getAttr();
                if (attrList != null && attrList.size() > 0) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrList.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    // ????????????????????????
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                }

                // 9.??????Sku????????????????????????????????????????????????????????????????????????
                SkuDiscountTO skuDiscountTO = new SkuDiscountTO();
                BeanUtils.copyProperties(sku, skuDiscountTO);
                skuDiscountTO.setSkuId(skuId);
                R saveDiscountResult = couponFeignService.saveDiscount(skuDiscountTO);
                if (saveDiscountResult.getCode() != 0) {
                    log.error("??????????????????Sku??????????????????");
                }

            });
        }


    }

    /**
     * ??????Spu????????????
     *
     * @param spuInfoEntity
     */
    @Override
    public void saveSpuBaseInfo(SpuInfoEntity spuInfoEntity) {
        this.save(spuInfoEntity);

    }

    /**
     * ??????????????????SPU??????
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils getSpuInfoByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!org.springframework.util.StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if (!org.springframework.util.StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!org.springframework.util.StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!org.springframework.util.StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * ????????????
     *
     * @param spuId
     */
    @Override
    public void spuAdd(Long spuId) {
        // ??????sku??????????????????
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.list(
                new QueryWrapper<SkuInfoEntity>()
                        .eq("spu_id", spuId));

        // 0.???????????????????????????spu??????
        // ????????????spu???????????????
        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId));
        // ??????spu???????????????ID??????
        List<Long> productAttrIdList = productAttrValueEntityList.stream().map(productAttrValueEntity -> {
            Long attrId = productAttrValueEntity.getAttrId();
            return attrId;
        }).collect(Collectors.toList());

        // ?????????????????????????????????
        List<EsSkuTO.Attr> attrList = productAttrIdList.stream().map(productAttrId -> {
            AttrEntity attrEntity = attrService.getById(productAttrId);
            return attrEntity;
        }).filter(attrEntity ->
                attrEntity.getSearchType() == 1
        ).map(ableSearchAttrEntity -> {
            Long attrId = ableSearchAttrEntity.getAttrId();
            ProductAttrValueEntity ableSearchProductAttrValueEntity = productAttrValueService.getOne(
                    new QueryWrapper<ProductAttrValueEntity>()
                            .eq("attr_id", attrId));
            EsSkuTO.Attr attr = new EsSkuTO.Attr();
            BeanUtils.copyProperties(ableSearchProductAttrValueEntity, attr);
            return attr;
        }).collect(Collectors.toList());

        // ????????????sku???????????????
        List<Long> skuIdList = skuInfoEntityList.stream().map(skuInfoEntity -> {
            Long skuId = skuInfoEntity.getSkuId();
            return skuId;
        }).collect(Collectors.toList());

        // ??????????????????????????????
        List<SkuHasStockTO> skuHasStockTOList = null;
        try {
            R r = wareFeignService.skuHasStock(skuIdList);
            Object data = r.get("data");
            // ????????????LinkedHashMap????????????????????????????????????
            String jsonString = JSON.toJSONString(data);
            skuHasStockTOList = JSON.parseObject(jsonString, new TypeReference<List<SkuHasStockTO>>() {});

            if (r.getCode() != 0) {
                log.error("??????????????????????????????");
            }

        } catch (Exception e) {
            log.error("?????????????????????????????????{}", e);
        }


        // ??????sku??????????????????
        List<SkuHasStockTO> finalSkuHasStockTOList = skuHasStockTOList;
        List<EsSkuTO> esSkuTOList = skuInfoEntityList.stream().map(skuInfoEntity -> {
            // 1.??????sku????????????
            EsSkuTO esSkuTO = new EsSkuTO();
            BeanUtils.copyProperties(skuInfoEntity, esSkuTO);
            esSkuTO.setSkuPrice(skuInfoEntity.getPrice());
            esSkuTO.setSkuImg(skuInfoEntity.getSkuDefaultImg());

            // 2.?????????????????????????????????sku????????????
            if (finalSkuHasStockTOList == null) { // ?????????????????????????????????????????????sku?????????
                esSkuTO.setHasStock(true);
            } else {
                for (SkuHasStockTO skuHasStockTO : finalSkuHasStockTOList) {
                    if (skuHasStockTO.getSkuId() == skuInfoEntity.getSkuId()) {
                        esSkuTO.setHasStock(skuHasStockTO.isHasStock());
                    }
                }
            }

            // 3.??????sku????????????
            // ????????????????????????0
            esSkuTO.setHotScore(0L);

            // 4.???????????????????????????
            BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
            esSkuTO.setBrandName(brandEntity.getName());
            esSkuTO.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
            esSkuTO.setCatalogName(categoryEntity.getName());

            // 5.??????sku??????sku??????
            esSkuTO.setAttrs(attrList);

            // ??????esSkuTO??????
            return esSkuTO;
        }).collect(Collectors.toList());


        // 6.???????????????????????????sku???????????????ES
        R r = elasticFeignService.saveSkuList(esSkuTOList);

        // ES??????sku???????????????????????????????????????
        if (r.getCode() == 0) {
            // ????????????????????????
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.ProductStatusEnum.SPU_UP.getCode());
            this.updateById(spuInfoEntity);
        } else {
            // ????????????
            log.error("????????????ES??????sku??????????????????");
        }
    }

}