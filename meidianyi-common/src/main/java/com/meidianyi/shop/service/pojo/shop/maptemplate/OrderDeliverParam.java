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
public class OrderDeliverParam {
    private String orderSn;
    private String consignee;
    private String mobile;
    private String address;
    private List<Integer> userIds;
}
