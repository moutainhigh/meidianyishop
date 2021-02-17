package com.meidianyi.shop.service.pojo.shop.doctor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorDo;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListVo;
import lombok.Data;

import java.sql.Date;
import java.util.List;

/**
 * @author chenjie
 */
@Data
public class DoctorOneParam extends DoctorDo {
    /**
     * 科室
     */
    @JsonIgnore
    private List<DepartmentOneParam> departmentList;
    /**
     * 职称
     */
    private String    titleName;
    /**
     * 科室id
     */
    private List<Integer> departmentIds;
    /**
     * 科室名称
     */
    private List<String> departmentNames;
    private String departmentIdsStr;
    /**
     * 医师评价
     */
    @JsonIgnore
    private PageResult<DoctorCommentListVo> commentList;
    private String hospitalName;
    private String departmentName;
    private Boolean isAttention=false;
    private String answerHour;
    private Boolean hasUndoneOrder=false;
    /**
     * 响应时间区间1：10分钟内，2：半小时内，3：1小时内，4：1小时以上
     */
    private Byte answerType;
    private Integer answerMunite;
    private Integer answerHourInt;

    /**
     * 字符串显示响应时间
     */
    private String avgAnswerTimeNotSecond;
}
