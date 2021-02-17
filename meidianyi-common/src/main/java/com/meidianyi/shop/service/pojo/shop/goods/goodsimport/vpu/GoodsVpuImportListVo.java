package com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu;

import lombok.Data;

import java.sql.Timestamp;

/**
 *商品Excel导入操作记录查询返回vo
 * @author 李晓冰
 * @date 2020年03月26日
 */
@Data
public class GoodsVpuImportListVo {
    private Integer id;
    private Integer totalNum;
    private Integer successNum;
    private Byte isUpdate;
    private Timestamp createTime;
    private String importFilePath;
}
