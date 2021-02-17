package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;
import org.jooq.types.UInteger;

import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-02-17
 */
@Data
public class UserRemarkListVo {
    /**
     * 备注ID
     */
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 备注内容
     */
    private String remark;
    /**
     * 添加时间
     */
    private Timestamp addTime;
    /**
     * 删除状态 0：未删除；1：删除
     */
    private Byte isDelete;
}
