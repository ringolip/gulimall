package com.shamo.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 配置Redission
 *
 * @author ringo
 * @version 1.0
 * @date 2021/8/17 17:35
 */
@Configuration
public class RedissonConfig {

    /**
     * 将RedissonClient加入容器
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.186.146:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
