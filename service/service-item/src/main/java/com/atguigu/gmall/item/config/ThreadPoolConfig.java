package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        //核心线程数，最大线程数，线程存活时间，时间单位，阻塞队列，线程工厂，拒绝策略
        return new ThreadPoolExecutor(50,500,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));
    }
}
