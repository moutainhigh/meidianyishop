package com.meidianyi.shop.service.pojo.shop.goods.goodsimport;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;

import lombok.Data;

/**
 * 导入封装插入状态
 * @author 李晓冰
 * @date 2020年05月26日
 */
@Data
public class ImportResultCodeWrap {
    JsonResultCode resultCode;
    /**本次导入操作处理号*/
    Integer batchId;
}
