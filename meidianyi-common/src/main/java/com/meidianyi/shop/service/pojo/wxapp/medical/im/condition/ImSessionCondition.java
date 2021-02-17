package com.meidianyi.shop.service.pojo.wxapp.medical.im.condition;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月22日
 */
@Data
public class ImSessionCondition {
    private Byte status;

    private Timestamp lessCreateTime;

    private Timestamp limitTime;

    private List<String> orderSns;

    private Integer doctorId;

    private Integer userId;

    private List<Byte> statusList;
}
