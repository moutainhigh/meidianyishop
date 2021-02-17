package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.pojo.shop.summary.ChartInfo;
import lombok.Getter;

import java.util.Arrays;

/**
 * 访问来源 key 对应关系
 *
 * @author 郑保乐
 */
@Getter
public enum AccessSource implements ChartInfo {

    /** 访问来源 **/
    MP_HISTORY_LIST(1, "source.mp_history_list"),
    SEARCH(2, "source.search"),
    SESSION(3, "source.session"),
    QR_CODE_SCAN(4, "source.qr_code_scan"),
    OFFICIAL_ACCOUNT_HOME_PAGE(5, "source.official_account_home_page"),
    CHAT_TOP(6, "source.chat_top"),
    SYSTEM_DESKTOP(7, "source.system_desktop"),
    MP_HOME_PAGE(8, "source.mp_home_page"),
    NEARBY_MP(9, "source.nearby_mp"),
    OTHER(10, "source.other"),
    TEMPLATE_MSG(11, "source.template_msg"),
    OFFICIAL_ACCOUNT_MENU(13, "source.official_account_menu"),
    APP_SHARE(14, "source.app_share"),
    PAY_FINISH_PAGE(15, "source.pay_finish_page"),
    LONG_PRESS_QR_CODE(16, "source.long_press_qr_code"),
    GALLERY_QR_CODE(17, "source.gallery_qr_code"),
    OFFICIAL_ACCOUNT_ARTICLE(18, "source.official_account_article"),
    CASH_WALLET(19, "source.cash_wallet"),
    CARD_WALLET(20, "source.card_wallet"),
    OTHER_MP(22, "source.other_mp"),
    OTHER_MP_CALLBACK(23, "source.other_mp_callback"),
    COUPON_SHOP_LIST(24, "source.coupon_shop_list"),
    SEARCH_INPUT_SHORTCUT_ENTRY(25, "source.search_input_shortcut_entry"),
    MP_CUSTOM_SERVICE_MSG(26, "source.mp_custom_service_msg"),
    OFFICIAL_ACCOUNT_BROADCAST(27, "source.official_account_broadcast"),
    TASK_BAR_HISTORY(29, "source.task_bar_history"),
    LONG_PRESS_MP_MENU_ROUND_POINT(30, "source.long_press_mp_menu_round_point"),
    WIFI_CONNECTED_PAGE(31, "source.wifi_connected_page"),
    CITY_SERVICE(32, "source.city_service"),
    WX_AD(33, "source.wx_ad"),
    OTHER_MOBILE_APP(34, "source.other_mobile_app"),
    DISCOVER_ENTRY_MY_MP(35, "source.discover_entry_my_mp"),
    TASK_BAR_MY_MP(36, "source.task_bar_my_mp"),
    WX_CIRCLE(37, "source.wx_circle"),
    TOP_UP_PHONE(38, "source.top_up_phone"),
    H5(39, "source.h5"),
    PLUGIN(40, "source.plugin"),
    EVERYBODY_IS_USING(41, "source.everybody_is_using"),
    DISCOVERY_PAGE(42, "source.discovery_page"),
    FLOATING_WINDOW(43, "source.floating_window"),
    PEOPLE_NEARBY(44, "source.people_nearby"),
    TAKE_A_LOOK(45, "source.take_a_look"),
    CIRCLE_OF_FRIENDS(46, "source.circle_of_friends"),
    ENTERPRISE_WX(47, "source.enterprise_wx"),
    VIDEO(48, "source.video"),
    COLLECTION(49, "source.collection"),
    WX_RED_ENVELOPES(50, "source.wx_red_envelopes"),
    WX_GAME_CENTER(51, "source.wx_game_center"),
    SHAKE(52, "source.shake"),
    PUBLIC_SHOPPING_GUIDE_NEWS(53, "source.public_shopping_guide_news"),
    KNOWLEDGE(54, "source.knowledge"),
    MP_ORDER(55, "source.mp_order"),
    MP_LIVE(56, "source.mp_live"),
    GROUP_TOOLS(57, "source.group_tools");

    private Integer index;
    private String source;

    AccessSource(Integer index, String source) {
        this.index = index;
        this.source = source;
    }

    @Override
    public String getName() {
        return getSource();
    }

    @Override
    public Integer getKey() {
        return getIndex();
    }

    public static AccessSource findByIndex(Integer index) {
        return Arrays.stream(values())
                .filter(i -> i.getIndex().equals(index)).findFirst().orElseThrow(IllegalStateException::new);
    }
}
