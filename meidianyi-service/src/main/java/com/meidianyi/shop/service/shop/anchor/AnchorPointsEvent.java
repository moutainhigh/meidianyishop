package com.meidianyi.shop.service.shop.anchor;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.pojo.shop.table.AnchorPointsDo;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPotionEventBo;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPotionKeyBo;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 锚点事件
 * @author 孔德成
 * @date 2020/8/28 17:06
 */
@Getter
public enum AnchorPointsEvent {

    /**
     * 下单(点击数量)
     */
    INTO_SEARCH("into_search","进入搜索页","路径来源","药品购买"),
    INTO_GOODS_DETAIL("into_goods_detail","进入详情页","路径来源","药品购买"),
    ADD_CART("add_cart","添加到购物车","路径来源","药品购买"),
    CREATE_ORDER_SUBMIT_SOURCE("create_order_submit","提交订单","订单来源","药品购买"),
    CREATE_ORDER_SUBMIT_MONEY("create_order_submit","提交订单","订单金额","药品购买"),
    CREATE_ORDER_SUBMIT_COUNT("create_order_submit","提交订单","药品数量","药品购买"),
    CREATE_ORDER_SUBMIT_KIND("create_order_submit","提交订单","药品种类","药品购买"),
    CREATE_ORDER_SUBMIT_USER_MONEY("create_order_submit","提交订单","使用余额","药品购买"),
    ORDER_PAY_SUCCESS("order_pay_success","支付成功","路径来源","药品购买"),

    /**
     * 登录
     */
    LOGIN_WXAPP("login_wxapp","登录小程序","点击","1"),
    HOME_PAGE_SYN("home_page_syn","首页弹窗去同步","点击","1"),
    HOME_PAGE_SYN_MY_MEDICAL("home_page_syn_my_medical","首页我的病历","点击","1"),
    MY_FAMILY_ADD_PATIENT("my_family_add_patient","我的家人-去添加","点击","1"),
    CREATE_ORDER_DETAIL_ADD_PATIENT("create_order_detail_add_patient","订单结算页-去添加","点击","1"),
    DOCTOR_ONLINE_CHAT("doctor_online_chat","医师咨询-在线咨询","点击","1"),
    CREATE_PATIENT_SUCCESS("create_patient_success","添加就诊人成功","点击","1"),

    /**
     * 医师
     */
    DOCTOR_ENTER_IN("doctor_enter_in","进入医师端","打卡","1");


    /**
     * 埋点事件
     */
    private final String event;
    /**
     * 事件名称
     */
    private final String eventName;
    /**
     * 锚点类型
     */
    private final String key;
    /**
     * 模块
     */
    private final String module;




    AnchorPointsEvent(String event, String eventName, String key, String module){
        this.event =event;
        this.eventName =eventName;
        this.key = key;
        this.module = module;
    }


    public static AnchorPointsEvent getInstance(String event, String key) {
        for (AnchorPointsEvent pEvent : AnchorPointsEvent.values()) {
            if (Objects.equals(pEvent.event, event) && Objects.equals(pEvent.key, key)) {
                return pEvent;
            }
        }
        return null;
    }

    public static List<AnchorPotionEventBo> eventKeyMap(){
        Map<AnchorPotionEventBo,List<AnchorPotionKeyBo>> map =new HashMap<>();
        Arrays.stream(AnchorPointsEvent.values()).forEach(anchorPointsEvent -> {
            if (!map.containsKey(anchorPointsEvent.getEventBo())){
                map.put(anchorPointsEvent.getEventBo(), Lists.newArrayList(anchorPointsEvent.getKeyBo()));
            }else {
                map.get(anchorPointsEvent.getEventBo()).add(anchorPointsEvent.getKeyBo());
            }
        });
        map.forEach(AnchorPotionEventBo::setKeys);
        return Lists.newArrayList(map.keySet());
    }

    public AnchorPotionEventBo getEventBo(){
        AnchorPotionEventBo bo =new AnchorPotionEventBo();
        bo.setEvent(this.event);
        bo.setEventName(this.eventName);
        return bo;
    }

    public AnchorPotionKeyBo getKeyBo(){
        AnchorPotionKeyBo bo =new AnchorPotionKeyBo();
        bo.setKey(this.key);
        return bo;
    }

    public AnchorPointsDo getDo(){
        AnchorPointsDo anchorPointsDo =new AnchorPointsDo();
        anchorPointsDo.setEvent(this.event);
        anchorPointsDo.setEventName(this.eventName);
        anchorPointsDo.setModule(this.module);
        return anchorPointsDo;
    }


}
