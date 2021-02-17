package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 标签关联表
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class GoodsLabelCoupleDo {
    private Integer id;
    private Integer labelId;
    private Integer gtaId;
    private Byte type;
    private Timestamp createTime;
    private Timestamp updateTime;
}
