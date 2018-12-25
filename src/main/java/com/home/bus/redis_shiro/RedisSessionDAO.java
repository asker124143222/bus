package com.home.bus.redis_shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Author: xu.dm
 * @Date: 2018/11/4 20:42
 * @Description:redis实现共享session
 */
public class RedisSessionDAO extends CachingSessionDAO {
    private Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
//    private String prefix = getActiveSessionsCacheName();
    // session 在redis过期时间是30分钟30*60
    @Value("${spring.cache.time-to-live}")
    private int expireTime = 1800;

//    @Resource(name = "redisTemplate2")
//    private RedisTemplate<String, Object> redisTemplate;
    private Cache cache;

    private CacheManager redisCacheManager;

//    private String getKey(String key) {
//        return this.prefix + key.toString();
//    }

    /**
     * Default no-arg constructor.
     */
    public RedisSessionDAO(CacheManager redisCacheManager) {
        String cacheName = getActiveSessionsCacheName();
        this.redisCacheManager = redisCacheManager;
        this.cache = this.redisCacheManager.getCache(cacheName);
    }

    /**
     * Subclass implementation hook to actually persist the {@code Session}'s state to the underlying EIS.
     *
     * @param session the session object whose state will be propagated to the EIS.
     */
    @Override
    protected void doUpdate(Session session) {
        if(session==null)
            return;

        logger.info("-->update session [{}]", session.getId());
        cache.put(session.getId().toString(),session);

    }

    /**
     * Subclass implementation hook to permanently delete the given Session from the underlying EIS.
     *
     * @param session the session instance to permanently delete from the EIS.
     */
    @Override
    protected void doDelete(Session session) {
        if(session==null)
            return;
        logger.info("-->delete session [{}]", session.getId());
        cache.remove(session.getId().toString());
    }

    /**
     * Subclass hook to actually persist the given <tt>Session</tt> instance to the underlying EIS.
     *
     * @param session the Session instance to persist to the EIS.
     * @return the id of the session created in the EIS (i.e. this is almost always a primary key and should be the
     * value returned from {@link Session#getId() Session.getId()}.
     */
    @Override
    protected Serializable doCreate(Session session) {
        if(session==null)
            return null;
        // 创建一个Id并设置给Session
        Serializable sessionId = this.generateSessionId(session);
        assignSessionId(session, sessionId);
        logger.info("-->create session [{}]", sessionId);
        cache.put(sessionId.toString(),session);
//        redisTemplate.opsForValue().set(getKey(sessionId.toString()),session,expireTime,TimeUnit.SECONDS);
        return sessionId;
    }

    /**
     * Subclass implementation hook that retrieves the Session object from the underlying EIS or {@code null} if a
     * session with that ID could not be found.
     *
     * @param sessionId the id of the <tt>Session</tt> to retrieve.
     * @return the Session in the EIS identified by <tt>sessionId</tt> or {@code null} if a
     * session with that ID could not be found.
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if(sessionId==null)
            return null;

        logger.info("-->read session [{}]", sessionId);

        Session  session=(Session) cache.get(sessionId.toString());

        return session;
    }
}