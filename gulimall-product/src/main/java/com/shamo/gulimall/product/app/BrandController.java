package com.shamo.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.shamo.common.valid.AddGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shamo.gulimall.product.entity.BrandEntity;
import com.shamo.gulimall.product.service.BrandService;
import com.shamo.common.utils.PageUtils;
import com.shamo.common.utils.R;


/**
 * 品牌
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 14:40:07
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 获取所有品牌，返回分页数据
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        // 增加模糊查询功能
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @Valid开启校验
    // @BindingResult获取校验结果
    // @Validated指定校验新增分组的字段
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand){
//		// 存储校验结果
//		Map<String, String> resultMap = new HashMap<>();
//        // 获取校验错误信息
//		if(validateResult.hasErrors()){
//		    validateResult.getFieldErrors().forEach((item) -> {
//                String field = item.getField();
//                String message = item.getDefaultMessage();
//                resultMap.put(field, message);
//            });
//
//		    return R.error(400, "提交的数据未通过校验").put("data", resultMap);
//        }

        brandService.save(brand);
        return R.ok();
    }

    /**
     * 更新品牌信息，同时更新其他含有品牌信息的冗余字段
     */
    @RequestMapping("/update")
    public R update(@Validated @RequestBody BrandEntity brand){
//		brandService.updateById(brand);
        // 更新冗余字段
        brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 只修改显示状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
