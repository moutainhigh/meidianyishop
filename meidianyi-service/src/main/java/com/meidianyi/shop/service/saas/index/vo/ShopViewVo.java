package com.meidianyi.shop.service.saas.index.vo;

/**
 * @author luguangyao
 */
public class ShopViewVo {

    private AccountStatisticsInfo accountStatisticsInfo;

    private OrderStatisticsInfo orderStatisticsInfo;

    private UserStatisticsInfo userStatisticsInfo;

    public AccountStatisticsInfo getAccountStatisticsInfo() {
        return accountStatisticsInfo;
    }

    public void setAccountStatisticsInfo(AccountStatisticsInfo accountStatisticsInfo) {
        this.accountStatisticsInfo = accountStatisticsInfo;
    }

    public OrderStatisticsInfo getOrderStatisticsInfo() {
        return orderStatisticsInfo;
    }

    public void setOrderStatisticsInfo(OrderStatisticsInfo orderStatisticsInfo) {
        this.orderStatisticsInfo = orderStatisticsInfo;
    }

    public UserStatisticsInfo getUserStatisticsInfo() {
        return userStatisticsInfo;
    }

    public void setUserStatisticsInfo(UserStatisticsInfo userStatisticsInfo) {
        this.userStatisticsInfo = userStatisticsInfo;
    }
}
