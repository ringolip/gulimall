package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.AttrEntity;
import com.shamo.gulimall.product.entity.ProductAttrValueEntity;
import com.shamo.gulimall.product.vo.AttrRespVO;
import com.shamo.gulimall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 新增属性，同时保存属性分组ID
     *
     * @param attrVO
     */
    void save(AttrVO attrVO);

    /**
     * 获取分类规格参数列表
     *
     * @param params
     * @param catelogId
     * @return
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId, String attrType);

    /**
     * 获取属性详情，用于更新属性时回显属性信息
     *
     * @param attrId
     * @return
     */
    AttrRespVO getAttrVO(Long attrId);

    /**
     * 同步修改属性分组关系表的属性分组ID
     *
     * @param attrVO
     */
    void updateAttrVO(AttrVO attrVO);

    /**
     * 获取Spu的规格参数
     *
     * @param spuId
     * @return
     */
    List<ProductAttrValueEntity> getSpuBaseAttr(Long spuId);

    /**
     * 修改Spu的基本属性
     *
     * @param spuId
     * @param attrValueEntityList
     */
    void updateSpuBaseAttrs(Long spuId, List<ProductAttrValueEntity> attrValueEntityList);
}

