package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/20
 * @description
 */
@Data
public class ShareRewardInfoVo {
    private Integer id;
    /**
     * 活动名称
     */
    @NotNull
    private String name;
    /**
     * 活动起始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /**
     * 活动结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /**
     * 是否永久有效
     */
    @NotNull
    private Byte isForever;
    /**
     * 活动优先级
     */
    @NotNull
    private Short priority;
    /**
     * 触发条件：1.分享全部商品，2.分享指定商品，3.分享访问量较少商品
     */
    @NotNull
    private Byte condition;
    /**
     * 触发条件为2时：分享指定商品id列表，逗号分隔符
     */
    private String goodsIds;
    /**
     * 触发条件为3时：被分享商品访问量条件
     */
    private Integer goodsPv;
    /**
     * 是否仅邀请未访问过店铺的用户有效
     */
    private Byte visitFirst;

    /**
     * 一级分享规则
     */
    @JsonIgnore
    private String firstLevelRule;
    /**
     * 二级分享规则
     */
    @JsonIgnore
    private String secondLevelRule;
    /**
     * 三级分享规则
     */
    @JsonIgnore
    private String thirdLevelRule;
    /**
     * 分享规则数组
     */
    private List<ShareRule> shareRules;

    private Integer firstAwardNum;
    private Integer secondAwardNum;
    private Integer thirdAwardNum;
}
