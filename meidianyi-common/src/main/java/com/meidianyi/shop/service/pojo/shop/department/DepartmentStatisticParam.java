package com.meidianyi.shop.service.pojo.shop.department;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author chenjie
 * @date 2020年09月09日
 */
@Data
public class DepartmentStatisticParam {
    private Integer shopId;
    private Integer departmentId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte type;
    private Date refDate;
}
