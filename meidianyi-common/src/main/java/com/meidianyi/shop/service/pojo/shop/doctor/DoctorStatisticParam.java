package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author chenjie
 * @date 2020年09月09日
 */
@Data
public class DoctorStatisticParam {
    private Integer shopId;
    private Integer doctorId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte type;
    private Date refDate;
    private String doctorName;
    private Integer departmentId;
    private List<Integer> doctorIds;
    private Integer currentPage;
    private Integer pageRows;
    /**
     * 排序字段
     */
    private String orderField;
    /**
     * 排序方式
     */
    private String orderDirection;
}
