package com.meidianyi.shop.service.pojo.shop.maptemplate;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月18日
 */
@Data
@Builder
public class ConsultationSuccessParam {
    public static final String REMARK = "您的医生已接诊，请查看";
    public static final String SERVICE_NAME = "在线问诊";
    private String patientName;
    private String doctorName;
    private String receiveTime;
    @Builder.Default
    private String remark=REMARK;
    @Builder.Default
    private String serviceName=SERVICE_NAME;
    private List<Integer> userIds;
}
