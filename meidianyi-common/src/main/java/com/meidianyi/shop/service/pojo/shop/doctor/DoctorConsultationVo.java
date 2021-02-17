package com.meidianyi.shop.service.pojo.shop.doctor;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentOneParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleOneParam;
import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 */
@Data
public class DoctorConsultationVo {
    private PageResult<DoctorConsultationOneParam> doctorList;
    private List<DepartmentOneParam> departmentList;
    private List<TitleOneParam> titleList;
}
