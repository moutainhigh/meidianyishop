package com.meidianyi.shop.service.pojo.shop.maptemplate;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年09月04日
 */
@Data
@Builder
public class OrderNewParam {
    public static final String REMARK = "请您及时处理。";
    private String orderSn;
    private String userName;
    private String mobile;
    private String deliverType;
    private String deliverTime;
    @Builder.Default
    private String remark=REMARK;
    private List<Integer> userIds;
}
