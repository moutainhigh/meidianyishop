package com.meidianyi.shop.service.saas.index.vo;

import java.util.List;


/**
 * @author luguangyao
 */
public class OrderStatisticsInfo {

    private Integer allOrderNum;

    private List<BaseInfo> orderNumInfos;

    private List<BaseMoneyInfo> orderMoneyInfos;

    private String wxPayed;

    /**
     * 余额支付
     */
    private String balancePayed;

    /**
     * 卡余额支付
     */
    private String cardBalancePayed;

    /**
     * 积分支付
     */
    private String integralPayed;

    public Integer getAllOrderNum() {
        return allOrderNum;
    }

    public void setAllOrderNum(Integer allOrderNum) {
        this.allOrderNum = allOrderNum;
    }

    public List<BaseInfo> getOrderNumInfos() {
        return orderNumInfos;
    }

    public void setOrderNumInfos(List<BaseInfo> orderNumInfos) {
        this.orderNumInfos = orderNumInfos;
    }

    public List<BaseMoneyInfo> getOrderMoneyInfos() {
        return orderMoneyInfos;
    }

    public void setOrderMoneyInfos(List<BaseMoneyInfo> orderMoneyInfos) {
        this.orderMoneyInfos = orderMoneyInfos;
    }

    public String getWxPayed() {
        return wxPayed;
    }

    public void setWxPayed(String wxPayed) {
        this.wxPayed = wxPayed;
    }

    public String getBalancePayed() {
        return balancePayed;
    }

    public void setBalancePayed(String balancePayed) {
        this.balancePayed = balancePayed;
    }

    public String getCardBalancePayed() {
        return cardBalancePayed;
    }

    public void setCardBalancePayed(String cardBalancePayed) {
        this.cardBalancePayed = cardBalancePayed;
    }

    public String getIntegralPayed() {
        return integralPayed;
    }

    public void setIntegralPayed(String integralPayed) {
        this.integralPayed = integralPayed;
    }
}
