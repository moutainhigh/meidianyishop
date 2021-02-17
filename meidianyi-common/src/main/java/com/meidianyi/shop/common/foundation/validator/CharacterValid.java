package com.meidianyi.shop.common.foundation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 字符校验
 * @author 孔德成
 * @date 2019/12/3 9:18
 */
@Constraint(validatedBy = {CharacterValidValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
public @interface CharacterValid {
    /**
     * 默认必须输入中英文字符
     * @return
     */
    String message() default "{com.vpu.validation.constraints.CharacterValid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * 最少输入字符数量 一个中文两个字符  -1不校验
     * @return
     */
    int min() default -1;

    /**
     * 最大字符数量默认 -1不校验
     * @return
     */
    int max() default -1;

    /**
     * 英文字符
     * @return
     */
    boolean letterDigit() default true;

    /**
     * 中文字符
     * @return
     */
    boolean chinese() default true;



    /**
     * 复注解,在同一个元素上定义几个
     * @see CharacterValid
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        CharacterValid[] value();
    }

}
