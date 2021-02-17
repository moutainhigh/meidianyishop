package com.meidianyi.shop.service.pojo.shop.medical.goods.bo;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月16日
 */
@Data
public class GoodsMedicalExternalRequestBo {
    private Integer totalCount;
    private Integer pageRows;
    private Integer curPage;
    private Integer pageSize;
    private List<GoodsMedicalExternalRequestItemBo> dataList;
}
