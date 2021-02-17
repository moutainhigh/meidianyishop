package com.meidianyi.shop.dao.foundation.transactional;

import java.lang.annotation.*;

/**
 * @author lixinguo
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DbTransactional {
    /**
     *
     * @return DbType
     */
    DbType type();
}
