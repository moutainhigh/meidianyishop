package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author changle
 * @date 2020/7/18 2:23 下午
 */
@Data
public class RebateRankingListVo {
    /**分销员头像*/
    private String userAvatar;
    /**分销员昵称*/
    private String username;
    /**分销员等级*/
    private String distributorLevel;
    /**当前分销员排名*/
    private Integer ranking;
    /**获取佣金总额*/
    private BigDecimal totalRebateMoney;

    private List<RebateList> rebateList;


    @Data
    static public class RebateList{
        private Integer userId;
        /**分销员昵称*/
        private String username;
        /**分销员头像*/
        private String userAvatar;
        /**分销员排名*/
        private Integer ranking;
        /**佣金金额*/
        private BigDecimal finalMoney;
    }
}
