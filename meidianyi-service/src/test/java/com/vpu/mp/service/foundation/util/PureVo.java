package com.meidianyi.shop.service.foundation.util;

import lombok.Data;

/**
 * @author 郑保乐
 */
@Data
class PureVo {

    @I18N(propertiesFileName = "category")
    private String name;

    /** 以下为测试递归用到的字段 **/
    private Integer integerValue = 100;
    private int intValue = 100;
    private Double doubleWrapperValue = 100.0;
    private double doubleValue = 100.0;
    private Float floatWrapperValue = 100f;
    private float floatValue = 100f;
    private Object objectValue = new Object();
    private String stringValue;

    PureVo(String name) {
        this.name = name;
    }
}
