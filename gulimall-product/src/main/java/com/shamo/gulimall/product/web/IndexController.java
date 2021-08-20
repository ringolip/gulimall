package com.shamo.gulimall.product.web;

import com.shamo.gulimall.product.entity.CategoryEntity;
import com.shamo.gulimall.product.service.CategoryService;
import com.shamo.gulimall.product.vo.CategoryLevel2VO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.MacSpi;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/5 16:08
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 首页显示
     *
     * @param model
     * @return
     */
    @RequestMapping({"/", "index.html"})
    public String getIndex(Model model) {
        // 获取所有一级分类集合
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categories();
        model.addAttribute("categories", categoryEntityList);

        return "index";
    }

    /**
     * 获取所有key为一级分类ID，value为二级分类VO集合的Map
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/catalog.json")
    public Map<String, List<CategoryLevel2VO>> getLevel2Categories() {
        // 获取所有key为一级分类ID，value为二级分类VO集合的Map
        Map<String, List<CategoryLevel2VO>> level2CategoryMap = categoryService.getLevel2Categories();

        return level2CategoryMap;
    }

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        RLock lock = redissonClient.getLock("redisson-lock");
        // 加锁：在redis存入lock，value为uuid+当前线程号，并为锁提供自动续期机制
        // 线程没有获得锁就循环尝试获取锁
        lock.lock(5, TimeUnit.SECONDS);
        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 解锁
        lock.unlock();
        System.out.println("解锁...");
        return "hello";
    }
}
