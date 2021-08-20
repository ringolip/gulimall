package com.shamo.gulimall.product.service.impl;

import com.shamo.common.constant.ProductConstant;
import com.shamo.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.shamo.gulimall.product.entity.AttrEntity;
import com.shamo.gulimall.product.entity.BrandEntity;
import com.shamo.gulimall.product.service.AttrAttrgroupRelationService;
import com.shamo.gulimall.product.service.AttrService;
import com.shamo.gulimall.product.service.BrandService;
import com.shamo.gulimall.product.vo.AttrGroupRelationVO;
import com.shamo.gulimall.product.vo.AttrGroupRespVO;
import com.shamo.gulimall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.product.dao.AttrGroupDao;
import com.shamo.gulimall.product.entity.AttrGroupEntity;
import com.shamo.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private BrandService brandService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取三级分类下的属性分组
     *
     * @param params    分页请求参数
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        // 从请求分页参数中获取查询关键字，构造查询条件
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like("attr_group_id", key).or().like("attr_group_name", key);
        }

        // 三级分类ID为0，即没有选中任何三级分类，查询所有属性分组
        if (catelogId == 0) {
            // 使用IService中的分页查询方法
            // Query从分页请求参数params中获取分页信息，返回Ipage执行查询后的分页信息对象
            // PageUtils封装分页的信息
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
            // 三级分类ID不为0时，查询指定的三级分类的所属属性分组
        } else {
            // 1.构造查询条件QueryWrapper
            wrapper.eq("catelog_id", catelogId);

            // 2.执行查询，返回分页数据
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 获取属性分组的关联的所有属性
     *
     * @param attrgroupId 属性分组ID
     * @return
     */
    @Override
    public List<AttrEntity> getAttrRelation(Long attrgroupId) {
        // 存储属性对象的集合
        List<AttrEntity> attrEntityList = new ArrayList<>();
        // 1.在关系表中获取当前属性分组的记录
        List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrgroupId));
        // 2.如果存在属于当前属性分组的记录，获取所有属性记录
        if (relationEntityList.size() != 0) {
            // 遍历所有记录，获取属性记录
            attrEntityList = relationEntityList.stream().map(relationEntity -> {
                Long attrId = relationEntity.getAttrId();
                AttrEntity attrEntity = attrService.getById(attrId);
                return attrEntity;
            }).collect(Collectors.toList());

            return attrEntityList;
        }

        // 没有关联的属性，返回空集合
        return attrEntityList;

    }

    /**
     * 删除属性与分组的关联关系
     *
     * @param relationVOList
     */
    @Override
    public void removeAttrRelation(List<AttrGroupRelationVO> relationVOList) {
        for (AttrGroupRelationVO VO : relationVOList) {
            attrAttrgroupRelationService.remove(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", VO.getAttrId()));
        }
    }


    /**
     * 获取属性分组中未关联但属于当前分类的属性
     *
     * @param attrgroupId 当前属性分组ID
     * @param params
     * @return
     */
    @Override
    public PageUtils getAttrNoRelation(Long attrgroupId, Map<String, Object> params) {
        // 只能关联当前属性分组所属三级分类下的属性
        Long catelogId = this.getById(attrgroupId).getCatelogId();

        // 获取所有已被当前分组下的属性分组关联的属性
        // 1.获取所属当前分类的属性分组
        List<AttrGroupEntity> attrGroupEntityList = this.list(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId));
        // 2.获取分组ID集合
        List<Long> groupIdList = attrGroupEntityList.stream().map(attrGroupEntity -> {
            Long attrGroupId = attrGroupEntity.getAttrGroupId();
            return attrGroupId;
        }).collect(Collectors.toList());

        // 3.获取所有已经被关联的对象
        List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .in("attr_group_id", groupIdList));
        // 4.获取已经被关联的属性ID集合
        List<Long> relationAttrIdList = relationEntityList.stream().map(relationEntity -> {
            Long attrId = relationEntity.getAttrId();
            return attrId;

        }).collect(Collectors.toList());


        // 获取所有属于当前三级分类，但未被关联的属性
        // 1.构造属于当前三级分类，但未被关联的属性，且为平台属性的查询条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId) //属于当前三级分类
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()); //平台属性

        // 2.保证获取的已被关联的属性ID集合不为空
        if (relationAttrIdList.size() > 0 && relationAttrIdList != null) {
            wrapper.notIn("attr_id", relationAttrIdList); //未被关联的属性
        }

        // 3.根据查询条件，生成分页数据
        IPage<AttrEntity> page = attrService.page(new Query<AttrEntity>().getPage(params), wrapper);

        return new PageUtils(page);

    }

    /**
     * 添加属性分组与属性的关联关系
     *
     * @param relationVOList
     */
    @Override
    public void saveAttrRelation(List<AttrGroupRelationVO> relationVOList) {
        // 将VO转换为属性分组关系对象，并给排序字段设置默认值
        List<AttrAttrgroupRelationEntity> relationEntityList = relationVOList.stream().map(relationVO -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(relationVO, relationEntity);
            relationEntity.setAttrSort(0);
            return relationEntity;
        }).collect(Collectors.toList());

        // 批量保存关系对象
        attrAttrgroupRelationService.saveBatch(relationEntityList);
    }

    /**
     * 获取分类下所有属性分组及其关联的属性集合
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupRespVO> getAttrGroupWithAttr(Long catelogId) {
        // 1.获取所有属于此分类的属性分组
        List<AttrGroupEntity> attrGroupEntityList = this.list(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId));

        // 属性分组集合不为空
        if (attrGroupEntityList != null && attrGroupEntityList.size() > 0) {
            // 2.遍历属性分组，获取属于属性分组的属性集合
            List<AttrGroupRespVO> attrGroupRespVOList = attrGroupEntityList.stream().map(attrGroupEntity -> {

                // 获取属性集合
                List<AttrVO> attrVOList = new ArrayList<>();
                List<AttrEntity> attrEntityList = this.getAttrRelation(attrGroupEntity.getAttrGroupId());

                if (attrEntityList != null && attrEntityList.size() > 0) {
                    attrVOList = attrEntityList.stream().map(attrEntity -> {
                        AttrVO attrVO = new AttrVO();
                        BeanUtils.copyProperties(attrEntity, attrVO);
                        return attrVO;
                    }).collect(Collectors.toList());
                }

                // 3.将属性分组对象转换为VO对象，并为attr属性赋值
                AttrGroupRespVO attrGroupRespVO = new AttrGroupRespVO();
                BeanUtils.copyProperties(attrGroupEntity, attrGroupRespVO);
                attrGroupRespVO.setAttrs(attrVOList);

                // 4.返回属性分组VO
                return attrGroupRespVO;

            }).collect(Collectors.toList());

            return attrGroupRespVOList;
        }

        // 如果没有关联的属性分组，返回空集合
        return new ArrayList<>();
    }


}