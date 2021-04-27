spring boot 微服务 
---------------------------

版本2.4.3

Eureka服务治理基础架构三要素：
    1. 服务注册中心
    2. 服务提供者
    3. 服务消费者
        ribbon消费、feign消费



数字顺序为创建项目顺序

1.eureka-server
服务注册中心
职责：维护服务实例  不正常服务会被剔除

实现高可用：就是将自己作为服务向其他服务中心注册自己
    实现：再创建一个eureka-server1， 将两个的service-url.defaultZone 相互指向。
    
    eureka-server的application.properties:
    eureka.client.service-url.defaultZone=http://127.0.0.1:1111/eureka/
    
    eureka-server1的application.properties:
    eureka.client.service-url.defaultZone=http://127.0.0.1:2222/eureka/

    其他服务实例的application.properties:
    eureka.client.service-url.defaultZone=http://127.0.0.1:2222/eureka/,http://127.0.0.1:1111/eureka/


2.spring-boot-hello
测试注册
服务生产者






3.balancer
spring-cloud-starter-netflix-eureka-client 3.0.1的客户端均衡负载改用spring-cloud-starter-loadbalancer
均衡负载
服务消费者
LoadBalancer的策略：
            RoundRobinLoadBalancer 轮询选择server（默认）
            RandomLoadBalancer 随机选择一个server

robbin的策略：
            最低并发策略BestAvailableRule： 选择最小请求数
            可用过滤策略（AvailabilityFilteringRule）： 过滤掉连接失败的服务节点，并且过滤掉高并发的服务节点，然后从健康的服务节点中，使用轮询策略选出一个节点返回。
            随机策略 RandomRule ： 随机选择一个server
            轮询策略 RoundRobinRule： 轮询选择server（Ribbon默认策略）
            重试策略 RetryRule ： 根据轮询的方式重试
            权重策略WeightedResponseTimeRule (响应时间加权策略)： 根据响应时间去分配一个weight ，weight越低，被选择的可能性就越低
            区域权衡策略ZoneAvoidanceRule： 根据server的zone区域和可用性来轮询选择

权重：计算规则：weightSoFar+totalResponseTime-ResponseTimeAvg
实例A:[0,220]
实例B:(220,410]
实例C:(410,560]
实例D:(560,690)
[0,690)区间的随机数落在实例区间上，便取该实例。


hystrix在spring boot 2.4.0版本就停更进维,应该引入resilience4j作为熔断器.


    
4.spring-boot-hello1
与spring-boot-hello1一同测试负载均衡
服务生产者



