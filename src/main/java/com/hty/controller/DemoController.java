package com.hty.controller;

import com.hty.annotation.RedissonLock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author hty
 * @date 2023-10-28 12:16
 * @email 1156388927@qq.com
 * @description
 */

@RestController
@Slf4j
public class DemoController {

    @Resource
    private RedissonClient redissonClient;

    //代码实现分布式锁
    @GetMapping("/test1")
    public void test1() throws Exception {
        // 这里获取锁的key，指定锁的范围
        RLock rLock = redissonClient.getLock("redisson-lock");
        // 第一个时间为未获取到锁的等待时间，第二个时间为锁占有时间
        boolean tryLock = rLock.tryLock(0L, 30L, TimeUnit.SECONDS);
        if (!tryLock) {
            log.info("获取锁失败");
            throw new Exception();
        }
        try {
            log.info("获取锁成功");
            System.out.println("开始执行业务");
            Thread.sleep(10000);
            System.out.println("结束执行业务");
        } finally {
            // 保证锁的释放
            rLock.unlock();
        }
    }

    @RedissonLock(key = "#val")
    @GetMapping("/test2")
    public void test2(Integer val){
        System.out.println("hello world" + val);
    }

}
