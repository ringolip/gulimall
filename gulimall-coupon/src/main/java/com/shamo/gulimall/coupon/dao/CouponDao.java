package com.shamo.gulimall.coupon.dao;

import com.shamo.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:12:46
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
