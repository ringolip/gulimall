package com.shamo.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.shamo.gulimall.product.service.CategoryBrandRelationService;
import com.shamo.gulimall.product.vo.CategoryLevel2VO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.Query;

import com.shamo.gulimall.product.dao.CategoryDao;
import com.shamo.gulimall.product.entity.CategoryEntity;
import com.shamo.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取所有一级分类以及子分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {

        // 获取所有分类
        List<CategoryEntity> categoryEntities = this.list();

        // 组装为父子结构
        List<CategoryEntity> level1CategoryList = categoryEntities.stream()
                // 1.获取所有一级分类
                .filter(entity -> entity.getParentCid().equals(0L))
                // 2.为一级分类添加子分类
                .map(level1category -> {
                    level1category.setChildren(getChildrenNodes(level1category, categoryEntities));
                    return level1category;
                })
                // 3.为一级分类排序
                .sorted((level1Category1, level1Category2) -> {
                    return (level1Category1.getSort() == null ? 0 : level1Category1.getSort()) -
                            (level1Category2.getSort() == null ? 0 : level1Category2.getSort());
                })
                // 4.返回包装好的一级分类
                .collect(Collectors.toList());
        return level1CategoryList;
    }

    /**
     * 获取三级分类的路径数组
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCateLogPath(Long catelogId) {
        // 1.收集三级分类路径的集合
        List<Long> paths = new ArrayList<>();
        // 2.获取三级分类路径
        paths = findParentPath(catelogId, paths);
        // 3.将路径集合元素的顺序反转
        Collections.reverse(paths);
        // 4.将集合转换为数组返回
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * 更新分类信息，同时更新其他表含有的分类信息字段
     *
     * @param category
     */
    @Transactional // 多个表进行操作，使用事务
    @Override
    public void updateDetail(CategoryEntity category) {
        // 1.更新分类信息
        this.updateById(category);
        // 2.更新品牌分类关系表中分类信息的冗余字段
        categoryBrandRelationService.updateCategoryInfo(category.getCatId(), category.getName());
        // TODO 更新其他表中含有分类信息的冗余字段
    }

    /**
     * 获取所有一分类集合
     *
     * @return
     */
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> level1CategoryEntityList = this.list(
                new QueryWrapper<CategoryEntity>()
                        .eq("parent_cid", 0));
        return level1CategoryEntityList;
    }


    /**
     * 从缓存中获取分类信息
     * 获取所有key为一级分类ID，value为二级分类VO集合的Map
     *
     * @return
     */
    @Override
    public Map<String, List<CategoryLevel2VO>> getLevel2Categories() {
        // 1.从缓存中获取分类信息
        String level2CategoriesJsonString = stringRedisTemplate.opsForValue().get("level2Categories");

        // 2.缓存中没有数据，则从数据库查询，并存入缓存
        if (StringUtils.isEmpty(level2CategoriesJsonString)) {
            System.out.println("缓存不命中...查询数据库...");
            // 调用方法，从数据库获取分类信息，此时可能有多个线程调用方法
            Map<String, List<CategoryLevel2VO>> level2CategoriesFromDb = getLevel2CategoriesWithDistributedLock();
            return level2CategoriesFromDb;
        }

        System.out.println("缓存命中...");
        // 3.将获取的JSON字符串还原为分类对象返回
        Map<String, List<CategoryLevel2VO>> level2Categories = JSON.parseObject(level2CategoriesJsonString, new TypeReference<Map<String, List<CategoryLevel2VO>>>() {
        });
        return level2Categories;
    }


    /**
     * 通过分布式锁查询数据库的分类信息，保证集群情况下只有一个线程获取分类信息
     *
     * @return
     */
    public Map<String, List<CategoryLevel2VO>> getLevel2CategoriesWithDistributedLock() {
        // 当前线程尝试获取分布式锁
        // 同时设置锁的过期时间，保证锁在线程执行业务代码时不会过期，将锁的过期时间设置长一些
        // 将锁的value设置为能代表该线程的token
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        // 成功获得锁
        if (lock) {
            System.out.println("成功获得锁...");
            Map<String, List<CategoryLevel2VO>> level2CategoriesFromDb;
            try {
                // 1.获取锁成功后，从数据库查询分类信息
                // 执行此业务时，出现异常情况执行不到释放锁，可能导致死锁（解决：设置锁自动过期时间）
                level2CategoriesFromDb = getLevel2CategoriesFromDb();

            } finally {
                // 2.释放锁
                // 业务代码执行时间长，删除锁时锁可能已经过期（解决：删除前查看是否是自己的锁）
                // 删除锁时，刚好过去，可能删了别人的锁（解决：删除操作也比必须是原子的，需要执行lua脚本）
                String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                // 执行lua脚本，删除锁
                stringRedisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Arrays.asList("lock"), uuid);
                System.out.println("释放锁...");
            }

            return level2CategoriesFromDb;
        } else {
            // 循环尝试获取锁
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Map<String, List<CategoryLevel2VO>> level2CategoriesWithDistributedLock = getLevel2CategoriesWithDistributedLock();
            return level2CategoriesWithDistributedLock;
        }
    }

    /**
     * 防止缓存击穿，大量请求数据库，为方法加锁
     * 从数据库获取分类信息
     * 优化服务性能，避免频繁查询数据库，只查询一次数据库获取所有分类对象
     * 获取所有key为一级分类ID，value为二级分类VO集合的Map
     *
     * @return
     */
    public Map<String, List<CategoryLevel2VO>> getLevel2CategoriesFromDb() {
        // 0.线程得到锁以后，再确认一次缓存中是否有分类数据
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String level2CategoriesJsonString = ops.get("level2Categories");
        if (!StringUtils.isEmpty(level2CategoriesJsonString)) {
            // 将获取的JSON字符串还原为分类对象返回
            Map<String, List<CategoryLevel2VO>> level2Categories = JSON.parseObject(level2CategoriesJsonString, new TypeReference<Map<String, List<CategoryLevel2VO>>>() {
            });
            return level2Categories;
        }

        System.out.println("查询了数据库...");
        // 只查询一次数据库，获取全部分类对象
        List<CategoryEntity> allCategoryEntityList = this.list(new QueryWrapper<>());

        // 1.获取所有一级分类的分类ID集合
//        List<CategoryEntity> level1Categories = this.getLevel1Categories();
        // 优化
        List<CategoryEntity> level1Categories = allCategoryEntityList.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        }).collect(Collectors.toList());

        List<Long> level1CatIdList = level1Categories.stream().map(level1Category -> {
            Long catId = level1Category.getCatId();
            return catId;
        }).collect(Collectors.toList());


        // 存储二级分类集合的Map
        Map<String, List<CategoryLevel2VO>> categoryMap = new HashMap<>();

        // 2.遍历所有一级分类ID获取所有一级分类下的二级分类集合
        for (Long level1CatId : level1CatIdList) {
            // 2.1.获取所有该一级分类下的二级分类集合
//            List<CategoryEntity> categoryLevel2EntityList = this.list(
//                    new QueryWrapper<CategoryEntity>()
//                            .eq("parent_cid", level1CatId));
            // 优化
            List<CategoryEntity> categoryLevel2EntityList = allCategoryEntityList.stream().filter(categoryEntity -> {
                return categoryEntity.getParentCid() == level1CatId;
            }).collect(Collectors.toList());

            // 2.2.将二级分类entity集合转换为二级分类VO集合
            List<CategoryLevel2VO> categoryLevel2VOList = categoryLevel2EntityList.stream().map(categoryLevel2Entity -> {
                CategoryLevel2VO categoryLevel2VO = new CategoryLevel2VO();
                categoryLevel2VO.setCatalog1Id(String.valueOf(level1CatId));
                categoryLevel2VO.setId(String.valueOf(categoryLevel2Entity.getCatId()));
                categoryLevel2VO.setName(categoryLevel2Entity.getName());

                // 2.3.获取所有二级分类下的三级分类集合
//                List<CategoryEntity> categoryLevel3EntityList = this.list(
//                        new QueryWrapper<CategoryEntity>()
//                                .eq("parent_cid", categoryLevel2Entity.getCatId()));
                // 优化
                List<CategoryEntity> categoryLevel3EntityList = allCategoryEntityList.stream().filter(categoryEntity -> {
                    return categoryEntity.getParentCid() == categoryLevel2Entity.getCatId();
                }).collect(Collectors.toList());

                // 2.4.将三级分类entity集合转换为三级分类VO集合
                List<CategoryLevel2VO.CategoryLevel3VO> categoryLevel3VOList = categoryLevel3EntityList.stream().map(categoryLevel3Entity -> {
                    CategoryLevel2VO.CategoryLevel3VO categoryLevel3VO = new CategoryLevel2VO.CategoryLevel3VO();
                    categoryLevel3VO.setCatalog2Id(String.valueOf(categoryLevel2Entity.getCatId()));
                    categoryLevel3VO.setId(String.valueOf(categoryLevel3Entity.getCatId()));
                    categoryLevel3VO.setName(categoryLevel3Entity.getName());
                    return categoryLevel3VO;
                }).collect(Collectors.toList());
                categoryLevel2VO.setCatalog3List(categoryLevel3VOList);

                // 2.5.返回转换好的二级分类VO
                return categoryLevel2VO;
            }).collect(Collectors.toList());

            // 3.将二级分类VO集合加入Map中
            categoryMap.put(String.valueOf(level1CatId), categoryLevel2VOList);
        }

        // 将分类信息转换为JSON字符串，存入缓存
        level2CategoriesJsonString = JSON.toJSONString(categoryMap);
        ops.set("level2Categories", level2CategoriesJsonString);

        // 4.返回组装好的二级分类Map
        return categoryMap;
    }

    /**
     * 获取当前三级分类的父分类
     *
     * @param catelogId
     * @param paths
     * @return
     */
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 1.将当前分类ID加入集合
        paths.add(catelogId);
        // 2.获取当前三级分类entity
        CategoryEntity categoryEntity = this.getById(catelogId);
        // 3.递归获取父分类ID
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }

        return paths;
    }


    /**
     * 获取父分类的所有子分类
     *
     * @param root         父分类
     * @param allCategorys 所有分类
     * @return 所有子分类
     */
    private List<CategoryEntity> getChildrenNodes(CategoryEntity root, List<CategoryEntity> allCategorys) {

        List<CategoryEntity> childrenNodes = allCategorys.stream()
                // 1. 获取所有父分类的下一级分类
                .filter(entity -> entity.getParentCid().equals(root.getCatId()))
                // 2.为子分类设置下一级子分类，并返回
                .map(childCategory -> {
                    // 递归查找子分类
                    childCategory.setChildren(getChildrenNodes(childCategory, allCategorys));
                    return childCategory;
                })
                // 3.为子分类排序
                .sorted((category1, category2) -> {
                    // 防止排序字段为null
                    return (category1.getSort() == null ? 0 : category1.getSort()) -
                            (category2.getSort() == null ? 0 : category2.getSort());
                })
                // 4.返回包装好的集合
                .collect(Collectors.toList());

        return childrenNodes;
    }

}