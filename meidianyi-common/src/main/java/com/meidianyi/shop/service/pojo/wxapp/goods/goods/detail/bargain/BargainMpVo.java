package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.bargain;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序-商品详情-砍价详情
 * @author 李晓冰
 * @date 2019年12月25日
 */
@Data
public class BargainMpVo extends GoodsActivityDetailMp {
    /** 砍价价格 */
    private BigDecimal bargainPrice;

    /** 砍价类型 0定人砍价，1任意砍价 */
    private Byte bargainType;

    /** 砍价库存 */
    private Integer stock;

    /**活动运费 0 不免运费 1 免运费*/
    private Byte freeShip;

    /** 砍价活动参与的人次 */
    private Integer bargainJoinNum;

    /**
     * 如果已经发起了砍价，发起的记录ID
     */
    private Integer recordId;
}
