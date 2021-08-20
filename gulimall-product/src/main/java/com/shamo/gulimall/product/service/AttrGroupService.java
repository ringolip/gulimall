package com.shamo.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.shamo.gulimall.product.entity.AttrEntity;
import com.shamo.gulimall.product.entity.AttrGroupEntity;
import com.shamo.gulimall.product.vo.AttrGroupRelationVO;
import com.shamo.gulimall.product.vo.AttrGroupRespVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取三级分类下的属性分组
     *
     * @param params
     * @param catelogId
     * @return
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取属性分组的关联的所有属性
     *
     * @param attrgroupId
     * @return
     */
    default List<AttrEntity> getAttrRelation(Long attrgroupId) {
        return null;
    }

    /**
     * 删除属性与分组的关联关系
     *
     * @param relationVOList
     */
    void removeAttrRelation(List<AttrGroupRelationVO> relationVOList);

    /**
     * 获取属性分组中未关联但属于当前分类的属性
     *
     * @param attrgroupId
     * @param params
     * @return
     */
    PageUtils getAttrNoRelation(Long attrgroupId, Map<String, Object> params);


    /**
     * 添加属性分组与属性的关联关系
     *
     * @param relationVOList
     */
    void saveAttrRelation(List<AttrGroupRelationVO> relationVOList);

    /**
     * 获取分类下所有属性分组及其关联的属性集合
     *
     * @param catelogId
     * @return
     */
    List<AttrGroupRespVO> getAttrGroupWithAttr(Long catelogId);
}

