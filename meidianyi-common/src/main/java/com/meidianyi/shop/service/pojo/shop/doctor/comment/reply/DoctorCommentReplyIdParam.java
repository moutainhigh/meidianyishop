package com.meidianyi.shop.service.pojo.shop.doctor.comment.reply;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/8/25 16:30
 */
@Data
public class DoctorCommentReplyIdParam {
    @NotNull
    private Integer id;
}
