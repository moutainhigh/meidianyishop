package com.meidianyi.shop.service.pojo.wxapp.distribution.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 提现状态枚举
 * @author panjing
 * @date 2020/7/7 9:52
 */
@Getter
@NoArgsConstructor
public enum WithdrawStatusEnum {
    /**
     * 待审核
     */
    PENDING(1),
    /**
     * 拒绝
     */
    REFUSE(2),
    /**
     * 已审核待出账
     */
    CHECKED(3),
    /**
     * 出账成功
     */
    SUCCESS(4),
    /**
     * 失败
     */
    FAILURE(5);

    private Byte status;

    WithdrawStatusEnum(int status) {
        this.status = (byte) status;
    }
}
