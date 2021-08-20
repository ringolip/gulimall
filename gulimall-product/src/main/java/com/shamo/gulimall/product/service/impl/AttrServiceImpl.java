package com.shamo.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shamo.common.constant.ProductConstant;
import com.shamo.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.shamo.gulimall.product.entity.AttrGroupEntity;
import com.shamo.gulimall.product.entity.ProductAttrValueEntity;
import com.shamo.gulimall.product.service.*;
import com.shamo.gulimall.product.vo.AttrRespVO;
import com.shamo.gulimall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

import com.shamo.gulimall.product.dao.AttrDao;
import com.shamo.gulimall.product.entity.AttrEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private ProductAttrValueService productAttrValueService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 新增属性，同时保存属性分组ID
     *
     * @param attrVO
     */
    @Transactional //多个表操作，使用事务
    @Override
    public void save(AttrVO attrVO) {
        // 1.保存新增的属性信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO, attrEntity);
        this.save(attrEntity);
        // 2.保存属性分组ID至属性和属性分组关联表中
        // 新增属性为销售属性时不需要将属性保存至关联表
        // 如果新增的平台属性未添加属性分组ID，也不需要保存属性关系
        if (attrVO.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                && attrVO.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attrVO.getAttrGroupId());
            relationEntity.setAttrSort(0);
            attrAttrgroupRelationService.save(relationEntity);
        }
    }

    /**
     * 获取分类参数列表，根据属性类型判断返回规格参数还是销售属性
     *
     * @param params
     * @param catelogId
     * @param attrType  属性类型
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId, String attrType) {
        // 1.根据属性类型base/sale构造查询条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(attrType) ? 1 : 0);

        // 2.构造关键字查询条件
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like("attr_id", key).or().like("attr_name", key);
        }

        // 3.如果三级分类ID为0，就是获取全部规格参数
        if (catelogId == 0) {
            IPage<AttrEntity> page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    wrapper
            );

            // 获取查询出的属性对象集合
            List<AttrEntity> attrEntityList = page.getRecords();
            // 遍历属性对象集合，构造AttrRespVO集合
            List<AttrRespVO> attrRespVOList = getAttrRespVOList(attrEntityList);

            // 将分页对象的列表数据从entity集合替换为VO集合
            PageUtils pageUtils = new PageUtils(page);
            pageUtils.setList(attrRespVOList);
            return pageUtils;
            // 4.三级分类ID不为0，就根据三级分类查询其属性
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrEntity> page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    wrapper
            );

            // 获取查询出的属性对象集合
            List<AttrEntity> attrEntityList = page.getRecords();
            // 遍历对象集合，构造AttrRespVO集合
            List<AttrRespVO> attrRespVOList = getAttrRespVOList(attrEntityList);
            // 将分页对象的entity集合替换为VO集合，并返回
            PageUtils pageUtils = new PageUtils(page);
            pageUtils.setList(attrRespVOList);
            return pageUtils;
        }
    }

    /**
     * 获取属性详情，用于更新属性时回显属性信息
     *
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVO getAttrVO(Long attrId) {
        // 1.获取属性详情的基本信息
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVO attrRespVO = new AttrRespVO();
        BeanUtils.copyProperties(attrEntity, attrRespVO);
        // 2.为VO设置分组属性ID
        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrId));
        // 防止有些属性未关联属性分组关系
        if (relationEntity != null) {
            Long attrGroupId = relationEntity.getAttrGroupId();
            attrRespVO.setAttrGroupId(attrGroupId);
        }

        // 3. 为VO设置所属分类路径
        Long[] cateLogPath = categoryService.findCateLogPath(attrEntity.getCatelogId());
        attrRespVO.setCatelogPath(cateLogPath);

        return attrRespVO;
    }

    /**
     * 同步修改属性分组关系表的属性分组ID
     *
     * @param attrVO
     */
    @Transactional // 多表操作，使用事务
    @Override
    public void updateAttrVO(AttrVO attrVO) {
        // 1.更新属性的基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO, attrEntity);
        this.update(attrEntity, new UpdateWrapper<AttrEntity>()
                .eq("attr_id", attrVO.getAttrId()));
        // 2.更新属性分组关系表的分组属性ID
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attrVO.getAttrId());
        relationEntity.setAttrGroupId(attrVO.getAttrGroupId());

        // 3.如果未查出关系表中的记录说明之前未添加关系，则做新增操作
        int count = attrAttrgroupRelationService.count(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrVO.getAttrId()));
        if (count > 0) {
            // 否则正常更新
            attrAttrgroupRelationService.update(relationEntity,
                    new UpdateWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrVO.getAttrId()));
        } else {
            // 新增关系
            attrAttrgroupRelationService.save(relationEntity);
        }


    }

    /**
     * 获取Spu的规格参数
     *
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> getSpuBaseAttr(Long spuId) {

        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId));
        return productAttrValueEntityList;
    }


    /**
     * 修改Spu的基本属性
     *
     * @param spuId
     * @param attrValueEntityList
     */
    @Transactional
    @Override
    public void updateSpuBaseAttrs(Long spuId, List<ProductAttrValueEntity> attrValueEntityList) {
        // 1.删除Spu之前的基本属性
        productAttrValueService.remove(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId));

        // 保存新的基本属性
        List<ProductAttrValueEntity> collect = attrValueEntityList.stream().map(attrValueEntity -> {
            attrValueEntity.setSpuId(spuId);
            return attrValueEntity;
        }).collect(Collectors.toList());
        // 2.批量保存
        productAttrValueService.saveBatch(collect);
    }

    /**
     * 将AttrEntity转换为AttrRespVO集合
     * 并为AttrRespVO添加catelogName，groupName属性值（销售属性没有属性分组，不需要获取）
     *
     * @param attrEntityList
     * @return
     */
    private List<AttrRespVO> getAttrRespVOList(List<AttrEntity> attrEntityList) {
        List<AttrRespVO> attrRespVOList = attrEntityList.stream().map(entity -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(entity, attrRespVO);

            // 1.添加分类名称属性值
            String categoryName = categoryService.getById(entity.getCatelogId()).getName();
            attrRespVO.setCatelogName(categoryName);

            // 2.添加属性分组名称属性值
            // 销售属性没有属性分组，不需要获取
            if (attrRespVO.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                Long attrId = entity.getAttrId();
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrId));
                // 防止关系表中未关联关系数据，或者添加新属性时未关联属性分组
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    Long attrGroupId = relationEntity.getAttrGroupId();
                    String attrGroupName = attrGroupService.getById(attrGroupId).getAttrGroupName();
                    attrRespVO.setGroupName(attrGroupName);
                } else {
                    attrRespVO.setGroupName("未关联");
                }
            }

            return attrRespVO;
        }).collect(Collectors.toList());

        return attrRespVOList;
    }

}