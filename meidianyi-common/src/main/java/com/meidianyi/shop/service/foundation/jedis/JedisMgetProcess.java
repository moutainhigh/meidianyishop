package com.meidianyi.shop.service.foundation.jedis;

import java.util.List;

/**
 * 支持redis mset
 * @author 卢光耀
 * @date 2019/11/20 2:54 下午
 *
*/
@FunctionalInterface
public interface JedisMgetProcess {
    /**
    * 从数据库去数据同时更新到缓存
    * @return 数据库取出并序列化后的数据
    */
    List<String> getByDb();
}
