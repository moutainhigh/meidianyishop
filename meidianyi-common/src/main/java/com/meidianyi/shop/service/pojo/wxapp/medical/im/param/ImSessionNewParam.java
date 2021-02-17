package com.meidianyi.shop.service.pojo.wxapp.medical.im.param;

import lombok.Data;

/**
 * 新增会话参数
 * @author 李晓冰
 * @date 2020年07月22日
 */
@Data
public class ImSessionNewParam {
    private Integer doctorId;
    private Integer userId;
    private Integer patientId;
    private String orderSn;
}
