package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 商品副图
 * @author 李晓冰
 * @date 2020年07月06日
 */
@Data
@NoArgsConstructor
public class GoodsImgDo {
    private Integer imgId;
    private Integer goodsId;
    private String imgUrl;
    private Timestamp createTime;
    private Timestamp updateTime;

    public GoodsImgDo(Integer goodsId, String imgUrl) {
        this.goodsId = goodsId;
        this.imgUrl = imgUrl;
    }
}
