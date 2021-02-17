package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 常乐
 * @Date 2020-04-06
 * 分销改价保存接口入参
 */
@Data
public class RebateChangeListParam {
    private List<RebateChangeParam> rebateChangeInfo;

}
