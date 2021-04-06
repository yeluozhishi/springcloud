package com.whk.balancer.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 伪权重负载策略
 * 修改{@link RandomLoadBalancer}而来
 * whk
 */
public class LoadBalancerRule implements ReactorServiceInstanceLoadBalancer{
    private static final Log log = LogFactory.getLog(RandomLoadBalancer.class);

    private final String serviceId;

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private Map<String,ServiceInstance> serviceInstanceMap;

    private Map<String, Integer> instanceNum;


    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     */
    public LoadBalancerRule(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                              String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        serviceInstanceMap=new HashMap<>();
        instanceNum = new HashMap<>();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);

        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        /**
         *
         * 用请求次数作为权重 次数越多权重越小
         */
        float weightSoFar = 0f;
        // 是否到达上限
        boolean isMax = false;
        List<Float> value = new ArrayList<>();
        for (int i=0; i<instances.size(); i++){
            if(!instanceNum.containsKey(instances.get(i).getInstanceId())){
                instanceNum.put(instances.get(i).getInstanceId(),1);
            }
            weightSoFar += 1f/instanceNum.get(instances.get(i).getInstanceId());
            value.add(weightSoFar);

            if (instanceNum.get(instances.get(i).getInstanceId()) > 1<<30){
                isMax = true;
            }
        }

        int index = 0;
        float nextFloat = ThreadLocalRandom.current().nextFloat()*weightSoFar;
        if (value.get(0) < nextFloat){
            for (int j = 1; j<=value.size();j++){
                if (value.get(j-1)<weightSoFar && value.get(j)>=weightSoFar){
                    index = j;
                }
            }
        }

        ServiceInstance instance = instances.get(index);

        //更新serviceid的使用次数
        instanceNum.put(instance.getInstanceId(),instanceNum.get(instance.getInstanceId())+1);

        if (isMax){
            instanceNum.clear();
        }

        return new DefaultResponse(instance);
    }
}
