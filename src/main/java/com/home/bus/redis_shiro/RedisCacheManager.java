package com.home.bus.redis_shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.LifecycleUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: xu.dm
 * @Date: 2018/11/4 20:58
 * @Description:
 */
@Component
public class RedisCacheManager implements CacheManager,Destroyable {

    @Resource(name = "redisTemplate2")
    private RedisTemplate<String, Object> redisTemplate;

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache cache = caches.get(name);
        if (cache == null) {
            cache = new ShiroCache<K, V>(name, redisTemplate);
            caches.put(name, cache);
        }
        return cache;
    }

    @Override
    public void destroy() throws Exception {
        while(!this.caches.isEmpty()) {
            Iterator var1 = this.caches.values().iterator();

            while(var1.hasNext()) {
                Cache cache = (Cache)var1.next();
                LifecycleUtils.destroy(cache);
            }
            this.caches.clear();
        }
    }
}