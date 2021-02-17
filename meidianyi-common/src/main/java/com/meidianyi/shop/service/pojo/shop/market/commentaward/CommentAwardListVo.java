package com.meidianyi.shop.service.pojo.shop.market.commentaward;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/8/20 14:50
 */
@Data
public class CommentAwardListVo {

    private Integer    id;
    /**
     * 活动名称
     */
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
    private Byte       isForever;
    /**
     * 商品类型 1全部商品 2指定商品 3 实际品论比较少的商品
     */
    private Byte       goodsType;
    /**
     * 评价类型 1评价即送 2 自定义
     */
    private Byte       commentType;

    /**
     * 奖品类型 1积分 2优惠卷 3 余额 4幸运大抽奖 5自定义
     */
    private Integer    awardType;
    /**
     * 状态  1启用
     */
    private Byte       status;
    /**
     * 状态
     */
    private Byte       currentStatus;

    /**
     * 优先级
     */
    private Integer    level;


}
