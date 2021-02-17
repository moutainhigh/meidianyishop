package com.meidianyi.shop.service.pojo.wxapp.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author 常乐
 * @Date 2020-05-20
 */
@Data
public class UserRebateVo {
    /**分销员ID*/
    private Integer userId;
    /**用户头像*/
    private String userAvatar;
    /**分销员昵称*/
    private String username;
    /**返利金额*/
    @JsonProperty("FinalMoney")
    private BigDecimal finalMoney;
    /**分销员等级*/
    private String levelName;
    /**等级id*/
    private Byte levelId;
}
