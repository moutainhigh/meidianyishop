package com.meidianyi.shop.service.pojo.shop.medical.goods.param;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年10月13日
 */
@Data
public class GoodsExternalPageParam extends BasePageParam {
    private String goodsCommonName;
    private String goodsAliasName;
    private String goodsQualityRatio;
    private String goodsApprovalNumber;
    private String goodsProductionEnterprise;
    private Byte pageListFrom;
    private Byte direction;
}
