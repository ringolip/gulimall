package com.shamo.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shamo.common.utils.PageUtils;
import com.shamo.gulimall.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author shamo
 * @email zhangringo@gmail.com
 * @date 2021-06-30 17:48:51
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

