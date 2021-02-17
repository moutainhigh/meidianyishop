package com.meidianyi.shop.service.shop.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.shop.config.ShopCfgDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

/**
 * @author 王兵兵
 *
 *         2019年6月26日
 *
 */
@Service
public class BaseShopConfigService extends ShopBaseService {

	@Autowired
	ShopCfgDao shopCfgDao;

	/**
	 * 获取配置key对应value
     *
	 * @param  key
	 * @return
	 */
	protected String get(String key) {
		return shopCfgDao.get(key);
	}

	/**
     * 判断key是否存在
	 * true:存在
	 * false:不存在
	 * @param key
	 * @return
	 */
	protected Boolean isHaveKey(String key) {
		return shopCfgDao.exists(key);
	}


	/**
	 * 设置配置key对应value
     *
	 * @param  key
	 * @param  value
	 * @param  db
	 * @return
	 */
	protected int set(String key, String value) {
		if (!isHaveKey(key)) {
			return shopCfgDao.add(key, value);
		} else {
			return shopCfgDao.update(key, value);
		}
	}


	/**
	 * 设置其他类型数据配置
     *
	 * @param  db
	 * @param  <T>
	 * @param  key
	 * @param  value
	 * @param  toClass
	 * @return
	 */
	protected <T> int set(String key, T value, Class<? extends T> toClass) {
		return this.set(key, value.toString());
	}

	/**
	 * 设置json对象数据配置
     *
	 * @param  key
	 * @param  value
	 * @return
	 */
	protected int setJsonObject(String key, Object value) {
		return this.set(key, Util.toJson(value));
	}


	/**
	 * 获取配置key对应value,未取到时，则返回默认值
     *
	 * @param  key
	 * @param  defaultValue
	 * @return
	 */
	protected String get(String key, String defaultValue) {
		String val = get(key);
		return val == null ? defaultValue : val;
	}

	/**
	 * 按T类型取配置key对应value
     *
	 * @param  <T>
	 * @param  key
	 * @param  toClass
	 * @param  defaultValue
	 * @return
	 */
	protected <T> T get(String key, Class<? extends T> toClass, T defaultValue) {
		return Util.convert(get(key), toClass, defaultValue);
	}

    /**
     * Gets 2 object.按T类型取配置key对应value
     * 支持直接将json字符串转换为复杂对象
     *
     * @param <T>          the type parameter
     * @param key          the key
     * @param reference    the reference
     * @param defaultValue the default value
     * @return the 2 object
     */
    protected <T> T getJsonObject(String key, TypeReference<T> reference, T defaultValue) {
    	String s = this.get(key);
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        T t = Util.json2Object(s, reference, false);
        return t != null ? t : defaultValue;
    }

	/**
	 * 按T类型取配置key对应json对象的value
	 *
	 * @param  <T>
	 * @param  key
	 * @param  toClass
	 * @return
	 */
	protected <T> T getJsonObject(String key, Class<? extends T> toClass) {
		String value = get(key);
		if (null != value) {
			return Util.parseJson(value, toClass);
		} else {
			return null;
		}
	}

	/**
	 * 按T类型取配置key对应json对象的value
	 *
	 * @param  <T>
	 * @param  key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected <T> T getJsonObject(String key, TypeReference<T> valueTypeRef) {
		return Util.parseJson(get(key), valueTypeRef);
	}

	/**
	 * 按T类型取配置key对应json对象的value,如果未取到，则返回默认值
	 *
	 * @param  <T>
	 * @param  key
	 * @param  toClass
	 * @param  defaultValue
	 * @return
	 */
	protected <T> T getJsonObject(String key, Class<? extends T> toClass, T defaultValue) {
		T result = getJsonObject(key, toClass);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
}
