package com.meidianyi.shop.service.pojo.shop.user.message;

import java.util.EnumMap;

import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;

/**
 * 小程序模版和公众号模版映射
 * @author 卢光耀
 * @date 2019-08-22 09:53
 *
*/
public class MaAndMpTemplate {


    public static final EnumMap<MaTemplateConfig,MpTemplateConfig> TEMPLATE_MAP;

    static {
        TEMPLATE_MAP = new EnumMap<MaTemplateConfig, MpTemplateConfig>(MaTemplateConfig.class);
        TEMPLATE_MAP.put(MaTemplateConfig.ACTIVITY_CONFIG,MpTemplateConfig.ACTIVITY_CONFIG);
    }

}
