package com.meidianyi.shop.service.pojo.shop.maptemplate;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月18日
 */
@Data
@Builder
public class OrderRefundSuccessParam {
    public static final String REMIND = "您已退款成功";
    private String orderSn;
    private String payTime;
    private String refundMoney;
    @Builder.Default
    private String remind=REMIND;
    private List<Integer> userIds;
}
