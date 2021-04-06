package com.whk.balancer.controller;

import com.whk.balancer.entity.User;
import com.whk.balancer.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    HelloService helloService;

    @RequestMapping(value = "/helloC",method = RequestMethod.GET)
    public String helloC(Long id){
        return helloService.Hello(id);
    }

//    public String helloC(@RequestBody User user){
//        System.out.println("load："+user);
//        return helloService.Hello();
//    }
}
