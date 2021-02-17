package com.meidianyi.shop.service.pojo.wxapp.share.rebate;

import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 分销
 * @author 李晓冰
 * @date 2020年04月27日
 */
@Getter
@Setter
public class RebateShareInfoParam extends GoodsShareBaseParam {
    private String rebateConfig;
}
