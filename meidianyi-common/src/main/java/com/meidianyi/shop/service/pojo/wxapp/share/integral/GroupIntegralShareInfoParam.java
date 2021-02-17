package com.meidianyi.shop.service.pojo.wxapp.share.integral;

import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 瓜分积分海报参数
 * @author 李晓冰
 * @date 2020年05月15日
 */
@Getter
@Setter
public class GroupIntegralShareInfoParam extends GoodsShareBaseParam {
    /**团id*/
    private Integer groupId;
}
