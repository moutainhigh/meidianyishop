package com.meidianyi.shop.service.pojo.shop.doctor;

import com.meidianyi.shop.common.pojo.shop.table.DoctorSummaryTrendDo;
import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年09月15日
 */
@Data
public class DoctorStatisticListVo extends DoctorSummaryTrendDo {
    private List<String> departmentNames;
    private Integer rank;
    private String name;
}
