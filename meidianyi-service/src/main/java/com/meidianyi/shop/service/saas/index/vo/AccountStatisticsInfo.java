package com.meidianyi.shop.service.saas.index.vo;

/**
 * system 概览-概览-账户统计
 * @author luguangyao
 */
public class AccountStatisticsInfo {

    /**
     * 账户总数
     */
    private Integer allAccountNum;

    /**
     * 店铺总数
     */
    private Integer allShopNum;

    /**
     * 有效店铺数量
     */
    private Integer effectiveShopNum;

    /**
     * 使用中店铺数量
     */
    private Integer usedShopNum;

    /**
     * 将过期店铺数量
     */
    private Integer toExpireShopNum;

    /**
     * 已过期店铺数量
     */
    private Integer expiredShopNum;

    /**
     * 已禁用店铺数量
     */
    private  Integer disabledShopNum;

    /**
     * 授权成功店铺数量
     */
    private Integer authorizedShopNum;
    /**
     * 未授权成功店铺数量
     */
    private  Integer unauthorizedShopNum;

    /**
     * 开通支付店铺数量
     */
    private Integer openedPaymentShopNum;

    /**
     * 未开通支付店铺数量
     */
    private Integer notOpenPaymentShopNum;

    public Integer getAllAccountNum() {
        return allAccountNum;
    }

    public void setAllAccountNum(Integer allAccountNum) {
        this.allAccountNum = allAccountNum;
    }

    public Integer getAllShopNum() {
        return allShopNum;
    }

    public void setAllShopNum(Integer allShopNum) {
        this.allShopNum = allShopNum;
    }

    public Integer getEffectiveShopNum() {
        return effectiveShopNum;
    }

    public void setEffectiveShopNum(Integer effectiveShopNum) {
        this.effectiveShopNum = effectiveShopNum;
    }

    public Integer getUsedShopNum() {
        return usedShopNum;
    }

    public void setUsedShopNum(Integer usedShopNum) {
        this.usedShopNum = usedShopNum;
    }

    public Integer getToExpireShopNum() {
        return toExpireShopNum;
    }

    public void setToExpireShopNum(Integer toExpireShopNum) {
        this.toExpireShopNum = toExpireShopNum;
    }

    public Integer getExpiredShopNum() {
        return expiredShopNum;
    }

    public void setExpiredShopNum(Integer expiredShopNum) {
        this.expiredShopNum = expiredShopNum;
    }

    public Integer getDisabledShopNum() {
        return disabledShopNum;
    }

    public void setDisabledShopNum(Integer disabledShopNum) {
        this.disabledShopNum = disabledShopNum;
    }

    public Integer getAuthorizedShopNum() {
        return authorizedShopNum;
    }

    public void setAuthorizedShopNum(Integer authorizedShopNum) {
        this.authorizedShopNum = authorizedShopNum;
    }

    public Integer getUnauthorizedShopNum() {
        return unauthorizedShopNum;
    }

    public void setUnauthorizedShopNum(Integer unauthorizedShopNum) {
        this.unauthorizedShopNum = unauthorizedShopNum;
    }

    public Integer getOpenedPaymentShopNum() {
        return openedPaymentShopNum;
    }

    public void setOpenedPaymentShopNum(Integer openedPaymentShopNum) {
        this.openedPaymentShopNum = openedPaymentShopNum;
    }

    public Integer getNotOpenPaymentShopNum() {
        return notOpenPaymentShopNum;
    }

    public void setNotOpenPaymentShopNum(Integer notOpenPaymentShopNum) {
        this.notOpenPaymentShopNum = notOpenPaymentShopNum;
    }
}
