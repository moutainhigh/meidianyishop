package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/13
 */
@Data
public class FormCopyVo {
    private String pageName;
    private String pageContent;
    private String formCfg;
    private Timestamp startTime;
    private Timestamp endTime;
    /**有效期*/
    private Byte isForeverValid;
}
