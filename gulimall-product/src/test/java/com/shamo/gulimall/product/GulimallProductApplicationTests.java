package com.shamo.gulimall.product;

import com.shamo.gulimall.product.entity.BrandEntity;
import com.shamo.gulimall.product.service.BrandService;
import com.shamo.gulimall.product.thread.bean.ReentrantTicket;
import com.shamo.gulimall.product.thread.bean.Ticket;
import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
public class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void contextLoads() {

        BrandEntity brandEntity = brandService.getById(2);
        System.out.println(brandEntity);
    }

    @Test
    public void testHttpClient() throws IOException {
        URL url = new URL("http://www.baidu.com");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        RestTemplate restTemplate = new RestTemplate();
    }

    @Test
    public void testRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world" + UUID.randomUUID().toString());
        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    public void testSaleTicket(){
        ReentrantTicket ticket = new ReentrantTicket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    ticket.saleticket();
                }

            }
        }, "线程A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    ticket.saleticket();
                }

            }
        }, "线程B").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    ticket.saleticket();
                }

            }
        }, "线程C").start();


    }

}
