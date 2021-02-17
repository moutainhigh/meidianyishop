package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Data;

import java.sql.Timestamp;

/**
 *
 * @author 卢光耀
 * @date 2019/10/30 9:46 上午
 *
*/
@Data
public class GoodsLabelAndCouple {
    private Integer labelId;

    private Integer gtaId;

    private Byte type;

    private Timestamp createTime;

    private Short level;
}
