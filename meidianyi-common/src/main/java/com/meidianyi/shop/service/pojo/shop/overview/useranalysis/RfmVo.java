package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.List;

/**
 * RFM统计
 * @author liangchen
 * @date 2019.11.13
 */
@Data
public class RfmVo {
    /** 最近消费时间类型：1最近5天内，2最近5到10天，3最近10到30天，4最近30到90天，5最近90到180天，6最近180到365天，7最近365天以上',*/
    private Byte recencyType;
    /** 一行的数据 */
    private List<RfmRowVo> rfmRowVo;
}
