package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.reduce;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

/**
 * 商品详情-限时降价
 * @author 李晓冰
 * @date 2020年01月07日
 */
@Getter
@Setter
public class ReducePriceMpVo extends GoodsActivityDetailMp {

    /**是否限购 */
    private Boolean isLimit;
    /**限购数量*/
    private Integer limitAmount;
    /**是否进行超购限制*/
    private Boolean limitFlag;
    /**规格信息列表*/
    List<ReducePricePrdMpVo> reducePricePrdMpVos;
    /**活动开始时间点*/
    private Timestamp nextStartTimestamp;
    /**活动结束时间点*/
    private Timestamp currentEndTimestamp;
}
