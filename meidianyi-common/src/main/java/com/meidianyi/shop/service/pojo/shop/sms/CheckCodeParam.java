package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-12 15:23
 **/

@Data
public class CheckCodeParam {

    private Integer shopId;

    private Integer userId;

    private String mobile;

    private String mobileCheckCode;

}
