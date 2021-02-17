package com.meidianyi.shop.service.pojo.wxapp.share.presale;

import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2020年02月05日
 */
@Getter
@Setter
public class PreSaleShareInfoParam extends GoodsShareBaseParam {
    /**定金，分享图片时默认宣传语使用*/
    private BigDecimal depositPrice;
}
