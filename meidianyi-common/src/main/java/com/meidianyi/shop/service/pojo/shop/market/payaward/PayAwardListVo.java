package com.meidianyi.shop.service.pojo.shop.market.payaward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/8/13 14:59
 */
@Getter
@Setter
public class PayAwardListVo {

    private Integer id;
    private String activityNames;
    /**
     * 有效期类型 0定期1永久
     */
    private Byte timeType;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte status;
    /**
     *  商品范围类型
     */
    private Integer goodsAreaType;
    /**
     * 最少支付金额
     */
    private BigDecimal minPayMoney;
    /**
     * 优先级
     */
    private Integer actFirst;
    /**
     * 奖励json
     */
    @JsonIgnore
    private String awardList;
    /**
     * 奖励内容
     */
    private  List<PayAwardContentBo>  awardContentList;

    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;
}
