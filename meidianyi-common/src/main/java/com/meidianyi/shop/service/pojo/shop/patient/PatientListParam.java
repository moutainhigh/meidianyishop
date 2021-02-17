package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author chenjie
 */
@Data
public class PatientListParam {
    private Integer nav;
    private Integer currentPage;
    private Integer pageRows;
    private String name;
    private String mobile;
    private Integer userId = 0;
    private List<Integer> patientIds;
    /**
     * 注册时间上界
     */
    private Timestamp startTime;
    /**
     * 注册时间下界
     */
    private Timestamp endTime;

    @Override
    public String toString() {
        return "PatientListParam{" +
            "nav=" + nav +
            ", currentPage=" + currentPage +
            ", pageRows=" + pageRows +
            ", name='" + name + '\'' +
            ", mobile='" + mobile + '\'' +
            ", userId=" + userId +
            ", patientIds=" + patientIds +
            '}';
    }
}
