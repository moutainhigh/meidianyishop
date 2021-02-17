package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liufei
 * @date 2/21/2020
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssiWxDataShop implements PendingRule<AssiWxDataShop> {
    /**  注册小程序 0：已完成，1：未完成 */
    public Metadata registerApplet;
    /**  授权小程序 0：已完成，1：未完成 */
    public Metadata authApplet;
    /**  配置小程序客服 0：已完成，1：未完成 */
    public Metadata appletService;
    /**  开通微信支付 0：已完成，1：未完成 */
    public Metadata wxPayment;
    /**  配置微信支付 0：已完成，1：未完成 */
    public Metadata configWxPayment;

    /**
     * 子账号设置 非0：已完成子账号设置，0：未完成
     */
    public Metadata childAccountConf;
    /**
     * 公众号 非0：已授权公众号，0：未授权公众号
     */
    public Metadata officialAccountConf;

    @Override
    public AssiWxDataShop ruleHandler() {
        handler2(officialAccountConf, childAccountConf);
        return this;
    }

    @Override
    public int getUnFinished() {
        int num = unFinished(registerApplet, authApplet, appletService, wxPayment, configWxPayment, childAccountConf, officialAccountConf);
        log.debug("WxShop unFinished Num:{}", num);
        return num;
    }
}
