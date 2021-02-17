package com.meidianyi.shop.service.pojo.shop.medical.goods.entity;

import com.meidianyi.shop.service.pojo.shop.medical.goods.base.GoodsMedicalBaseInfo;
import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class GoodsMedicalInfoEntity extends GoodsMedicalBaseInfo {
    private Integer id;
    private Integer goodsId;

}
