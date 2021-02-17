package com.meidianyi.shop.common.foundation.validator;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 字符校验,中文,英文,及个数限制
 *
 * @author 孔德成
 * @date 2019/12/3 9:19
 */
public class CharacterValidValidator implements ConstraintValidator<CharacterValid, String> {

    int min;
    int max;
    int chineseNum;
    boolean letterDigit;
    boolean chinese;
    private String message;


    @Override
    public void initialize(CharacterValid constraintAnnotation) {
        max = constraintAnnotation.max();
        min = constraintAnnotation.min();
        letterDigit = constraintAnnotation.letterDigit();
        chinese = constraintAnnotation.chinese();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (value == null) {
            if (min <= 0) {
                return true;
            }
            //最少输入
            message = "{com.vpu.validation.constraints.CharacterValid.min}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        int num = 0;
        char[] charArray = value.toCharArray();
        //中文和英文
        if (letterDigit && chinese) {
            return processChineseAndLetterDigit(context, num, charArray);
        }
        //英文
        if (letterDigit) {
            return processLetterDigit(context, num, charArray);
        }
        //中文
        if (chinese) {
            for (char c : charArray) {
                int cnorEn = isCnorEn(c);
                if (cnorEn == 0) {
                    //只能输入中文
                    message = "{com.vpu.validation.constraints.CharacterValid.Chinese}";
                    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                    return false;
                }
                num += cnorEn;
            }
            if (min >= 0 && num < min) {
                //至少输入几个字符
                chineseNum = min / 2;
                message = "{com.vpu.validation.constraints.CharacterValid.Chinese.min}";
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            if (max >= 0 && num > max) {
                //至多输入几个字符
                chineseNum = max / 2;
                message = "{com.vpu.validation.constraints.CharacterValid.Chinese.max}";
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
        }
        for (char c : charArray) {
            int cnorEn = isCnorEn(c);
            if (cnorEn == 0) {
                //只能输入中文
                message = "{com.vpu.validation.constraints.CharacterValid.Chinese}";
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            num += 1;
        }
        if (min >= 0 && num < min) {
            //至少输入几个字符
            chineseNum = min / 2;
            message = "{com.vpu.validation.constraints.CharacterValid.min}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (max >= 0 && num > max) {
            //至多输入几个字符
            chineseNum = max / 2;
            message = "{com.vpu.validation.constraints.CharacterValid.max}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean processLetterDigit(ConstraintValidatorContext context, int num, char[] charArray) {
        for (char c : charArray) {
            int cnorEn = isCnorEn(c);
            if (cnorEn == 0) {
                //只能输入英文
                message = "{com.vpu.validation.constraints.CharacterValid.English}";
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            num += cnorEn;
        }
        if (min >= 0 && num < min) {
            //至少输入几个字符
            chineseNum = min / 2;
            message = "{com.vpu.validation.constraints.CharacterValid.English.min}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (max >= 0 && num > max) {
            //至多输入几个字符
            chineseNum = max / 2;
            message = "{com.vpu.validation.constraints.CharacterValid.English.max}";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean processChineseAndLetterDigit(ConstraintValidatorContext context, int num, char[] charArray) {
        for (char c : charArray) {
            int cnorEn = isCnorEn(c);
            if (cnorEn == 0) {
                //只能输入中文英文
                message = "{com.vpu.validation.constraints.CharacterValid.CnAEn}";
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            num += cnorEn;
        }
        if (min >= 0 && num < min) {
            //至少输入几个字符
            chineseNum = min / 2;
            message = "{com.vpu.validation.constraints.CharacterValid.CnAEn.min}";
//                context.buildConstraintViolationWithTemplate(message).addPropertyNode("chineseNum").inContainer(Integer.class,chineseNum)
//                        .addConstraintViolation();
            context.unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("chineseNum", chineseNum)
                .buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
            return false;
        }
        if (max >= 0 && num > max) {
            //至多输入几个字符
            chineseNum = max / 2;
            message = "{com.vpu.validation.constraints.CharacterValid.CnAEn.max}";
            HibernateConstraintValidatorContext hcontext = context.unwrap(HibernateConstraintValidatorContext.class);
            hcontext.addMessageParameter("chineseNum", chineseNum)
                .buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
            return false;
        }
        return true;
    }


    /**
     * 判断是否是中文和英文
     *
     * @param c
     * @return
     */
    private static int isCnorEn(char c) {
        //中文字符
        if (c >= 0x0391 && c <= 0xFFE5) {
            return 2;
        }
        //英文字符
        if (c <= 0x00FF) {
            return 1;
        }
        return 0;
    }

    // GENERAL_PUNCTUATION 判断中文的"号
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号

    public static boolean isChinese(char c) {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
            return true;

        }
        return false;

    }

    @Override
    public String toString() {
        return "CharacterValidValidator{" +
            "min=" + min +
            ", max=" + max +
            ", chineseNum=" + chineseNum +
            ", letterDigit=" + letterDigit +
            ", chinese=" + chinese +
            ", message='" + message + '\'' +
            '}';
    }
}
