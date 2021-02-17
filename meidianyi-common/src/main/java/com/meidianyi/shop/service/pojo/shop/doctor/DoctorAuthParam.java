package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author 赵晓东
 * @Create 2020-07-22 14:25
 **/

@Data
public class DoctorAuthParam {
    /**
     * 医师姓名
     */
    @NotNull(message = "医师姓名不能为空")
    private String doctorName;
    /**
     * 医师电话
     */
    @NotNull(message = "手机号不能为空")
    private String mobile;
    /**
     * 医师医院唯一编码
     */
    @NotNull(message = "院内编码不能为空")
    private String hospitalCode;
    /**
     * 当前用户id
     */
    private Integer userId;
    /**
     * 验证码
     */
    @NotNull(message = "验证码不能为空")
    private String mobileCheckCode;
    /**
     * 医师签名
     */
    private String signature;

}
