package com.meidianyi.shop.service.pojo.shop.medical.goods.param;

import com.meidianyi.shop.common.foundation.util.Page;
import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年07月16日
 */
@Data
public class MedicalGoodsExternalRequestParam{
    private Long startTime;
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = 100;
}
