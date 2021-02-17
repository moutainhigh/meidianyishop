package com.meidianyi.shop.service.pojo.wxapp.footprint;

import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 我的足迹列表
 * @author 孔德成
 * @date 2019/11/4 14:50
 */
@Getter
@Setter
public class FootprintListVo {

    List<FootprintDayVo> day;
    /**划线价开关 0关，1显示*/
    Byte delMarket;
    /**购物车按钮显示配置*/
    ShowCartConfig showCart;
    /**
     * 	分页信息
     */
    Page page;

}
