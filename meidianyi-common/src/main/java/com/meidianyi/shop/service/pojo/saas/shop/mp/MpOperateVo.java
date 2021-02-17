package com.meidianyi.shop.service.pojo.saas.shop.mp;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年08月07日
 */
@Data
public class MpOperateVo {

    private Integer templateId;

    private Timestamp createTime;

    private String appId;

    private String nickName;

    private String memo;

    private String userVersion;
    
    @JsonIgnore
    @JsonProperty("memo_list")
    private String memoList;
    @JsonIgnore
    @JsonProperty("memo_id")
    private String memoId;
}
