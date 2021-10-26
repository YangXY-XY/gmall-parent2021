package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void testLock() {
        String uuid= UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,3, TimeUnit.SECONDS);
        if(lock){
            Integer num = (Integer) redisTemplate.opsForValue().get("num");
            int n = num + 1;
            redisTemplate.opsForValue().set("num", n);
            String script="if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
        }else{
            try {
                Thread.sleep(100);
                testLock();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
