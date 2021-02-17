package com.meidianyi.shop.common.foundation.validator;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * 列表校验 最大个数最少个数,是否为空
 * @author 孔德成
 * @date 2019/12/2 18:18
 */
public class ListValidValidator implements ConstraintValidator<ListValid, Collection<?>> {

    private int min;
    private int max;
    private boolean notNull;
    private String message;

    @Override
    public void initialize(ListValid listNum) {
        min =listNum.min();
        max=listNum.max();
        notNull =listNum.notNull();
        message=listNum.message();
    }

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (value==null){
            if (!notNull){
                message="{com.vpu.validation.constraints.ListValid.notNull.message}";
            }
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return !notNull;
        }
        if (value.size()<min){
            message="{com.vpu.validation.constraints.ListValid.min.message}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (value.size()>max){
            message="{com.vpu.validation.constraints.ListValid.max.message}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
