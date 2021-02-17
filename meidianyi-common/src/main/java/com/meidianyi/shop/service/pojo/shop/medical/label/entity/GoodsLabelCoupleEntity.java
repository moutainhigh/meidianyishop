package com.meidianyi.shop.service.pojo.shop.medical.label.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
@NoArgsConstructor
public class GoodsLabelCoupleEntity {
    private Integer labelId;
    private Integer gtaId;
    private Byte type;

    public GoodsLabelCoupleEntity(Integer labelId, Integer gtaId, Byte type) {
        this.labelId = labelId;
        this.gtaId = gtaId;
        this.type = type;
    }
}
