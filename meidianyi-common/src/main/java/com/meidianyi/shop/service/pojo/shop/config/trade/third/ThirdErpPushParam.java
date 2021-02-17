package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 第三方rep推送配置
 * @author 孔德成
 * @date 2020/5/19
 */
@Setter
@Getter
@ToString
public class ThirdErpPushParam {
    public final static byte REP_ACTION=1;
    public final static byte POS_ACTION=2;
    public final static byte CRM_ACTION=3;

    @NotNull
    @Min(0)
    @Max(1)
    private Byte status;
    @NotNull
    private String type;
}
