package com.meidianyi.shop.service.pojo.wxapp.order.refund;

import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 小程序售后中心退款列表
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class ReturnOrderListMp {
    private Integer retId;
    private List<OrderReturnGoodsVo> goods;
    /**1买家；0商家（包含定时任务）*/
    private Byte role;
    private Byte refundStatus;
    private String returnOrderSn;
    /**申请时间*/
    private Timestamp createTime;
    /**完成时间、撤销时间、拒绝时间*/
    private Timestamp finishTime;
    private Byte returnType;
    private Byte reasonType;
    private String reasonTypeDesc;
    private String reasonDesc;
    private BigDecimal money;
    private BigDecimal shippingFee;
    /**申请凭证*/
    private String goodsImages;
    /**退货凭证（物流）*/
    private String voucherImages;
    private String shippingType;
    /**商家提交快递公司名称*/
    private String shippingName;
    private String shippingNo;
    private String phone;
    /**拒绝退款退货原因*/
    private String refundRefuseReason;
    /**拒绝申请原因*/
    private String applyNotPassReason;
}
