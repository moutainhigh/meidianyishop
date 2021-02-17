package com.meidianyi.shop.service.pojo.shop.department;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 科室列表出参
 * @author chenjie
 * 2020年7月2日
 */
@Data
public class DepartmentListVo {
    private Integer id;
    /**
     * 科室代码
     */
    private String code;
    /**
     * 科室名称
     */
    private String name;
    /**
     * 父节点ID
     */
    private Integer parentId;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 是否叶子节点
     */
    private byte isLeaf;
    private List<DepartmentListVo> childDepartmentList;
    private Integer doctorNumber;
    private BigDecimal consultationNumber;
    private BigDecimal inquiryMoney;
    private BigDecimal inquiryNumber;
    private BigDecimal prescriptionMoney;
    private BigDecimal prescriptionNum;
}
