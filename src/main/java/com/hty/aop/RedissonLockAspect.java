package com.hty.aop;

import com.hty.annotation.RedissonLock;
import com.hty.service.RedissonLockService;
import com.hty.util.SpElUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author hty
 * @date 2023-10-28 12:32
 * @email 1156388927@qq.com
 * @description
 */

@Slf4j
@Aspect
@Component
@Order(0)//确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {

    @Autowired
    private RedissonLockService redissonLockService;

    @Around("@annotation(com.hty.annotation.RedissonLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        String prefix = StringUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();//默认方法限定名+注解排名（可能多个）
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        return redissonLockService.executeWithLockThrows(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
