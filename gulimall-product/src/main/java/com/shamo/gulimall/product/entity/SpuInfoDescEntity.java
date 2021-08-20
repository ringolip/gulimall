package com.shamo.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu信息介绍
 * 
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 11:55:07
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type = IdType.INPUT) // 数据库主键不是自增，添加数据时自行set主键值
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
