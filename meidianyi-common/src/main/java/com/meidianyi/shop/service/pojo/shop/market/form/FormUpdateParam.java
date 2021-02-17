package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/12
 */
@Data
public class FormUpdateParam {
    @NotNull
    private Integer pageId;
    @NotNull
    private Integer shopId;
    private String pageName;
    private Byte status;
    private Byte state;
    private String pageContent;
    private String formCfg;
    private Timestamp startTime;
    private Timestamp endTime;
    /**有效期*/
    private Byte validityPeriod;
    /**反馈数量*/
    private Integer submitNum;
}
