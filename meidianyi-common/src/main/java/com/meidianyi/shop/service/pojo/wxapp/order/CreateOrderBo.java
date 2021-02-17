package com.meidianyi.shop.service.pojo.wxapp.order;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.coupon.OrderCouponVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 创建订单bo
 * @author 王帅
 */
@Getter
@Setter
@Builder
@ToString
public class CreateOrderBo {
    private List<Byte> orderType;
    private StorePojo store;
    private UserAddressVo address;
    private PaymentVo payment;
    private InvoiceVo invoice;
    private OrderMemberVo currentMember;
    private OrderCouponVo currentCupon;
    @Builder.Default
    private List<OrderGoodsBo> orderGoodsBo = null;
    private Integer orderId;

    public void intoRecord(OrderInfoRecord orderRecord){
        if(address != null && !orderType.contains(BaseConstant.ACTIVITY_TYPE_GIVE_GIFT)){
            //非送礼地址信息赋值
            orderRecord.setConsignee(address.getConsignee());
            orderRecord.setAddressId(address.getAddressId());
            orderRecord.setProvinceCode(address.getProvinceCode());
            orderRecord.setProvinceName(address.getProvinceName());
            orderRecord.setCityCode(address.getCityCode());
            orderRecord.setCityName(address.getCityName());
            orderRecord.setDistrictCode(address.getDistrictCode());
            orderRecord.setDistrictName(address.getDistrictName());
            orderRecord.setAddress(address.getAddress());
            orderRecord.setCompleteAddress(address.getCompleteAddress());
            orderRecord.setZipcode(address.getZipcode());
            orderRecord.setMobile(address.getMobile());
            orderRecord.setLat(address.getLat());
            orderRecord.setLng(address.getLng());
        }
        if (invoice != null) {
            //发票信息
            orderRecord.setInvoiceId(invoice.getId());
            orderRecord.setInvoiceContent((int)invoice.getType());
            orderRecord.setInvoiceTitle(Util.toJson(invoice.getTitle()));
        }
    }
}
