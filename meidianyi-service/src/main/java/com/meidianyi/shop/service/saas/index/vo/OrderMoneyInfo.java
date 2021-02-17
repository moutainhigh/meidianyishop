package com.meidianyi.shop.service.saas.index.vo;

import java.math.BigDecimal;

/**
 * @author luguangyao
 */
public class OrderMoneyInfo {

    private String time;

    private BigDecimal wxPayed = new BigDecimal(0);

    /**
     * 余额支付
     */
    private BigDecimal balancePayed= new BigDecimal(0);;

    /**
     * 卡余额支付
     */
    private BigDecimal cardBalancePayed= new BigDecimal(0);;

    /**
     * 积分支付
     */
    private BigDecimal integralPayed= new BigDecimal(0);;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getWxPayed() {
        return wxPayed;
    }

    public void setWxPayed(BigDecimal wxPayed) {
        this.wxPayed = wxPayed;
    }

    public BigDecimal getBalancePayed() {
        return balancePayed;
    }

    public void setBalancePayed(BigDecimal balancePayed) {
        this.balancePayed = balancePayed;
    }

    public BigDecimal getCardBalancePayed() {
        return cardBalancePayed;
    }

    public void setCardBalancePayed(BigDecimal cardBalancePayed) {
        this.cardBalancePayed = cardBalancePayed;
    }

    public BigDecimal getIntegralPayed() {
        return integralPayed;
    }

    public void setIntegralPayed(BigDecimal integralPayed) {
        this.integralPayed = integralPayed;
    }

    public BigDecimal getAll(){
        return this.balancePayed.add(this.cardBalancePayed).add(this.integralPayed).add(this.wxPayed);
    }
}
