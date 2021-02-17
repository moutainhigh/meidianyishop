package com.meidianyi.shop.service.pojo.shop.doctor.comment;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/8/26 10:22
 */
@Data
public class DoctorCommentIdParam {
    @NotNull
    private Integer id;

    /**
     *
     */
    private Byte status  = BaseConstant.YES;
}
