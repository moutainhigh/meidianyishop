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
public class ConsultationOrderPayParam {
    public static final String REMARK = "请您及时接诊";
    public static final String ORDER_TYPE = "在线问诊";
    private String doctorName;
    private String patientData;
    @Builder.Default
    private String orderType=ORDER_TYPE;
    private String orderSn;
    private String createTime;
    private String diseaseDetail;
    @Builder.Default
    private String remark=REMARK;

    private List<Integer> userIds;
}
