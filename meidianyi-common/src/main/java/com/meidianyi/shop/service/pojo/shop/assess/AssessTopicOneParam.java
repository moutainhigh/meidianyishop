package com.meidianyi.shop.service.pojo.shop.assess;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class AssessTopicOneParam {
	private Integer   id;
    private Integer   shopId;
    private Integer   assessId;
    private Byte      topicType;
    private String    topicTypePath;
    private String    topicTitle;
    private Byte      topicLevel;
    private Byte      bgImgType;
    private String    bgImgPath;
    private Byte      optionType;
    private Byte      optionSkipType;
    private Integer   optionSkipValue;
    private String    optionContent;
    private Integer   resultId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Byte      delFlag;
}
