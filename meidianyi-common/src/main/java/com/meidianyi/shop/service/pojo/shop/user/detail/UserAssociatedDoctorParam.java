package com.meidianyi.shop.service.pojo.shop.user.detail;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-14 17:18
 **/
@Data
public class UserAssociatedDoctorParam {

    @NotNull(message = "用户不能为空")
    private Integer userId;

    private String doctorName;

    private String departmentName;

    private Byte isFavorite;

    /**
     * 分页查询参数
     */
    private Integer currentPage;
    private Integer pageRows ;

}
