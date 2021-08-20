package com.shamo.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.shamo.common.constant.EsConstant;
import com.shamo.common.to.EsSkuTO;
import com.shamo.gulimall.search.service.ElasticProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author ringo
 * @version 1.0
 * @date 2021/8/4 11:17
 */
@Slf4j
@Service
public class ElasticProductServiceImpl implements ElasticProductService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 在ES中保存sku信息集合
     * @param esSkuTOList
     * @return
     */
    @Override
    public boolean saveSkuList(List<EsSkuTO> esSkuTOList) throws IOException {
        // 批量保存skuTO
        BulkRequest bulkRequest = new BulkRequest();

        // 遍历skuTO集合，依次添加保存请求
        for (EsSkuTO esSkuTO : esSkuTOList) {
            // 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(String.valueOf(esSkuTO.getSkuId()));
            String jsonString = JSON.toJSONString(esSkuTO);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        // 执行批量保存
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean failures = bulkResponse.hasFailures();
        // 如果保存失败
        if(failures){
            List<String> failReason = Arrays.stream(bulkResponse.getItems()).map(item -> {
                String id = item.getId();
                return id;
            }).collect(Collectors.toList());
            log.error("Elatic执行批量保存出现错误，{}", failReason);
        }

        return failures;
    }
}
