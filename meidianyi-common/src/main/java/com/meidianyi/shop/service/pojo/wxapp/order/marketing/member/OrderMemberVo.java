package com.meidianyi.shop.service.pojo.wxapp.order.marketing.member;

import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.base.BaseMarketingBaseVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 王帅
 */
@Getter
@Setter
@Slf4j
@ToString
public class OrderMemberVo extends BaseMarketingBaseVo {
    private ValidUserCardBean info;
    /**
     * 使用pojo初始化自己
     * @param source
     * @return
     */
    public OrderMemberVo init(ValidUserCardBean source){
        setInfo(source);
        this.setBaseCardId(source.getCardId());
        this.setBaseCardType(source.getCardType());
        return this;
    }
}
