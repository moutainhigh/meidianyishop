package com.meidianyi.shop.service.pojo.shop.order;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import com.meidianyi.shop.common.foundation.util.PageResult;

/**
 * @author wangshuai
 */
@Getter
@Setter
@ToString
public class OrderQueryVo {
    private PageResult<? extends OrderListInfoVo> list;
    private Map<Byte, Integer> count;
}
