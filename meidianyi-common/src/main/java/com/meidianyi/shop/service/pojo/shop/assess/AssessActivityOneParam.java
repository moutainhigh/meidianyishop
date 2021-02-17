package com.meidianyi.shop.service.pojo.shop.assess;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class AssessActivityOneParam {
	private Integer   id;
    private Integer   shopId;
    private String    actCode;
    private String    actName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte      dueTimeType;
    private Byte      partTimesType;
    private Integer   partTimesDay;
    private Integer   partTimesTotal;
    private Integer   feedbackTotal;
    private Byte      assessJudgeType;
    private Byte      coverStyleType;
    private String    coverStyle;
    private String    assessDesc;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Byte      isBlock;
    private Byte      delFlag;
    private Byte      pubFlag;
}
