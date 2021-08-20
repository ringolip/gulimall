package com.shamo.gulimall.search.service;

import com.shamo.common.to.EsSkuTO;

import java.io.IOException;
import java.util.List;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/4 11:16
 */
public interface ElasticProductService {

    /**
     * 在ES中保存sku信息集合
     *
     * @param esSkuTOList
     * @return
     */
    boolean saveSkuList(List<EsSkuTO> esSkuTOList) throws IOException;

}
