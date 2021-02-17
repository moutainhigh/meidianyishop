package com.meidianyi.shop.service.pojo.shop.department;

import lombok.Data;

import java.util.List;

/**
 * 科室列表入参
 *
 * @author chenjie
 * 2020年7月2日
 */
@Data
public class DepartmentListParam {
    private Integer nav;
    private Integer currentPage;
    private Integer pageRows;
    private String keyword;
    private Integer doctorId;
    private List<Integer> departmentIds;
    private Integer limitNum;
    private Integer departmentRecommendType;
    private Integer consultationRate;
    private Integer inquiryRate;
    private Integer doctorRate;

    @Override
    public String toString() {
        return "DepartmentListParam{" +
            "nav=" + nav +
            ", currentPage=" + currentPage +
            ", pageRows=" + pageRows +
            ", keyword='" + keyword + '\'' +
            ", doctorId=" + doctorId +
            ", departmentIds=" + departmentIds +
            ", limitNum=" + limitNum +
            '}';
    }
}
