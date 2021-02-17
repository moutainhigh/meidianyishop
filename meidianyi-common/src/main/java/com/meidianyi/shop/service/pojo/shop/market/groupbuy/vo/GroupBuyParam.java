package com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.validator.ListValid;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyProductParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyShareConfigParam;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/7/18 18:12
 */
@Data
@NoArgsConstructor
public class GroupBuyParam {


    @NotBlank
    private String name;
    /**
     * 商品id
     */
    @NotNull
    private String goodsId;
    /**
     * 预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数
     */
    private Integer preTime;
    /**
     * 参加活动打标签
     */
    private Byte activityTag;
    /**
     * 参加活动打标签id
     */
    private String activityTagId;
    /**
     * 成团人数 不可小于2人,保存后不可编辑
     */
    @NotNull
    private Short limitAmount;
    /**
     * 每人参团数量限制 0表示不限制
     */
    @NotNull
    private Short joinLimit;
    /**
     * 每人开团数量限制  0表示不限制数量
     */
    @NotNull
    private Short openLimit;
    /**
     * 默认成团 0 不默认 1 默认成团
     */
    private Byte isDefault =0;
    /**
     * 有效期
     */
    @NotNull
    private Timestamp startTime;
    @NotNull
    private Timestamp endTime;
    /**
     * 最少购买数 0不限制
     * limit_buy_num
     */
    private Short limitBuyNum =0;
    /**
     * 最多购买数 0 不限制
     */
    private Short limitMaxNum =0;

    /**
     * 状态： 1：启用  0： 禁用
     */
    private Byte status = BaseConstant.ACTIVITY_STATUS_NORMAL;

    /**
     * 活动类型：1：普通拼团，2：老带新团
     */
    @NotNull
    private Byte activityType;
    /**
     * 是否开启团长优惠：0：不开启，1：开启
     */
    private Byte isGrouperCheap =0;
    /**
     * 是否免运费 1 免运费 2 使用原商品运费模板
     */
    @NotNull
    private Byte shippingType =2;
    /**
     * 拼团失败发放优惠券
     */
    private String rewardCouponId;
    /**
     * 显示文明
     */
    private String activityCopywriting;

    /**
     * 分享设置
     */
    private GroupBuyShareConfigParam share;


    private String shareConfig;

    /**
     * 初始化等级
     */
    private Integer level=0;
    /**
     * 初始化数量
     */
    private Integer beginNum=0;
    /**
     * 产品规格配置
     */
    @NotNull
    @ListValid(min = 1)
    @Valid
    private List<GroupBuyProductParam> product;
}
