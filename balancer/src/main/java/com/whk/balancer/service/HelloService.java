package com.whk.balancer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "HelloFallback") //熔断调用HelloFallback方法
    public String Hello(){
        return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
    }

    public String HelloFallback(){
        return "error";
    }
}
