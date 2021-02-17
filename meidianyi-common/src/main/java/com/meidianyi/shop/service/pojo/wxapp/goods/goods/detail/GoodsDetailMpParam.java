package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 小程序-商品详情参数
 * @author 李晓冰
 * @date 2019年11月07日
 */
@Data
public class GoodsDetailMpParam {
    private Byte fromPage;

    private Integer goodsId;

    private Integer userId;

    /**用户当前位置经度*/
    private String lon;
    /**
     * 用户当前位置纬度
     */
    private String lat;

    /**
     * 指定该商品的详情页营销活动id
     */
    private Integer activityId;
    /**
     * 指定该商品的详情页营销活动类型
     */
    private Byte activityType;
    /**分销商品改价信息*/
    private GoodsRebateConfigParam rebateConfig;

    /**
     * 分享有礼--分享者的ID
     */
    private Integer shareAwardLaunchUserId;
    /**
     * 分享有礼--分享有礼活动ID
     */
    private Integer shareAwardId;

    private String userGrade;

    /**
     * 分销二维码对应参数id，
     * 由于时间原因此处未能对二维码进行参数统一解析，
     * 可以和分销活动一起进行代码抽取
     */
    @JsonProperty("rebateSId")
    private Integer rebateSid;
}
