package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Data;

/**
 * 全部优惠卷 vo
 *
 * @author 孔德成
 * @date 2019/10/21 11:45
 */
@Data
public class CouponAllVo {

    private Integer id;
    private String actName;
    private String actCode;
    private Integer surplus;
    private Byte type;
    private Byte limitSurplusFlag;

}
