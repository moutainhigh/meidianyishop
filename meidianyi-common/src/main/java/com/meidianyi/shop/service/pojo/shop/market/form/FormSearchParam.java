package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import java.sql.Date;

/**
 * @author liufei
 * @date 2019/8/7
 */
@Data
public class FormSearchParam {
    private String pageName;
    private Date startTime;
    private Date endTime;
    private Byte status;
    private Integer currentPage;
    private Integer pageRows;

}
