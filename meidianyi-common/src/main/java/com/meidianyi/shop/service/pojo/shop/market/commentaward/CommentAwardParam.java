package com.meidianyi.shop.service.pojo.shop.market.commentaward;

import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/8/20 13:58
 */
@Getter
@Setter
public class CommentAwardParam {

    @NotNull(groups = UpdateGroup.class)
    private Integer    id;
    /**
     * 活动名称
     */
    @NotNull
    private String     name;
    /**
     * 开始时间
     */
    private Timestamp startTime;
    /**
     * 结束时间
     */
    private Timestamp  endTime;
    /**
     * 永久有效  1
     */
    @NotNull
    private Byte       isForever;
    /**
     * 优先级
     */
    @Range(min = 0,max = 100)
    private Integer    level;
    /**
     * 商品类型 1全部商品 2指定商品 3 实际品论比较少的商品
     */
    @NotNull
    @Range(min = 1,max = 3)
    private Byte       goodsType;
    /**
     * 对应商品
     */
    private String     goodsIds;
    /**
     * 品论数
     */
    private Integer    commentNum;
    /**
     * 评价类型 1评价即送 2 自定义
     */
    @NotNull
    @Range(min = 1,max = 2)
    private Byte       commentType;
    /**
     * 评价字数条件
     */
    private Integer    commentWords;
    /**
     * 嗮图
     */
    private Byte       hasPicNum;
    /**
     * 五星好评
     */
    private Byte       hasFiveStars;
    /**
     * 奖品类型 1积分 2优惠卷 3 余额 4幸运大抽奖 5自定义
     */
    private Integer    awardType;
    /**
     * 积分数
     */
    private Integer    score;
    /**
     * 评价奖励活动id，逗号分隔  优惠卷或者抽奖
     */
    private String     activityId;
    /**
     * 用户余额
     */
    @DecimalMin("0")
    private BigDecimal account;
    /**
     * 奖品份数
     */
    @Min(0)
    private Integer    awardNum;
    /**
     * 设置链接
     */
    private String     awardPath;
    /**
     * 活动图片
     */
    private String     awardImg;
    /**
     * 首次评价商品
     */
    @NotNull
    private Byte       firstCommentGoods;
}
