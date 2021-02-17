package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/7
 */
@Data
public class FormAddParam {
    private Integer pageId;
    @NotNull
    private String pageName;
    @NotNull
    private Byte status;
    private String pageContent;
    private String formCfg;
    private Timestamp startTime;
    private Timestamp endTime;
    /**有效期*/
    @NotNull
    private Byte validityPeriod;
}
