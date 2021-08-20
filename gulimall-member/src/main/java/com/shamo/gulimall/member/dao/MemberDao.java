package com.shamo.gulimall.member.dao;

import com.shamo.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:34:10
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
