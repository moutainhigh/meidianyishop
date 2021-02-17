package com.meidianyi.shop.service.pojo.shop.member.account;

import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 会员卡续费-详情页出参
 * @author liangchen
 * @date 2020.04.08
 */
@Data
public class CardRenewInfoVo extends UserCardParam {
    /** 1:未过期 -1:已过期 */
    protected Byte status;
    /** 门店信息 */
    protected List<StoreBasicVo> storeInfoList;
    /** 用户积分 */
    protected Integer score;
    /** 用户余额 */
    protected BigDecimal account;

    protected Map<String,RenewValidCardList> memberCardList;
    protected String memberCardNo;
    protected Byte cardFirst;
    protected Byte balanceFirst;
    protected String shouldRenewMoney;
    protected String shouldRenewDate;
}
