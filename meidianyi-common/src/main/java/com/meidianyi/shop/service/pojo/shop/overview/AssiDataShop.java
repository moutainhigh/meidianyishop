package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liufei
 * date 2019/7/18
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssiDataShop implements PendingRule<AssiDataShop> {

    /**
     * 店铺首页 0：已完成店铺首页装修，否未装修店铺首页
     */
    public Metadata homePageConf;
    /**
     * 好物圈 1: 已开启好物圈，否未开启
     */
    public Metadata shopRecommendConf;
    /** 客服 0: 已开启客服，否未开启 */
    public Metadata customServiceConf;


    @Override
    public AssiDataShop ruleHandler() {
        handler1(customServiceConf, homePageConf);
        handler2(shopRecommendConf);
        return this;
    }

    @Override
    public int getUnFinished() {
        int num = unFinished(homePageConf, shopRecommendConf, customServiceConf);
        log.debug("Shop unFinished Num:{}", num);
        return num;
    }
}