package com.meidianyi.shop.service.pojo.shop.doctor.comment;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/8/12 15:45
 */
@Data
public class DoctorCommentListParam extends BasePageParam {


    private Integer doctorId;
    private String doctorCode;
    private String doctorName;
    private Byte stars;
    /**
     * 0:未审批,1:审批通过,2:审批未通过
     */
    private Byte auditStatus;

    private Byte hasDelete;
    private Integer userId;
    private String sort;
}
