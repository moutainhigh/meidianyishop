package com.meidianyi.shop.service.pojo.shop.title;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author chenjie
 */
@Data
public class TitleOneParam {
    private Integer id;
    /**
     * 职称名称
     */
    private String name;
    private String code;
    private Byte isDelete=0;
    private Timestamp createTime;
    private Integer first=0;
    /**
     * 该职称人数
     */
    private Integer doctorNum;
}
