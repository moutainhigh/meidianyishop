package com.meidianyi.shop.service.pojo.wxapp.market.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.List;

import com.meidianyi.shop.common.foundation.validator.ListValid;

/**
 * @author 孔德成
 * @date 2020/3/16
 */
@Data
@NoArgsConstructor
public class FormSubmitListVo {


    private Integer submitId;
    private Integer pageId;
    private Integer userId;
    private String openId;
    private  String nickName;
    private Integer sendScore;
    private String sendCoupons;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<SendCoupon> sendCouponList;
    public class SendCoupon{

    }
}



