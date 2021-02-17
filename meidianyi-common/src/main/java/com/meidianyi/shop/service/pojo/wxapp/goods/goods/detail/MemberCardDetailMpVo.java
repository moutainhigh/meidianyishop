package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2019年11月08日
 */
@Data
public class MemberCardDetailMpVo {
    private Integer id;
    private String cardName;
    /**
     * 是否直接领取：0直接领，1需要购买，2需要领取码
     */
    private Byte isPay;
    /**需要购买时的费用*/
    private BigDecimal payFee;
    /**用户对本卡的使用状态：0 待领取，1已领取，2待激活，3待续费，4已过期*/
    private Byte status;

    public MemberCardDetailMpVo() {
    }
    public MemberCardDetailMpVo(MemberCardRecord record) {
        this.id = record.getId();
        this.cardName = record.getCardName();
        this.isPay = record.getIsPay();
        this.payFee = record.getPayFee();
    }
}
