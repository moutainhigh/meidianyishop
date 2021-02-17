package com.meidianyi.shop.service.pojo.shop.doctor;

import com.meidianyi.shop.common.pojo.shop.table.DoctorDo;
import lombok.Data;

/**
 * @author chenjie
 */
@Data
public class DoctorConsultationOneParam extends DoctorDo {
    private String departmentName="";
    private String titleName;
    private Integer departmentId=0;
    private String hospitalName;
}
