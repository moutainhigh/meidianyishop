package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/8
 */
@Data
public class FormDetailVo{
    private Integer pageId;
    private String pageName;
    /**
     * 活动状态 0未发布，1已发布 2已关闭 3 已删除
     */
    private Byte state;
    private Byte status;
    private String statusText;
    private String pageContent;
    private String formCfg;
    private Timestamp startTime;
    private Timestamp endTime;
    /**有效期*/
    private Byte isForeverValid;
    private Integer submitNum;

    public void setStatus(int status) {
        this.status =(byte)status;
    }
}
