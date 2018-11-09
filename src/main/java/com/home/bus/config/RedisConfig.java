package com.home.bus.config;

import com.home.bus.redis_shiro.RedisObjectSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @Author: xu.dm
 * @Date: 2018/10/14 22:36
 * @Description:
 */
@Configuration
//@EnableCaching //开启springboot注解式缓存
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.cache.time-to-live}")
    private long timeToLive=3600;

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

//    private Duration timeToLive = Duration.ofSeconds(60*60);

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuffer sb = new StringBuffer();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    // 缓存管理器
    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(this.timeToLive));


        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(config)
                .transactionAware();
//        @SuppressWarnings("serial")
//        Set<String> cacheNames = new HashSet<String>() {
//            {
//                add("codeNameCache");
//            }
//        };
//        builder.initialCacheNames(cacheNames);

        return builder.build();
    }


    /**
     * RedisTemplate配置
     * GenericJackson2JsonRedisSerializer 不能反序列化没有无参数构造函数的类，不能反序列化 接口的动态代理类，原因相同
     * Jackson2JsonRedisSerializer 不能直接序列化Map和list（貌似）然后GenericJackson2JsonRedisSerializer不能的它也不能
     * fashjson（GenericFastJsonRedisSerializer和FastJson2JsonRedisSerializer）不能反序列化动态代理类 ，不能反序列化没有无参数构造函数的类
     * kyro-serializers 可以序列化无缺省构造函数的类，序列化后占用空间小，但是不能反序列化动态代理类
     * JdkSerializationRedisSerializer 不能（反）序列化动态代理类，序列化后可读性差，占用空间大，序列化的对象必须实现Serializable
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        // 设置jackson序列化1
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
//                Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);

        //设置fastjson序列化
//        FastJson2JsonRedisSerializer<Object> fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer<>(Object.class);
//        // 全局开启AutoType，不建议使用
////         ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//        // 建议使用这种方式，小范围指定白名单
//        ParserConfig.getGlobalInstance().addAccept("com.home.bus.");

//        设置kyro-serializers序列化
//        KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<>(Object.class);
//
        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);// key序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());// value序列化
        redisTemplate.setHashKeySerializer(stringSerializer);// Hash key序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());// Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "redisTemplate2")
    public RedisTemplate<String, Object> redisTemplate2(LettuceConnectionFactory lettuceConnectionFactory) {

        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());// key序列化
        redisTemplate.setValueSerializer(new RedisObjectSerializer());// value序列化
        return redisTemplate;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
//        // 设置序列化
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
//                Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        return jackson2JsonRedisSerializer;

        //两种区别，主要是序列化List
//        return new GenericJackson2JsonRedisSerializer();

        //第三种
        return new RedisObjectSerializer();
    }

}
