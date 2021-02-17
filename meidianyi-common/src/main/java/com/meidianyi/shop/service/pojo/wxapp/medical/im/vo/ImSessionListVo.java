package com.meidianyi.shop.service.pojo.wxapp.medical.im.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 小程序会话列表
 * @author 李晓冰
 * @date 2020年07月23日
 */
@Data
public class ImSessionListVo {
    /**会话id*/
    private Integer id;
    /**订单号*/
    private String orderSn;
    /**医生id*/
    private Integer doctorId;
    /**医师名称*/
    private String doctorName;
    /**科室id*/
    private Integer departmentId;
    /**科室名*/
    private String departmentName;
    /**患者id*/
    private Integer patientId;
    /**患者名*/
    private String patientName;
    /**用户id*/
    private Integer userId;
    /**会话状态*/
    private Byte sessionStatus;
    /**是否有新信息*/
    private Long newMsgNum;
    /**评价状态*/
    private Byte evaluateStatus;

    private Timestamp createTime;
    /**订单金额**/
    private BigDecimal orderAmount;
}
