package com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微铺宝excel商品导入
 * @author 李晓冰
 * @date 2020年03月18日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVpuExcelImportMqParam {
    private List<GoodsVpuExcelImportBo> goodsVpuExcelImportBos;
    private String lang;
    private boolean isUpdate;
    private Integer batchId;
    private Integer shopId;
    /**
     * 任务id
     */
    private Integer taskJobId;
}
