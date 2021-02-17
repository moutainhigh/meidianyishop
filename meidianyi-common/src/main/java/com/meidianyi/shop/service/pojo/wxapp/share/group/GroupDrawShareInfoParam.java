package com.meidianyi.shop.service.pojo.wxapp.share.group;

import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 拼团抽奖-分享活动-入参
 * @author 李晓冰
 * @date 2020年02月03日
 */
@Getter
@Setter
public class GroupDrawShareInfoParam extends GoodsShareBaseParam {
    
    /**小程序二维码需要跳转的页面地址*/
    private Byte pageType;
}
