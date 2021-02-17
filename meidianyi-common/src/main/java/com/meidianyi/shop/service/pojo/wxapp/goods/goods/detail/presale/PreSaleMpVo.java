package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.presale;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

/**
 * 小程序-商品详情-预售活动
 * @author 李晓冰
 * @date 2020年01月09日
 */
@Getter
@Setter
public class PreSaleMpVo extends GoodsActivityDetailMp {
    /**预售类型，0定金支付，1全款支付*/
    private Byte preSaleType;
    /**发货类型，1指定发货时间，2支付完成后n天发货*/
    private Byte deliverType;
    /**发货类型为1，发货时间*/
    private Timestamp deliverTime;
    /**发货类型为2，天数*/
    private Integer deliverDays;
    /**优惠券是否叠加，false否 true是*/
    private Boolean useCoupon;
    /**定金退还策略 true 退，false 不退*/
    private Boolean returnDeposit;
    /**是否展示预售数量 true 是，false 否*/
    private Boolean showPreSaleNumber;
    /**已预订数量（直接去取的数据库sale_number之和）*/
    private Integer saleNumber;
    /**是否支持原价购买true 支持，false否*/
    private Boolean originalBuy;
    /**用户单次可购买数量*/
    private Integer limitAmount;
    /**预售总库存*/
    private Integer stock;
    /**定金支付时使用 尾款支付开始时间*/
    private Timestamp finalPaymentStart;
    /**尾款支付结束时间*/
    private Timestamp finalPaymentEnd;

    private List<PreSalePrdMpVo> preSalePrdMpVos;
}
