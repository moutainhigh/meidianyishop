package com.meidianyi.shop.service.pojo.shop.doctor.comment;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加医师评价
 * @author 孔德成
 * @date 2020/8/12 15:20
 */
@Data
public class DoctorCommentAddParam {

    /**
     * 评论内容
     */
    @NotNull
    private String commNote;
    /**
     * 打星
     */
    private Byte stars;
    /**
     * 是否匿名
     */
    @NotNull
    private Byte isAnonymou;
    /**
     * 医师id
     */
    @NotNull
    private Integer doctorId;
    /**
     * 医师名称
     */
    private String doctorName;
    /**
     * 医师编码
     */
    private String doctorCode;
    /**
     * 会话id
     */
    @NotNull
    private Integer imSessionId;
    /**
     * 患者id
     */
    @NotNull
    private Integer patientId;
    /**
     * 患者名称
     */
    private String patientName;
    /**
     * 订单号
     */
    @NotNull
    private String orderSn;
    private Integer orderId;

    private Integer userId;

    private Byte auditStatus;

    private String    userName;

}
