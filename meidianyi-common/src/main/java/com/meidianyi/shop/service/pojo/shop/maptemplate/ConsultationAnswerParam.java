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
public class ConsultationAnswerParam {
    public static final String REMARK = "请尽快处理，24小时后将超时关闭。";
    private String doctorName;
    private String patientName;
    private String consultationContent;
    @Builder.Default
    private String remark= REMARK;
    private List<Integer> userIds;
}
