package com.meidianyi.shop.service.pojo.shop.title;

import lombok.Data;

import java.util.Date;

/**
 * @author chenjie
 */
@Data
public class TitleFetchOneParam {
    private String positionCode;
    private String name;
    private Integer state;
    private Integer createTime;
    private Date lastUpdateTime;
}
