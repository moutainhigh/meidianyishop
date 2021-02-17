package com.meidianyi.shop.service.pojo.shop.maptemplate;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/10/10
 **/
@Data
@Builder
public class OrderSaleAfterParam {
    public static final String REMARK = "请您及时处理！";
    private String orderSn;
    private String createTime;
    private String orderSource;
    private String refundMoney;
    private String refundReason;
    @Builder.Default
    private String remark=REMARK;
    private List<Integer> userIds;

}
