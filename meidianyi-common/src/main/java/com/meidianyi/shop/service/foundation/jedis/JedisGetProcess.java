package com.meidianyi.shop.service.foundation.jedis;

/**
 *
 * @author: 卢光耀
 * @date: 2019-07-15 13:49
 *
*/
@FunctionalInterface
public interface JedisGetProcess {
    /**
     * 从数据库去数据同时更新到缓存
     * @return
     */
    String getByDb();
}
