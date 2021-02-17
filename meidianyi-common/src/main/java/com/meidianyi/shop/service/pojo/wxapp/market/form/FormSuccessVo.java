package com.meidianyi.shop.service.pojo.wxapp.market.form;

import com.meidianyi.shop.db.shop.tables.FormSubmitList;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponAndVoucherDetailVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * 表单统计
 * @author 孔德成
 * @date 2020/3/16
 */
@Data
@NoArgsConstructor
public class FormSuccessVo {

    private Integer submitId;
    private Integer pageId;
    private Integer userId;
    private String openId;
    private String nickName;
    private String sendScore;
    private String sendCoupons;
    private String formCfg;
    private Timestamp createTime;
    private Timestamp updateTime;

    private List<CouponAndVoucherDetailVo> couponList;

}
