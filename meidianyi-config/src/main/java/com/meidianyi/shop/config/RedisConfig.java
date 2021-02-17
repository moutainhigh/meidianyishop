package com.meidianyi.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author lixinguo
 *
 */
@Configuration
public class RedisConfig {
	@Value("${spring.redis.host:127.0.0.1}")
    private String host;
 
    @Value("${spring.redis.port:6379}")
    private int port;
 
    @Value("${spring.redis.timeout:0}")
    private int timeout;
 
    @Value("${spring.redis.pool.max-idle:8}")
    private int maxIdle;
 
    @Value("${spring.redis.pool.max-wait:-1}")
    private long maxWaitMillis;
 
    @Value("${spring.redis.password:#{null}}")
    private String password;

 
    @Bean("jedisPool")
    public JedisPool getJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 是否启用pool的jmx管理功能, 默认true
        jedisPoolConfig.setJmxEnabled(true);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return jedisPool;
    }
}
