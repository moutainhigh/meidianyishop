package com.meidianyi.shop.common.foundation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 孔德成
 * @date 2019/12/2 18:16
 */

@Constraint(validatedBy = ListValidValidator.class)
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface  ListValid {
    String message() default "{com.vpu.validation.constraints.ListMin.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * @return size the element must be higher or equal to
     */
    int min() default 0;

    /**
     * @return size the element must be lower or equal to
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 默认非空
     * @return
     */
    boolean notNull() default true;

    /**
     * Defines several {@link ListValid} annotations on the same element.
     *
     * @see ListValid
     */
    @Target({ FIELD })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ListValid[] value();
    }
}
