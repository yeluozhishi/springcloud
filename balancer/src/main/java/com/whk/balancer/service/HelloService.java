package com.whk.balancer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

public interface HelloService {

    public String Hello(Long id);
}
