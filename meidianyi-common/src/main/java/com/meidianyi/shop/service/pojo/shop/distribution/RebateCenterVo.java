package com.meidianyi.shop.service.pojo.shop.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.wxapp.distribution.RebateOrderListVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.RebateRankingTopVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.UserRebateVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 常乐
 * @Date 2020-03-13
 */
@Data
public class RebateCenterVo {
    /**
     * 可提现佣金
     */
    private BigDecimal canWithdraw;
    /**
     * 累积获得佣金
     */
    private BigDecimal totalWithdraw;
    /**
     * 待返利佣金
     */
    private BigDecimal waitWithdraw;
    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     *佣金排名开关
     */
    private Byte rankStatus;
    /**
     * 邀请用户数
     */
    private Integer inviteUserNum;
    /**
     * 返利订单数
     */
    private Integer rebateOrderNum;
    /**
     * 累积返利商品总额
     */
    private BigDecimal totalCanFanliMoney;
    /**
     * 返利佣金排名
     */
    private List<DistributorRankingVo> distributorRanking;
    /**
     * 我的等级
     */
    private String distributorLevel;
    /**
     * 我的分组
     */
    private String distributorGroup;
    /**排行top*/
    private List<RebateRankingTopVo> rebateRankingTop;
    /**当前分销员佣金信息*/
    private UserRebateVo userRebate;
    /**当前分销员返利佣金排名*/
    private Integer rebateRanking;
    /**返利轮播信息*/
    @JsonProperty("RebateOrderList")
    private List<RebateOrderListVo> rebateOrderList;
    /**是否是分销员*/
    private Integer isDistributor;
    /**最低等级名称*/
    private String lowerLevelName;

    private Integer hasPromotionLanguage;

    /**分销开关*/
    private Byte status;

    /**分销审核开关*/
    private Byte judgeStatus;
    /**邀请码配置 0：关闭；1：开启*/
    private Integer invitationCodeCfg;
    /** 提现开关*/
    private Byte withdrawStatus;
    /**分销员是否具有独立商品推广页的权限 0:不具有 1:具有*/
    private Byte personalPromote;
    /**分销员是否已上传二维码 0:未上传 1:已上传*/
    private Byte qrCode;
}
