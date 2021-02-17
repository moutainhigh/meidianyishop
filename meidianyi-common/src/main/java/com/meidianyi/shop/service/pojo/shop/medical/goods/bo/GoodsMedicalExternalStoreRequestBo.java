package com.meidianyi.shop.service.pojo.shop.medical.goods.bo;

import lombok.Data;

import java.util.List;

/**
 * 药店接口请求参数
 * @author 李晓冰
 * @date 2020年08月26日
 */
@Data
public class GoodsMedicalExternalStoreRequestBo {
    private Integer totalCount;
    private Integer pageRows;
    private Integer curPage;
    private Integer pageSize;
    private List<GoodsMedicalExternalRequestItemBo> dataList;
}
