package com.meidianyi.shop.service.pojo.shop.market;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 营销活动订单
 * @author: 王兵兵
 * @create: 2019-09-04 15:41
 **/
@Data
public class MarketOrderListVo {

    private String orderSn;
    /** 行信息 */
    private List<MarketOrderGoodsListVo> goods;
    private Byte orderStatus;
    /**退款/退货状态*/
    private Byte refundStatus;

    /** 收件人姓名 */
    private String consignee;
    /** 收件人手机 */
    private String mobile;
    /** 支付方式 */
    private String payCode;
    /** 配送方式:0 快递 1 自提 */
    private Byte deliverType;
    /** 下单时间 */
    private Timestamp createTime;
    /** 快递费金额 */
    private BigDecimal shippingFee;
    /** 支付金额 */
    private BigDecimal moneyPaid;

    /**积分抵扣金额*/
    private BigDecimal scoreDiscount;
    /**用户消费余额*/
    private BigDecimal useAccount;
    /**会员卡消费金额*/
    private BigDecimal memberCardBalance;

    /** 下单人信息 */
    private Integer userId;
    private String username;
    private String userMobile;
}
