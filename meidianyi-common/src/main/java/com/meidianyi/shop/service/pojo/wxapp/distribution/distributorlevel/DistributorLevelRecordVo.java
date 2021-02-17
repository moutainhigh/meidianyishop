package com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author changle
 * @date 2020/8/17 9:17 上午
 */
@Data
public class DistributorLevelRecordVo {
    /**原等级ID*/
    @JsonIgnore
    private Byte oldLevel;
    /**新等级ID*/
    @JsonIgnore
    private Byte newLevel;
    /**变更时间*/
    private Timestamp createTime;
    /**等级升降描述*/
    private String levelRecordDesc;
}
