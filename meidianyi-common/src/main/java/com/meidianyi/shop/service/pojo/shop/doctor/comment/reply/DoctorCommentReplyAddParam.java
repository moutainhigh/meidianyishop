package com.meidianyi.shop.service.pojo.shop.doctor.comment.reply;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加回复
 * @author 孔德成
 * @date 2020/8/25 16:23
 */
@Data
public class DoctorCommentReplyAddParam {

    @NotNull
    private Integer   commentId;
    @NotNull
    private String    replyNote;

}
