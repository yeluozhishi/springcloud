package com.whk.balancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@EnableHystrix//启用熔断机制
@EnableDiscoveryClient//向服务中心注册
//@LoadBalancerClients(defaultConfiguration = {LoadBalancerConfig.class}) //启用自己的负载配置
@SpringBootApplication
public class BalancerApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx=SpringApplication.run(BalancerApplication.class, args);

    }

}
