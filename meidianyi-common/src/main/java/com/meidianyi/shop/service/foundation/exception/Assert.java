package com.meidianyi.shop.service.foundation.exception;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The type Assert.
 *
 * @author liufei
 * @date 11 /5/19
 */
public class Assert {
    /**
     * Is null.
     *
     * @param o    the o
     * @param code the code
     */
    public static void isNull(Object o, JsonResultCode code) {
        if (o == null) {
            throw new BusinessException(code);
        }
    }

    public static void isNull(Object o, JsonResultCode code, Object... args) {
        if (o == null) {
            commonFunc(code, args);
        }
    }

    /**
     * Not null.
     *
     * @param o    the o
     * @param code the code
     */
    public static void notNull(Object o, JsonResultCode code, Object... args) {
        if (Objects.isNull(o)) {
            commonFunc(code, args);
        }
    }

    public static void notNull(Object o, JsonResultCode code) {
        if (Objects.isNull(o)) {
            throw new BusinessException(code);
        }
    }

    /**
     * Is empty.
     *
     * @param <E>        the type parameter
     * @param collection the collection
     * @param code       the code
     */
    public static <E> void isEmpty(Collection<E> collection, JsonResultCode code) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(code);
        }
    }

    /**
     * Not empty.
     *
     * @param <E>        the type parameter
     * @param collection the collection
     * @param code       the code
     */
    public static <E> void notEmpty(Collection<E> collection, JsonResultCode code) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(code);
        }
    }

    /**
     * Data abnormal.数据库字段数据非法/异常
     *
     * @param <T>       the type parameter 校验对象数组
     * @param predicate the predicate 自定义校验规则, 返回true表示校验通过
     * @param code      the code 校验失败抛出错误
     * @param t         the t
     */
    public static <T> void dataAbnormal(Predicate<T> predicate, JsonResultCode code, T... t) {
        for (T e : t) {
            if (!predicate.test(e)) {
                throw new BusinessException(code, e);
            }
        }
    }

    /**
     * Is true.
     *
     * @param boo  the boo
     * @param code the code
     * @param args the args
     */
    public static void isTrue(boolean boo, JsonResultCode code, Object... args) {
        isFalse(!boo, code, args);
    }

    /**
     * Is false.
     *
     * @param boo  the boo
     * @param code the code
     * @param args the args
     */
    public static void isFalse(boolean boo, JsonResultCode code, Object... args) {
        if (boo) {
            if (args != null && args.length > 0) {
                throw new BusinessException(code, args);
            }
            throw new BusinessException(code);
        }
    }

    private static void commonFunc(JsonResultCode code, Object... args) {
        if (args != null && args.length > 0) {
            throw new BusinessException(code, args);
        }
        throw new BusinessException(code);
    }

    public static String join(Object... args) {
        return Arrays.stream(args).map(Object::toString).collect(Collectors.joining(StringUtils.SPACE));
    }
}
