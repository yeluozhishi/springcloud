package com.whk.balancer.service.HystrixCache;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.whk.balancer.entity.User;
import org.springframework.web.client.RestTemplate;

/**
 * 传统方法开启HystrixCache
 * 重写getCacheKey方法，不返回null就开启了
 * 但是如何用？？？？？
 */
public class UserGetCommand extends HystrixCommand<User> {

    private static final HystrixCommandKey GETTER_KEY =  HystrixCommandKey.Factory.asKey("CommandKey");
    private RestTemplate restTemplate;
    private Long id;

    protected UserGetCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected UserGetCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
        super(group, threadPool);
    }

    protected UserGetCommand(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, executionIsolationThreadTimeoutInMilliseconds);
    }

    protected UserGetCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
    }

    protected UserGetCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected User run() {
        User r = restTemplate.getForObject("",User.class, id);
        return r;
    }

    @Override
    protected String getCacheKey(){
        //根据id置入缓存
        return String.valueOf(id);
    }

    public static void flushCache(Long id){
        //刷新缓存，根据id进行清理
        HystrixRequestCache.getInstance(GETTER_KEY,
                HystrixConcurrencyStrategyDefault.getInstance()).clear(String.valueOf(id));
    }
}

class UserPostCommand extends HystrixCommand<User>{

    private RestTemplate restTemplate;
    private User user;

    protected UserPostCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected UserPostCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
        super(group, threadPool);
    }

    protected UserPostCommand(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, executionIsolationThreadTimeoutInMilliseconds);
    }

    protected UserPostCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
    }

    protected UserPostCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected User run() throws Exception {
        User r = restTemplate.postForObject("",user,User.class);
        UserGetCommand.flushCache(user.getId());
        return r;
    }
}
