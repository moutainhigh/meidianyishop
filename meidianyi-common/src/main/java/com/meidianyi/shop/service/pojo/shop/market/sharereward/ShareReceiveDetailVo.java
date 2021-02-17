package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/20
 * @description
 */
@Data
public class ShareReceiveDetailVo {
    /**
     * 活动id
     */
    public Integer shareId;
    /**
     * 用户id
     */
    public Integer userId;
    /**
     * 用户昵称
     */
    public String username;
    /**
     * 手机号
     */
    public String mobile;
    /**
     * 分享商品id
     */
    public Integer goodsId;
    /**
     * 分享商品名称
     */
    public String goodsName;
    /**
     * 奖励级别
     */
    public Byte awardLevel;
    @JsonIgnore
    public String firstLevelRule;
    @JsonIgnore
    public String secondLevelRule;
    @JsonIgnore
    public String thirdLevelRule;
    /**
     * 邀请用户数
     */
    public Integer inviteUserNum;
    /**
     * 邀请新用户数
     */
    public Integer inviteNewUserNum;
    /**
     * 奖励类型
     */
    public Byte rewardType;
    /**
     * 奖励类型为积分时，显示具体积分值
     */
    public Integer score;
    /**
     * 领取时间
     */
    public Timestamp createTime;

}
