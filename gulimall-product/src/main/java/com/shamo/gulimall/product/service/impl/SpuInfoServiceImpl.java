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
     * 保存所有SpuVO信息
     *
     * @param spuSaveVO
     */
    @Transactional // 操作多张表，使用事务
    @Override
    public void saveSpuInfo(SpuSaveVO spuSaveVO) {
        // 1.保存Spu基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveSpuBaseInfo(spuInfoEntity);

        // 2.保存Spu商品介绍的图片数组信息
        List<String> decript = spuSaveVO.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        // 将集合转换为字符串进行保存
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        // 3.保存Spu图片集数组信息
        List<String> images = spuSaveVO.getImages();
        // 转换VO的image对象为SpuImagesEntity实体类对象
        if (images != null && images.size() > 0) {
            List<SpuImagesEntity> spuImagesEntityList = images.stream().map(image -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setImgUrl(image);
                spuImagesEntity.setSpuId(spuInfoEntity.getId());
                return spuImagesEntity;
            }).collect(Collectors.toList());
            // 批量保存SpuImagesEntity实体类对象
            spuImagesService.saveBatch(spuImagesEntityList);
        }

        // 4.保存Spu基本属性数组信息
        List<BaseAttrs> baseAttrs = spuSaveVO.getBaseAttrs();
        // 将VO中的基本属性数组对象转换为ProductAttrValueEntity实体类对象
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
            // 批量保存ProductAttrValueEntity实体类对象
            productAttrValueService.saveBatch(productAttrValueEntityList);
        }

        // 8.保存Spu的商品积分信息
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        Bounds bounds = spuSaveVO.getBounds();
        BeanUtils.copyProperties(bounds, spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfoEntity.getId());
        // 远程调用优惠服务，保存商品积分信息
        R saveBoundsResult = couponFeignService.saveBounds(spuBoundsTO);
        if (saveBoundsResult.getCode() != 0) {
            log.error("远程调用保存商品积分信息出错");
        }


        // 5.保存Sku基本信息
        List<Skus> skus = spuSaveVO.getSkus();
        if (skus != null && skus.size() > 0) {
            // 遍历Sku对象
            skus.stream().forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());

                // 获取Sku对象的默认图片
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
                // 逐个保存Sku对象
                skuInfoService.save(skuInfoEntity);

                // 当前SKUID
                Long skuId = skuInfoEntity.getSkuId();

                // 6.保存Sku图片信息数组
                if (skuImages != null && skuImages.size() > 0) {
                    // 将VO重的Sku图片对象转换为skuImagesEntity实体类对象
                    List<SkuImagesEntity> skuImagesEntityList = skuImages.stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(image, skuImagesEntity);
                        skuImagesEntity.setSkuId(skuId);
                        return skuImagesEntity;
                        // 过滤掉Sku空的图片路径对象
                    }).filter(imageEntity -> {
                        return !StringUtils.isEmpty(imageEntity.getImgUrl());
                    }).collect(Collectors.toList());

                    // 批量保存Sku图片信息数组
                    skuImagesService.saveBatch(skuImagesEntityList);
                }

                // 7.保存Sku销售属性信息
                List<Attr> attrList = sku.getAttr();
                if (attrList != null && attrList.size() > 0) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrList.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    // 批量保存销售信息
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                }

                // 9.保存Sku的所有优惠信息（打折信息，满减信息，会员价信息）
                SkuDiscountTO skuDiscountTO = new SkuDiscountTO();
                BeanUtils.copyProperties(sku, skuDiscountTO);
                skuDiscountTO.setSkuId(skuId);
                R saveDiscountResult = couponFeignService.saveDiscount(skuDiscountTO);
                if (saveDiscountResult.getCode() != 0) {
                    log.error("远程调用保存Sku优惠信息出错");
                }

            });
        }


    }

    /**
     * 保存Spu基本信息
     *
     * @param spuInfoEntity
     */
    @Override
    public void saveSpuBaseInfo(SpuInfoEntity spuInfoEntity) {
        this.save(spuInfoEntity);

    }

    /**
     * 根据条件检索SPU信息
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
     * 商品上架
     *
     * @param spuId
     */
    @Override
    public void spuAdd(Long spuId) {
        // 获取sku基本信息集合
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.list(
                new QueryWrapper<SkuInfoEntity>()
                        .eq("spu_id", spuId));

        // 0.获取所有可被检索的spu属性
        // 获取所有spu的属性集合
        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId));
        // 获取spu所有属性的ID集合
        List<Long> productAttrIdList = productAttrValueEntityList.stream().map(productAttrValueEntity -> {
            Long attrId = productAttrValueEntity.getAttrId();
            return attrId;
        }).collect(Collectors.toList());

        // 获取其中可被检索的属性
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

        // 获取所有sku的库存信息
        List<Long> skuIdList = skuInfoEntityList.stream().map(skuInfoEntity -> {
            Long skuId = skuInfoEntity.getSkuId();
            return skuId;
        }).collect(Collectors.toList());

        // 远程调用仓储服务成功
        List<SkuHasStockTO> skuHasStockTOList = null;
        try {
            R r = wareFeignService.skuHasStock(skuIdList);
            Object data = r.get("data");
            // 将获取的LinkedHashMap内容转换为真正可用的内容
            String jsonString = JSON.toJSONString(data);
            skuHasStockTOList = JSON.parseObject(jsonString, new TypeReference<List<SkuHasStockTO>>() {});

            if (r.getCode() != 0) {
                log.error("远程调用仓储服务出错");
            }

        } catch (Exception e) {
            log.error("远程调用仓储服务出错，{}", e);
        }


        // 遍历sku基本属性集合
        List<SkuHasStockTO> finalSkuHasStockTOList = skuHasStockTOList;
        List<EsSkuTO> esSkuTOList = skuInfoEntityList.stream().map(skuInfoEntity -> {
            // 1.设置sku基本信息
            EsSkuTO esSkuTO = new EsSkuTO();
            BeanUtils.copyProperties(skuInfoEntity, esSkuTO);
            esSkuTO.setSkuPrice(skuInfoEntity.getPrice());
            esSkuTO.setSkuImg(skuInfoEntity.getSkuDefaultImg());

            // 2.远程调用仓储服务，获取sku库存信息
            if (finalSkuHasStockTOList == null) { // 如果远程调用仓储服务失败，设置sku有库存
                esSkuTO.setHasStock(true);
            } else {
                for (SkuHasStockTO skuHasStockTO : finalSkuHasStockTOList) {
                    if (skuHasStockTO.getSkuId() == skuInfoEntity.getSkuId()) {
                        esSkuTO.setHasStock(skuHasStockTO.isHasStock());
                    }
                }
            }

            // 3.设置sku热度评分
            // 热度评分先设置为0
            esSkuTO.setHotScore(0L);

            // 4.获取品牌和分类信息
            BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
            esSkuTO.setBrandName(brandEntity.getName());
            esSkuTO.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
            esSkuTO.setCatalogName(categoryEntity.getName());

            // 5.设置sku所属sku属性
            esSkuTO.setAttrs(attrList);

            // 返回esSkuTO对象
            return esSkuTO;
        }).collect(Collectors.toList());


        // 6.远程调用检索服务将sku集合保存至ES
        R r = elasticFeignService.saveSkuList(esSkuTOList);

        // ES保存sku集合成功，修改商品上架状态
        if (r.getCode() == 0) {
            // 修改商品上架状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.ProductStatusEnum.SPU_UP.getCode());
            this.updateById(spuInfoEntity);
        } else {
            // 保存失败
            log.error("远程服务ES保存sku信息集合失败");
        }
    }

}