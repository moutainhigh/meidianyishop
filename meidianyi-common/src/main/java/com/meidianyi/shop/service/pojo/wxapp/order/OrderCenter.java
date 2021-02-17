package com.meidianyi.shop.service.pojo.wxapp.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

import com.meidianyi.shop.common.foundation.util.PageResult;

/**
 * 微信订单中心
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class OrderCenter {
    private PageResult<OrderListMpVo> orders;
    private Map<Byte, Integer> orderStatuCount;
}
