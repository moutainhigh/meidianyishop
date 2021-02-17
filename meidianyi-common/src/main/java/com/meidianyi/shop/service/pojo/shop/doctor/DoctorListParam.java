package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chenjie
 */
@Data
public class DoctorListParam {

    /**
     * 前台传入的控制排序方向
     */
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    /**
     * 待排序字段
     */
    public static final String AVG_COMMENT_STAR = "avgCommentStar";
    public static final String AVG_ANSWER_TIME = "avgAnswerTime";
    public static final String ATTENTION_NUMBER = "attentionNumber";
    public static final String CONSULTATION_NUMBER = "consultationNumber";
    public static final String CONSULTATION_PRICE = "consultationPrice";

    private Integer nav;
    private Integer currentPage;
    private Integer pageRows;
    private String name;
    private String doctorNo;
    private String departmentName;
    private List<Integer> doctorIds;
    private Integer titleId;
    private Byte sex;
    private BigDecimal consultationMoneyMix;
    private BigDecimal consultationMoneyMax;

    @Override
    public String toString() {
        return "DoctorListParam{" +
            "nav=" + nav +
            ", currentPage=" + currentPage +
            ", pageRows=" + pageRows +
            ", name='" + name + '\'' +
            ", doctorNo='" + doctorNo + '\'' +
            ", departmentName='" + departmentName + '\'' +
            ", doctorIds=" + doctorIds +
            '}';
    }

    /**
     * 排序字段
     */
    private String orderField;
    /**
     * 排序方式
     */
    private String orderDirection;
}
