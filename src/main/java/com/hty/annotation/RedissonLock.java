package com.hty.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 使用此注解会被RepeatLockAspect拦截，默认对当前方法加不等待永久持有的锁，直到此方法执行结束释放锁
 * 锁的范围为同一用户同一接口同一入参不能重复请求
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLock {

    /**
     * key的前缀，默认获取的是方法的全限定名(包名+方法名)
     */
    String prefixKey() default "";

    /**
     * 使用EL表达式来解析key
     * */
    String key();

    /**
     * 锁的等待时间，默认-1，不等待直接失败,redisson默认也是-1
     * */
    int waitTime() default -1;

    /**
     * 时间单位，默认毫秒
     * */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}