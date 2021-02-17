package com.meidianyi.shop.service.pojo.shop.distribution.withdraw;

import lombok.Data;

/**
 * 提现记录详情添加备注接口
 * @author panjing
 * @date 2020/7/6 10:09
 */
@Data
public class WithdrawRemarkParam {
    /**
     * 提现记录id
     */
    private Integer id;
    /**
     * 备注
     */
    private String desc;
}
