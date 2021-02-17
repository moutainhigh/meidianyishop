package com.meidianyi.shop.service.foundation.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.meidianyi.shop.common.foundation.util.Util;

/**
 * @author 郑保乐
 */
public class VoTranslator {
	final static String STRING_TYPE = "String";

    /**
     * 递归查找对象中需要翻译的字段并进行翻译
     *
     * @param object 出参对象
     */
    public static  void translateFields(Object object,String language) {
        if (null == object) {
            return;
        }
        if (isRawType(object)) {
            return;
        }
        Class<?> clz = object.getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                translateStringValue(field, object,language);
            	translateListValue(field, object,language);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换语言
     */
    public static String translate(String prefix, String message, String defaultMessage,String language) {
        return Util.translateMessage(language, message, defaultMessage, prefix);
    }

    /**
     * 翻译 List 类型
     */
    @SuppressWarnings("unchecked")
    private static void translateListValue(Field field, Object object,String language) throws IllegalAccessException {
        I18N annotation = getI18nAnnotation(field);
        if (List.class.isAssignableFrom(field.getType())) {
        	
      //     ParameterizedType type = (ParameterizedType) field.getGenericType();            
     //      Class<?> realType = (Class<?>) type.getActualTypeArguments()[0];
            List<?> listObj = (List<?>)field.get(object);
            String realType = null;
            if(listObj != null && listObj.size()>0) {
            	realType = listObj.get(0).getClass().getSimpleName();
            } 
            
            if (null != annotation && STRING_TYPE.equals(realType)) {
                String fileName = annotation.propertiesFileName();
                List<String> list = (List<String>) field.get(object);
                if (null != list) {
                    List<String> translated = new LinkedList<>();
                    for (String s : list) {
                        String translate = translate(fileName, s, s,language);
                        translated.add(translate);
                    }
                    field.set(object, translated);
                }
            } else {
                List<?> o = (List<?>) field.get(object);
                if (null != o) {
                    o.forEach((obj)->{
                    	translateFields(obj,language);
                    });
                }
            }
        } else {
            Object o = field.get(object);
            translateFields(o,language);
        }
    }

    /**
     * 翻译 String 类型
     */
    private static void translateStringValue(Field field, Object object,String language) throws IllegalAccessException {
        I18N annotation = getI18nAnnotation(field);
        if (field.getType().equals(String.class) && null != annotation) {
            String value = (String) field.get(object);
            String fileName = annotation.propertiesFileName();
            String realValue = translate(fileName, value, value,language);
            field.set(object, realValue);
        }
    }


    /**
     * 判断对象是否原生类型（不可再递归翻译）
     */
    private static boolean isRawType(Object object) {
        return object instanceof String || object instanceof Number;
    }

    /**
     * 获取国际化注解
     */
    private static I18N getI18nAnnotation(Field field) {
        return field.getDeclaredAnnotation(I18N.class);
    }
}
