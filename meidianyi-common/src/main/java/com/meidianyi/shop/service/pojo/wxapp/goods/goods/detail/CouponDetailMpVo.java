package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 优惠券
 * @author 李晓冰
 * @date 2019年11月08日
 */
@Data
public class CouponDetailMpVo {
    private Integer id;
    /**优惠券类型0普通，1分裂*/
    private Byte type;
    private String actName;
    /**优惠券使用时间类型 1领取后开始指定时间段内有效，0固定时间段有效*/
    private Byte validityType;

    private Timestamp startTime;
    private Timestamp endTime;

    private Integer validity;
    private Integer validityHour;
    private Integer validityMinute;
    /**是否使用积分*/
    private Boolean useScore;
    /**积分兑换量*/
    private Integer scoreNumber;
    /**是否会员专享优惠券*/
    private Boolean isCardExclusive;
    /**当前用户是否可以领取*/
    private Boolean canFetch;
    /**是否已有用优惠券*/
    private Boolean alreadyHas;

    /**优惠券类型voucher是减金额，discount打折 random 随机*/
    private String actCode;
    /**优惠券面额*/
    private BigDecimal denomination;
    /** 随机最大金额*/
    private BigDecimal randomMax;
    /**是否存在使用门槛 0否 1是*/
    private Byte useConsumeRestrict;
    /** 满多少可用*/
    private BigDecimal leastConsume;
    /** 领取码*/
    private String validationCode;

    public CouponDetailMpVo() {
    }

    public CouponDetailMpVo(MrkingVoucherRecord record){
        this.setId(record.getId());
        this.setType(record.getType());
        this.setActCode(record.getActCode());
        this.setActName(record.getActName());
        this.setValidityType(record.getValidityType());
        this.setStartTime(record.getStartTime());
        this.setEndTime(record.getEndTime());
        this.setValidity(record.getValidity());
        this.setValidityHour(record.getValidityHour());
        this.setValidityMinute(record.getValidityMinute());
        this.setUseScore(record.getUseScore()==1);
        this.setScoreNumber(record.getScoreNumber());
        this.setIsCardExclusive(StringUtils.isNotEmpty(record.getCardId()));
        this.setDenomination(record.getDenomination());
        this.setUseConsumeRestrict(record.getUseConsumeRestrict());
        this.setLeastConsume(record.getLeastConsume());
        this.setValidationCode(record.getValidationCode());
        this.setRandomMax(record.getRandomMax());
    }
}
