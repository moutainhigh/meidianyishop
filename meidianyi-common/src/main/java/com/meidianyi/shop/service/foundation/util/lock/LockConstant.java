package com.meidianyi.shop.service.foundation.util.lock;

/**
 * redis锁参数
 * @author 王帅
 */
public class LockConstant {
    /**
     * 等待时间（毫秒）
     */
    public static int DEFAULT_MAXWAIT = 2 * 60 * 1000;
    /**
     * 过期时间（毫秒）
     */
    public static int DEFAULT_expired = 2 * 60 * 1000;
}
