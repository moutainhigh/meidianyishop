package com.meidianyi.shop.service.saas.index.vo;

/**
 * @author luguangyao
 */
public class BaseMoneyInfo {

    private String date;

    private OrderMoneyInfo money;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OrderMoneyInfo getMoney() {
        return money;
    }

    public void setMoney(OrderMoneyInfo money) {
        this.money = money;
    }
}
