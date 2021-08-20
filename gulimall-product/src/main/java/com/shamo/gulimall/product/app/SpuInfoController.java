package com.shamo.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.shamo.gulimall.product.vo.SpuSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.product.entity.SpuInfoEntity;
import com.shamo.gulimall.product.service.SpuInfoService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * spu信息
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 14:40:07
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 商品上架
     *
     * @param spuId
     * @return
     */
    @RequestMapping("/{spuId}/up")
    public R spuAdded(@PathVariable("spuId") Long spuId) {
        // 商品上架，远程调用检索服务，将上架的sku信息存储至ES
        spuInfoService.spuAdd(spuId);
        return R.ok();
    }

    /**
     * 根据条件检索SPU信息
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = spuInfoService.getSpuInfoByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 新增商品
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVO spuSaveVO) {
//		spuInfoService.save(spuInfo);
        // 保存商品所有VO信息
        spuInfoService.saveSpuInfo(spuSaveVO);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo) {
        spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
