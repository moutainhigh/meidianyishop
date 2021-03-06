/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.PaymentRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PaymentRecordRecord extends UpdatableRecordImpl<PaymentRecordRecord> {

    private static final long serialVersionUID = 1858849530;

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.shop_id</code>. 店铺id
     */
    public void setShopId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.shop_id</code>. 店铺id
     */
    public Integer getShopId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.pay_sn</code>. 支付流水号
     */
    public void setPaySn(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.pay_sn</code>. 支付流水号
     */
    public String getPaySn() {
        return (String) get(2);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.pay_code</code>. 支付宝:alipay,微信：？，...
     */
    public void setPayCode(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.pay_code</code>. 支付宝:alipay,微信：？，...
     */
    public String getPayCode() {
        return (String) get(3);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.pay_code_alias</code>. 支付宝:alipay,微信：？，...
     */
    public void setPayCodeAlias(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.pay_code_alias</code>. 支付宝:alipay,微信：？，...
     */
    public String getPayCodeAlias() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.trade_no</code>. 各平台交易号
     */
    public void setTradeNo(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.trade_no</code>. 各平台交易号
     */
    public String getTradeNo() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.trdae_status</code>. 交易状态0:成功，其它失败
     */
    public void setTrdaeStatus(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.trdae_status</code>. 交易状态0:成功，其它失败
     */
    public Byte getTrdaeStatus() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.trdae_origin_status</code>. 原始交易状态
     */
    public void setTrdaeOriginStatus(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.trdae_origin_status</code>. 原始交易状态
     */
    public String getTrdaeOriginStatus() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.subject</code>. 商品名称
     */
    public void setSubject(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.subject</code>. 商品名称
     */
    public String getSubject() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.quantity</code>. 购买数量
     */
    public void setQuantity(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.quantity</code>. 购买数量
     */
    public Integer getQuantity() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.order_sn</code>. 网站订单号
     */
    public void setOrderSn(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.order_sn</code>. 网站订单号
     */
    public String getOrderSn() {
        return (String) get(10);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.main_order_sn</code>. 网站主订单号
     */
    public void setMainOrderSn(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.main_order_sn</code>. 网站主订单号
     */
    public String getMainOrderSn() {
        return (String) get(11);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.total_fee</code>. 交易金额
     */
    public void setTotalFee(BigDecimal value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.total_fee</code>. 交易金额
     */
    public BigDecimal getTotalFee() {
        return (BigDecimal) get(12);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.buyer_id</code>. 买家支付用户号
     */
    public void setBuyerId(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.buyer_id</code>. 买家支付用户号
     */
    public String getBuyerId() {
        return (String) get(13);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.buyer_account</code>. 各平台买家支付账号
     */
    public void setBuyerAccount(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.buyer_account</code>. 各平台买家支付账号
     */
    public String getBuyerAccount() {
        return (String) get(14);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.seller_id</code>. 收款方用户号
     */
    public void setSellerId(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.seller_id</code>. 收款方用户号
     */
    public String getSellerId() {
        return (String) get(15);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.seller_account</code>. 各平台收款方支付账号
     */
    public void setSellerAccount(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.seller_account</code>. 各平台收款方支付账号
     */
    public String getSellerAccount() {
        return (String) get(16);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.gmt_create</code>. 支付交易创建时间
     */
    public void setGmtCreate(Timestamp value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.gmt_create</code>. 支付交易创建时间
     */
    public Timestamp getGmtCreate() {
        return (Timestamp) get(17);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.notify_time</code>. 通知时间
     */
    public void setNotifyTime(Timestamp value) {
        set(18, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.notify_time</code>. 通知时间
     */
    public Timestamp getNotifyTime() {
        return (Timestamp) get(18);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.gmt_pay_time</code>. 交易付款时间
     */
    public void setGmtPayTime(Timestamp value) {
        set(19, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.gmt_pay_time</code>. 交易付款时间
     */
    public Timestamp getGmtPayTime() {
        return (Timestamp) get(19);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.gmt_close_time</code>. 交易关闭时间
     */
    public void setGmtCloseTime(Timestamp value) {
        set(20, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.gmt_close_time</code>. 交易关闭时间
     */
    public Timestamp getGmtCloseTime() {
        return (Timestamp) get(20);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.create_time</code>. 插入时间
     */
    public void setCreateTime(Timestamp value) {
        set(21, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.create_time</code>. 插入时间
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(21);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.remark1</code>. 自定义备注  建议用于存储原始数据
     */
    public void setRemark1(String value) {
        set(22, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.remark1</code>. 自定义备注  建议用于存储原始数据
     */
    public String getRemark1() {
        return (String) get(22);
    }

    /**
     * Setter for <code>mini_shop_6797286.b2c_payment_record.remark2</code>. 自定义备注
     */
    public void setRemark2(String value) {
        set(23, value);
    }

    /**
     * Getter for <code>mini_shop_6797286.b2c_payment_record.remark2</code>. 自定义备注
     */
    public String getRemark2() {
        return (String) get(23);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PaymentRecordRecord
     */
    public PaymentRecordRecord() {
        super(PaymentRecord.PAYMENT_RECORD);
    }

    /**
     * Create a detached, initialised PaymentRecordRecord
     */
    public PaymentRecordRecord(Integer id, Integer shopId, String paySn, String payCode, String payCodeAlias, String tradeNo, Byte trdaeStatus, String trdaeOriginStatus, String subject, Integer quantity, String orderSn, String mainOrderSn, BigDecimal totalFee, String buyerId, String buyerAccount, String sellerId, String sellerAccount, Timestamp gmtCreate, Timestamp notifyTime, Timestamp gmtPayTime, Timestamp gmtCloseTime, Timestamp createTime, String remark1, String remark2) {
        super(PaymentRecord.PAYMENT_RECORD);

        set(0, id);
        set(1, shopId);
        set(2, paySn);
        set(3, payCode);
        set(4, payCodeAlias);
        set(5, tradeNo);
        set(6, trdaeStatus);
        set(7, trdaeOriginStatus);
        set(8, subject);
        set(9, quantity);
        set(10, orderSn);
        set(11, mainOrderSn);
        set(12, totalFee);
        set(13, buyerId);
        set(14, buyerAccount);
        set(15, sellerId);
        set(16, sellerAccount);
        set(17, gmtCreate);
        set(18, notifyTime);
        set(19, gmtPayTime);
        set(20, gmtCloseTime);
        set(21, createTime);
        set(22, remark1);
        set(23, remark2);
    }
}
