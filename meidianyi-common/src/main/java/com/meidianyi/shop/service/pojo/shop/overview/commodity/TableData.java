package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author liufei
 * @date 2/11/2020
 */
@Data
@Builder
public class TableData {
    List<String> refDate;
    Set<String> goodsName;
    Object[][] arrayData;
}
