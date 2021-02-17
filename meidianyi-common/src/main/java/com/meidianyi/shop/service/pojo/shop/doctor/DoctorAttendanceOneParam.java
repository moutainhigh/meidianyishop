package com.meidianyi.shop.service.pojo.shop.doctor;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorDo;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListVo;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author chenjie
 */
@Data
public class DoctorAttendanceOneParam{
    private Integer doctorId;
    private String name;
    private Timestamp lastTime;
    private Integer loginDays;
    private BigDecimal loginRate;
    private Integer loginRank=1;
    private Integer neededNum;
}
