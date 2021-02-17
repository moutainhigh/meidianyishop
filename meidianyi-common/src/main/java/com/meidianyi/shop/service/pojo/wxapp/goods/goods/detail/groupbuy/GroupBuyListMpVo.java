package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy;

import lombok.Data;

/**
 * 拼团-正在进行中信息
 * @author 李晓冰
 * @date 2019年12月23日
 */
@Data
public class GroupBuyListMpVo {
    /**开启的拼团项id*/
    private Integer groupId;
    /**团长名称*/
    private String userName;
    /**团长头像*/
    private String userAvatar;
    /**还差成团人数*/
    private Integer remainNum;

    /**拼团失效剩余时间*/
    private Long remainTime;
}
