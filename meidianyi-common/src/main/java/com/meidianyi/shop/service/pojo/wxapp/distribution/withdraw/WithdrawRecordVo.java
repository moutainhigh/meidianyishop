package com.meidianyi.shop.service.pojo.wxapp.distribution.withdraw;

import com.meidianyi.shop.common.foundation.util.PageResult;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author panjing
 * @date 2020/7/2 15:12
 */
@Data
public class WithdrawRecordVo {
    /**
     * 已提现总额
     */
    private BigDecimal totalWithdraw;
    /**
     * 提现记录列表
     */
    private PageResult<SingleWithdrawRecordVo> withdrawRecords;

}