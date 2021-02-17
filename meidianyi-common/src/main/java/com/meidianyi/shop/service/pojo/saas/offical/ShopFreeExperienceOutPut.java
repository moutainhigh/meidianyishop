package com.meidianyi.shop.service.pojo.saas.offical;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 返回json的pojo
 * @author 黄壮壮
 * 2019-07-03 17:24
 */
@Data
@NoArgsConstructor
public class ShopFreeExperienceOutPut{

    private Integer   feId;
    private String    company;
    private String    contact;
    private String    mobile;
    private Integer   provinceId;
    private String    content;
    private Timestamp askTime;
    private Byte      isDeal;
    private Integer   shopId;
    private String    desc;
    private String    source;
    private Integer   userId;
}
