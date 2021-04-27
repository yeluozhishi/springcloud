package com.whk.balancer.service.HelloServiceImpl;

import com.whk.balancer.service.HelloService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Service
public class HelloServiceIpml implements HelloService {

    // 为断路器创建自定义的配置
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .permittedNumberOfCallsInHalfOpenState(2)
            .slidingWindowSize(2)
            .recordExceptions(IOException.class, TimeoutException.class)
//            .ignoreExceptions(BusinessException.class, OtherBusinessException.class)
            .build();

    // 使用自定义的全局配置创建CircuitBreakerRegistry
    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);



    @Autowired
    RestTemplate restTemplate;

    public String Hello(Long id){
        CircuitBreaker circuitBreaker = circuitBreakerRegistry
                .circuitBreaker("name");
        CheckedFunction0<Long> decoratedSupplier1 = CircuitBreaker
                .decorateCheckedSupplier(circuitBreaker, () -> {
                    if (id == 1) {
                        throw new RuntimeException("第一个熔断器 报错！");
                    }
                    return id;
                });
        return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();

    }







//    @CacheResult  //Hystrix缓存开启注释
//    @HystrixCommand(fallbackMethod = "HelloFallback") //熔断调用HelloFallback方法
//    public String Hello(Long id){
//        return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
//    }

    /**
     * 批量和并请求器
     * batchMethod指定合并请求方法
     * collapserProperties中设定合并请求属性，这里设置的是合并时间窗为100毫秒，更多设置在{@link HystrixPropertiesManager}里
     * @param id
     * @return
     */
//    @HystrixCollapser(batchMethod = "findAll",
//            collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds", value = "100")})
//    public User find(Long id){
//        return null;
//    }
//
//    @HystrixCommand
//    public List<User> findAll(){
//        return null;
//    }
//
//    @CacheRemove(commandKey = "getUserByKey")  //缓存清理
//    @HystrixCommand(fallbackMethod = "HelloFallback") //熔断调用HelloFallback方法
//    public String updataHello(@CacheKey("id") Long id){
//        return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
//    }

    public String HelloFallback(){
        return "error";
    }
}
