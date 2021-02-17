package com.meidianyi.shop.service.pojo.wxapp.order.marketing.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author 王帅
 */
@Getter
@Setter
@ToString
public abstract class BaseMarketingBaseVo {
    /**折扣金额*/
    private BigDecimal totalDiscount;
    private BigDecimal totalGoodsNumber;
    private BigDecimal totalPrice;
    private BigDecimal ratio;
    /**折扣标识（如优惠卷sn,会员卡no）*/
    private String identity;
    private List<OrderGoodsBo> bos;
    /**
     * 会员卡属性
     */
    @JsonIgnore
    private Byte baseCardType;
    @JsonIgnore
    private Integer baseCardId;
    /**
     * 初始化折扣比例
     * @return 折扣比例
     */
    public BigDecimal initRatio (){
        //总价可以为0
        if (totalPrice.compareTo(BigDecimal.ZERO)==0){
            ratio =BigDecimal.ZERO;
        }else {
            if(totalDiscount.compareTo(totalPrice) > 0){
                //折扣超总价
                totalDiscount = totalPrice;
            }
            ratio = BigDecimalUtil.divide(totalDiscount ,totalPrice, RoundingMode.FLOOR, 8);
        }
        return ratio;
    }

    public boolean checkRatio(){
        if(ratio != null && BigDecimalUtil.compareTo(ratio, BigDecimal.ZERO) >= 0 && BigDecimalUtil.compareTo(ratio, BigDecimal.ONE) <= 0){
            return true;
        }
        return false;
    }
}
