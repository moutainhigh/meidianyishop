package com.meidianyi.shop.service.pojo.shop.medical.goods.param;

import com.meidianyi.shop.common.foundation.util.Page;
import lombok.Data;

/**
 * 拉取药房商品信息
 * @author 李晓冰
 * @date 2020年08月26日
 */
@Data
public class MedicalGoodsExternalStoreRequestParam {
    private Long startTime;
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = 200;
    private String shopSn;
}
