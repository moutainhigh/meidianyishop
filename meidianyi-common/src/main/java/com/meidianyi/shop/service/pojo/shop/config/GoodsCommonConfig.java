package com.meidianyi.shop.service.pojo.shop.config;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年04月02日
 */
@Data
public class GoodsCommonConfig {
    /**
     *商品重量配置项开关
     */
    public Byte goodsWeightCfg;

    /**
     *商品条码配置项开关
     */
    public Byte needPrdCodes;

}
