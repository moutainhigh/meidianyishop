package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 小程序-好友助力展示信息
 * @author liangchen
 * @date 2020.02.25
 */
@Data
public class PromoteInfo {
    /** 活动id */
    private Integer id;
    /** 奖励类型：0赠送商品，1折扣商品，2赠送优惠券 */
    private Byte rewardType;
    /** 奖励内容 */
    private FpRewardContent rewardContent;
    /** 助力次数 */
    private Integer hasLaunchNum;
    /** 活动库存 */
    private Integer marketStore;
    /** 商品详情 */
    private GoodsInfo goodsInfo;
    /** 优惠券详情 */
    private CouponInfo couponInfo;
    /** 活动状态：0未开始，1进行中，2已结束 */
    private Byte actStatus;
    /** 助力进度 */
    private Byte promoteStatus;
    /** 所需助力次数 */
    private Integer promoteTimes;
    /** 已被助力次数总次数 */
    private Integer hasPromoteTimes;
    /** 是否可以再次助力 */
    private Byte canLaunch;
    /** 活动状态：0未停用，1已停用 */
    private Byte isBlock;
    /** 是否删除 */
    private Byte delFlag;
    /** 发起限制次数，0不限制 */
    private Byte launchLimitTimes;
    /** 发起次数限制时长，0不限制 */
    private Integer launchLimitDuration;
    /** 发起次数限制时长单位：0小时 1天，2周，3月，4年 */
    private Byte launchLimitUnit;
    /** 好友助力明细列表 */
    private List<PromoteDetail> promoteDetailList;
    /** 订单 */
    private String orderSn;
    /** 助力完成订单操作标识：0不可下单，1立即下单，2查看订单详情 */
    private Byte orderFlag;
    /** 单个用户每天最多可帮忙助力次数 */
    private Integer promoteTimesPerDay;
    /** 是否可以继续助力 */
    private CanPromote canPromote;
    /** 分享可得助力次数*/
    private Byte shareCreateTimes;
    /** 是否可以分享获取助力次数*/
    private Byte canShare;
    /** 活动结束时间 */
    private Timestamp endTime;
    /** 剩余秒数 */
    private Long surplusSecond;
    /** 助力总值 */
    private Integer hasPromoteValue;
    /** 发起id */
    private Integer launchId;
    /** 更多的助力活动 */
    private List<PromoteActList> promoteActList;
    /** 发起标识 */
    private Integer launchFlag;
    /** 所需助力总值 */
    private BigDecimal promoteAmount;
    /** 单次助力值类型：0平均，1随机 */
    private Byte promoteType;
    /** 助力活动分享样式类型：0默认样式，1自定义样式 */
    private Byte activityShareType;
    /** 自定义分享样式文案 */
    private String customShareWord;
    /** 自定义分享图片类型：0首页截图，1自定义图片 */
    private Byte shareImgType;
    /** 自定义分享样式图片路径 */
    private String customImgPath;
    /** 奖励有效期 */
    private Integer rewardDuration;
    /** 奖励有效期单位 */
    private Byte rewardDurationUnit;
    /** 奖励倒计时 */
    private Long rewardSpurTime;
    /** 奖品记录id */
    private Integer rewardRecordId;
    /** 0可不授权个人信息，1必须授权 */
    private Byte promoteCondition;
    /** 活动说明相关 */
    private PromoteActCopywriting actCopywriting;
}
