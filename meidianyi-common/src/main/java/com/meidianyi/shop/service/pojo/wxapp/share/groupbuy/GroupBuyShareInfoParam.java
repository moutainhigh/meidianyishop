package com.meidianyi.shop.service.pojo.wxapp.share.groupbuy;

import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 拼团活动-分享小程序-入参
 * @author 李晓冰
 * @date 2019年12月27日
 */
@Getter
@Setter
public class GroupBuyShareInfoParam extends GoodsShareBaseParam {
    /**需要跳转的页面类型*/
    private Byte pageType;
    /**pageType=2时,groupId为用户开团的开团记录id*/
    private Integer groupId;
}
