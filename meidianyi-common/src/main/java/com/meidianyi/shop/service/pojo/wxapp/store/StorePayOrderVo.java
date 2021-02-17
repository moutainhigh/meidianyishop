package com.meidianyi.shop.service.pojo.wxapp.store;

import com.meidianyi.shop.service.pojo.shop.config.trade.PaymentConfigVo;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * The type Store pay order vo.
 *
 * @author liufei
 * @date 10 /21/19
 */
@Data
public class StorePayOrderVo {
    /**
     * The Score.用户积分
     */
    public Integer score;
    /**
     * The Account.用户余额
     */
    public BigDecimal account;
    /**
     * The Invoice switch.发票配置开关
     */
    public Byte invoiceSwitch;
    /**
     * The Member card list.可用会员卡列表
     */
    public List<ValidUserCardBean> memberCardList;
    /**
     * The Shop business state.店铺营业状态
     */
    public Byte shopBusinessState;
    /**
     * The Store business state.门店营业状态
     */
    public Byte storeBusinessState;
    /**
     * The Shop logo.店铺logo
     */
    public String shopLogo;
    /**
     * The Store buy.门店买单开关配置
     */
    public Byte storeBuy;
    /**
     * The Del flag.门店删除标识
     */
    public Byte delFlag;
    /**
     * 支付方式开关
     */
    private List<PaymentConfigVo> payStatusList;
    /**
     * 默认支付配置
     */
    private Map<String, Byte> defaultPayConf;
    /**
     * 积分抵扣比例(积分使用上限)
     */
    private Integer scoreDiscountRatio;
    /**
     * 积分支付下限
     */
    private Integer scorePayNum;
    /**
     * 积分支付下限开关(0不限制，1限制)
     */
    private Byte scorePayLimit;
    /**
     * 积分兑换比
     */
    private Integer scoreProportion;
}
