package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-16 19:04
 **/
@Data
public class UserPatientWithoutCheckCodeParam extends UserPatientParam{
    @NotNull
    private String name;
    @NotNull
    private String mobile;
    @NotNull
    private String identityCode;
    /**
     * 增量查询时间
     */
    private Long startTime;
}
