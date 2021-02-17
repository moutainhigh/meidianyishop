package com.meidianyi.shop.service.saas.index.vo;

/**
 * 数据统计-概览-数据展示
 * @author luguangyao
 */
public class DataDisplayInfo {

    /**
     * 访问人数
     */
    private Integer visitUserNum;
    /**
     * 下单数量
     */
    private Integer orderNum;
    /**
     * 支付订单数量
     */
    private Integer payedOrderNum;
    /**
     * 下单用户人数
     */
    private Integer orderUserNum;
    /**
     * 下单金额
     */
    private String orderMoney;
    /**
     * 支付用户人数
     */
    private Integer payedOrderUserNum;
    /**
     * 访问->下单 转化率
     */
    private String visitToOrderData;
    /**
     * 访问->支付 转化率
     */
    private String visitToPayedData;
    /**
     * 下单->支付 转化率
     */
    private String orderToPayedData;

    public Integer getVisitUserNum() {
        return visitUserNum;
    }

    public void setVisitUserNum(Integer visitUserNum) {
        this.visitUserNum = visitUserNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getPayedOrderNum() {
        return payedOrderNum;
    }

    public void setPayedOrderNum(Integer payedOrderNum) {
        this.payedOrderNum = payedOrderNum;
    }

    public Integer getOrderUserNum() {
        return orderUserNum;
    }

    public void setOrderUserNum(Integer orderUserNum) {
        this.orderUserNum = orderUserNum;
    }

    public String getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(String orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getPayedOrderUserNum() {
        return payedOrderUserNum;
    }

    public void setPayedOrderUserNum(Integer payedOrderUserNum) {
        this.payedOrderUserNum = payedOrderUserNum;
    }

    public String getVisitToOrderData() {
        return visitToOrderData;
    }

    public void setVisitToOrderData(String visitToOrderData) {
        this.visitToOrderData = visitToOrderData;
    }

    public String getVisitToPayedData() {
        return visitToPayedData;
    }

    public void setVisitToPayedData(String visitToPayedData) {
        this.visitToPayedData = visitToPayedData;
    }

    public String getOrderToPayedData() {
        return orderToPayedData;
    }

    public void setOrderToPayedData(String orderToPayedData) {
        this.orderToPayedData = orderToPayedData;
    }
}
