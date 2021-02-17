package com.meidianyi.shop.service.pojo.wxapp.distribution.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author panjing
 * @date 2020/7/2 15:43
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class WithdrawRecordParam {
    /**
     * 将要查询提现记录的用户Id
     */
    private Integer userId;
    /**
     * 当前页数
     */
    private Integer currentPage;
    /**
     * 每页展示条数
     */
    private Integer pageRows;
}
