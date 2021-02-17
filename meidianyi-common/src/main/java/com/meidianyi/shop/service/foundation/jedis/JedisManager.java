package com.meidianyi.shop.service.foundation.jedis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;


/**
 *
 * JedisPool是线程安全的
 *
 * @author 新国
 *
		 */
@Service
public class JedisManager {

	private static final Long RELEASE_SUCCESS = 1L;

	public static final String LOCK_SUCCESS = "OK";
	public static final String SET_IF_NOT_EXIST = "NX";
	public static final String SET_WITH_EXPIRE_TIME = "PX";

	private static final Integer INCR_SEQUENCE_MAX =9999;
	private static final Integer INCR_SEQUENCE_MIN =1;
	final String STRING_ZERO = "0";

	@Autowired
	private JedisPool pool;


	/**
	 * 获取缓存连接池
	 *
	 * @return
	 */
	public JedisPool getJedisPool() {
		return pool;
	}

	/**
	 * 按时间设置缓存
	 *
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void set(String key, String value, int seconds) {
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.set(key, value);
			jedis.expire(key, seconds);
		}
	}
	/**
	 *
	 * @param key
	 * @param values
	 */
	public void lpush(String key, String[] values) {
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.lpush(key, values);
		}
	}

    public void lpush(String key, List<String> values) {
        String[] strings = values.toArray(new String[values.size()]);
        lpush(key,strings);
    }

    public void lpush(String key, String value) {
        try (Jedis jedis = getJedisPool().getResource()){
            jedis.lpush(key, value);
        }
    }

    public void rpush(String key, List<String> values) {
        try (Jedis jedis = getJedisPool().getResource()){
            jedis.rpush(key, values.toArray(new String[values.size()]));
        }
    }

    public void rpush(String key, String value) {
        try (Jedis jedis = getJedisPool().getResource()){
            jedis.rpush(key, value);
        }
    }

    public void cleanList(String key){
        try (Jedis jedis = getJedisPool().getResource()){
            jedis.ltrim(key,1,0);
        }
    }

    /**
     * 根据范围获取list内容
     * @param key
     * @param startIndex
     * @param endIndex
     * @return
     */
    public List<String> lrange(String key, long startIndex, long endIndex) {
        try (Jedis jedis = getJedisPool().getResource()){
            return jedis.lrange(key,startIndex,endIndex);
        }
    }

	/**
	 *
	 * @param key
	 */
	public Long getListSize(String key) {
		try (Jedis jedis = getJedisPool().getResource()){
			return jedis.llen(key);
		}
	}
	/**
	 * 获取当前key下的所有list
	 * @param key
	 */
	public List<String> getList(String key) {
		try (Jedis jedis = getJedisPool().getResource()){
			return jedis.lrange(key,0,-1);
		}
	}

	/**
	 * 永久设置缓存
	 *
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.set(key, value);
		}
	}

	/**
	 * 删除key的缓存
	 *
	 * @param key
	 */
	public void delete(String key) {
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.del(key);
		}
	}

    /**
     * 删除key集合
     * @param keys
     */
	public void delete(String[] keys){
        try (Jedis jedis = getJedisPool().getResource()){
            jedis.del(keys);
        }
    }

	/**
	 * 删除key的缓存
	 *
	 * @param keys
	 */
	public void delete(Set<String> keys) {
		try (Jedis jedis = getJedisPool().getResource()){
			for (String key:keys){
				if (jedis.exists(key)){
					jedis.del(key);
				}
			}
		}
	}

	/**
	 * 得到key的缓存
	 *
	 * @param  key
	 * @return
	 */
	public String get(String key) {
		try (Jedis jedis = getJedisPool().getResource()){
			return jedis.get(key);
		}
	}

	/**
	 * 命令用于检查给定 key 是否存在。
	 * @param key key
	 * @return true or false
	 */
	public Boolean exists(String key){
		try (Jedis jedis = getJedisPool().getResource()){
			return jedis.exists(key);
		}
	}

	/**
	 * 得到keys
	 *
	 * @param  key
	 * @return
	 */
	public Set<String> keys(String key) {
		try (Jedis jedis = getJedisPool().getResource()){
			return jedis.keys(key);
		}
	}

	/**
	 * 重设key缓存时间
	 *
	 * @param key
	 * @param seconds
	 */
	public void expire(String key, int seconds) {
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.expire(key, seconds);
		}
	}
	/**
     * 从redis读取数据;取不到从自定义的查询里取，并存到redis里;数据库查询不到，返回null
     *
     * @author: 卢光耀
     * @date: 2019-07-17 14:13
     */
	public String getValueAndSave(String key, Integer timeOut, JedisGetProcess function){
		String value;
		try (Jedis jedis = getJedisPool().getResource()){
			value = jedis.get(key);
			if( value != null ){
				return value;
            } else {
                value = function.getByDb();
                if (value == null) {
                    return null;
                }
                jedis.set(key, value);
                jedis.expire(key, timeOut);
                return value;
			}
		}
	}
	/**
	 *  从redis获取数据(单个)
	 * @param key 键值
	 * @param field Hash->K
	 * @param timeOut Hash->V
	 * @param function get data by db
	 * @return data
	 */
	public String getValueAndSaveForHash(String key,String field,Integer timeOut, JedisGetProcess function){
		try(Jedis jedis = getJedisPool().getResource()){
			String result = jedis.hget(key,field);
			if( result == null ){
				result = function.getByDb();
			}else{
				return result;
			}
			jedis.hset(key,field,result);
			jedis.expire(key,timeOut);
			return result;
		}
	}
    public List<String> batchGetHash(String key,String[] field){
        try(Jedis jedis = getJedisPool().getResource()){
            return jedis.hmget(key,field);
        }
    }
	/**
	 *  从redis获取数据(批量)
	 * @param key 键值
	 * @param field Hash->K
	 * @param timeOut Hash->V
	 * @param function get data by db
	 * @return data
	 */
	public List<String> getValueAndSaveForHash(String key,String[] field,Integer timeOut, JedisMgetProcess function){
		try(Jedis jedis = getJedisPool().getResource()){
			List<String> result = jedis.hmget(key,field);

			if( result == null ){
				result = function.getByDb();
			}else{
				return result;
			}
			Map<String,String> data = new HashMap<>(field.length);
			for (int i = 0; i < field.length; i++) {
				data.put(field[i],result.get(i));
			}
			jedis.hmset(key,data);
			jedis.expire(key,timeOut);
			return result;
		}
	}

    /**
     * 根据key获取key下的所有hash数据
     * @param key key
     * @return {Map<String,String>}
     */
    public Map<String,String> getAllHash(String key){
        try (Jedis jedis = getJedisPool().getResource()){
            return jedis.hgetAll(key);
        }
    }
	public void addToHash(String key, Map<String,String> data,Integer timeOut){
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.hmset(key,data);
			jedis.expire(key,timeOut);
		}
	}
    public void addToHash(String key,String fieldKey,String filed,Integer timeOut){
        try (Jedis jedis = getJedisPool().getResource()){
            jedis.hset(key,fieldKey,filed);
            jedis.expire(key,timeOut);
        }
    }
	public void delHash(String key, String... field){
		try (Jedis jedis = getJedisPool().getResource()){
			jedis.hdel(key,field);
		}
	}

	/**
	 * redis加锁
	 * @param lockKey
	 * @param requestId
	 * @param timeOut
	 * @return
	 */
	public boolean  addLock(String lockKey,String requestId,Integer timeOut){
		try( Jedis jedis = getJedisPool().getResource() ){
			String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeOut);
			if (LOCK_SUCCESS.equals(result)) {
				return true;
			}
			return false;
		}
	}


	/**
	 * redis释放锁
	 * @param lockKey 锁
	 * @param requestId 请求标识
	 * @return 是否释放成功
	 */
	public  boolean releaseLock( String lockKey, String requestId) {
		try( Jedis jedis = getJedisPool().getResource() ){
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
			if (RELEASE_SUCCESS.equals(result)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 获取自增序列
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public Long getIncrSequence(String key,Integer max,Integer min){
		try (Jedis jedis = getJedisPool().getResource()){
			return  jedis.incr(key)%(max-min)+min;
		}
	}

	/**
	 * 自增序列 默认9999-1
	 * @param key
	 * @return
	 */
	public String getIncrSequence(String key){
		Long sequence = getIncrSequence(key, INCR_SEQUENCE_MAX, INCR_SEQUENCE_MIN);
		return StringUtils.leftPad(sequence.toString(), INCR_SEQUENCE_MAX.toString().length(), STRING_ZERO);
	}

	/**
	 * 获取自增字段
	 * @param key key
	 * @param timeOut 超时时间
	 * @param function redis没有时去方法中的值
	 * @return incr
	 */
	public Long getIncrValueAndSave(String key,Integer timeOut, JedisGetProcess function){
		Long value =null;
		try (Jedis jedis = getJedisPool().getResource()){
			Boolean exists = jedis.exists(key);
			if (exists){
				value = jedis.incr(key);
			}
			if( value != null ){
				return value;
			}else {
				String byDb = function.getByDb();
				jedis.set(key, byDb);
				jedis.expire(key,timeOut);
				return Long.valueOf(byDb);
			}
		}
	}
	/**
	 * 递减
	 * @param key key
	 * @return incr
	 */
	public Long decr(String key){
		Long value =null;
		try (Jedis jedis = getJedisPool().getResource()){
			value=jedis.decr(key);
		}
		return value;
	}

}
