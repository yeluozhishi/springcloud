package com.whk.balancer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.whk.balancer.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    @CacheResult  //Hystrix缓存开启注释
    @HystrixCommand(fallbackMethod = "HelloFallback") //熔断调用HelloFallback方法
    public String Hello(@CacheKey("id") Long id){
        return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
    }

    /**
     * 批量和并请求器
     * batchMethod指定合并请求方法
     * collapserProperties中设定合并请求属性，这里设置的是合并时间窗为100毫秒，更多设置在{@link HystrixPropertiesManager}里
     * @param id
     * @return
     */
    @HystrixCollapser(batchMethod = "findAll",
            collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds", value = "100")})
    public User find(Long id){
        return null;
    }

    @HystrixCommand
    public List<User> findAll(){
        return null;
    }

    @CacheRemove(commandKey = "getUserByKey")  //缓存清理
    @HystrixCommand(fallbackMethod = "HelloFallback") //熔断调用HelloFallback方法
    public String updataHello(@CacheKey("id") Long id){
        return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
    }

    public String HelloFallback(){
        return "error";
    }
}
