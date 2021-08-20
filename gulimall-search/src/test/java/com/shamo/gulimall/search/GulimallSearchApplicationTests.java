package com.shamo.gulimall.search;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @AllArgsConstructor
    @Data
    public class User{
        private String name;
        private Integer age;
        private String gender;
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void contextLoads() {
        System.out.println(restHighLevelClient);
    }

    @Test
    public void testIndexDocument() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");

        User user = new User("张三", 29, "男");
        String jsonString = JSON.toJSONString(user);
        IndexRequest source = request.source(jsonString, XContentType.JSON);
        System.out.println(source);

        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse);

    }

    @Test
    public void testQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");

        // 构造检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        // 构造聚合条件
        searchSourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));
        searchSourceBuilder.aggregation(AggregationBuilders.avg("ageAvg").field("age"));
        searchSourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));

        // 执行检索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 获取复杂查询结果，封装为Bean
        SearchHits hits = searchResponse.getHits();
        SearchHit[] realHits = hits.getHits();
        for (SearchHit realHit : realHits) {
            String sourceAsString = realHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);
        }


        // 获取聚合信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        List<? extends Terms.Bucket> buckets = ageAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println("key: "+ bucket.getKey()+ "---value: " +bucket.getDocCount());
        }

        Avg ageAvg = aggregations.get("ageAvg");
        System.out.println("ageAvg: " + ageAvg.getValue());

        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("balanceAvg: " + balanceAvg.getValue());
        // 获取检索结果
        System.out.println(searchResponse);

    }


}

