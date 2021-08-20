package com.shamo.gulimall.order.dao;

import com.shamo.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:42:34
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
