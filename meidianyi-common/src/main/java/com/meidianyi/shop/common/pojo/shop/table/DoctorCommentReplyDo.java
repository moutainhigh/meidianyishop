package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/8/25 15:34
 */
@Data
public class DoctorCommentReplyDo {

    private Integer   id;
    private Integer   commentId;
    private String    replyNote;
    private Byte      isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;

}
