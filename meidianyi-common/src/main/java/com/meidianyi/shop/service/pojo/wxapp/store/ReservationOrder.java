package com.meidianyi.shop.service.pojo.wxapp.store;

import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentVo;
import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceParam;
import com.meidianyi.shop.service.pojo.shop.store.service.order.RecentOrderInfo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * The type Reservation order.
 *
 * @author liufei
 * @date 11 /7/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationOrder {
    /**
     * The Service.门店服务信息
     */
    StoreServiceParam service;
    /**
     * The Store pojo.门店信息
     */
    StorePojo storePojo;
    /**
     * The Recent order info.用户最近的一个服务预约订单信息
     */
    RecentOrderInfo recentOrderInfo;
    /**
     * The Payment vo list.支持的支付方式
     */
    List<PaymentVo> paymentVoList;
    /**
     * The Technician title.门店职称配置
     */
    String technicianTitle;
    /**
     * The Card first.会员卡余额支付 支付开关配置,
     */
    Byte cardFirst;
    /**
     * The Balance first.余额支付 支付开关配置,
     */
    Byte balanceFirst;
    /**
     * The Card list.门店有效用户会员卡列表
     */
    List<ValidUserCardBean> cardList;
    /**
     * The Account.用户余额account
     */
    BigDecimal account;
    /**
     * The Shop avatar.店铺logo
     */
    String shopAvatar;
}
