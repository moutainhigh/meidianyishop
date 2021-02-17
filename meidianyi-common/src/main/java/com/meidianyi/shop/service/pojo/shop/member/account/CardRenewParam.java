package com.meidianyi.shop.service.pojo.shop.member.account;

import lombok.Data;

/**
 * 会员卡续费
 * @author liangchen
 * @date 2020.04.07
 */
@Data
public class CardRenewParam {
    /** 用户id */
    private Integer userId;
    /** 会员卡编号 */
    private String cardNo;
}
