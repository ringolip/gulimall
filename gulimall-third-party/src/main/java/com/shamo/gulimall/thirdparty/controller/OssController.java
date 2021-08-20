package com.shamo.gulimall.thirdparty.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.google.gson.internal.$Gson$Preconditions;
import com.shamo.common.utils.R;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/5 11:51
 */
@RestController // 前后端分离，返回JSON数据
public class OssController {

    // OSSAccessKey，Endpoint在配置中心配置过，直接注入OSS即可使用
    @Autowired
    private OSS ossClient;

    // 获取配置中心的属性配置
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint; // 上传文件的基础访问路径

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket; // 上传的项目存储空间

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKey;

    /**
     * 返回允许上传的policy和签名
     *
     * @return
     */
    @RequestMapping("/oss/policy")
    public R policy() {

        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // 以文件的上传的上传日期为前缀
        String dir = format + "/"; // 用户上传文件时指定的前缀

        Map<String, String> respMap = null; // 响应

        try {
            // 签名的有效时间
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            // 生成上传policy
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            // 生成签名
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            // 返回响应，包含生成的上传policy和签名
            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return R.ok().put("data", respMap);
    }

}
