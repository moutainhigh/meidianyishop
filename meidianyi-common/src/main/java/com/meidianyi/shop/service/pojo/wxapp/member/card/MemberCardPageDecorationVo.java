package com.meidianyi.shop.service.pojo.wxapp.member.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-11-20 14:49
 **/
@Getter
@Setter
public class MemberCardPageDecorationVo {
    /**
     * 会员卡状态：
     *-1未领取， 1已领取， 2过期= ， 3停用,4已达到领取上限，5无库存
     */
    private Byte status = 1;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer id;
    private Byte activation;
    private String bgColor;
    private String bgImg;
    private Byte bgType;
    private String cardGrade;
    private Integer cardId;
    private String cardName;
    private Byte cardType;
    private Timestamp endTime;
    private Byte expireType;
    private Byte flag;
    private Byte isPay;
    private BigDecimal payFee;
    private Byte payType;
    private Integer receiveDay;
    private Byte dateType;
    private Integer limit;
    private Integer stock;
    private String shopImg;
    /**
     * 限次卡门店使用次数
     */
    private Integer count;

    private Byte hiddenCard=0;

}
