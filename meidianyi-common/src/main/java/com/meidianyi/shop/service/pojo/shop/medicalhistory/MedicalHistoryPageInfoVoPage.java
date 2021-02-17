package com.meidianyi.shop.service.pojo.shop.medicalhistory;

import com.meidianyi.shop.common.foundation.util.PageResult;
import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-10-19 10:31
 **/
@Data
public class MedicalHistoryPageInfoVoPage<T> {

    /**
     * 是否有就诊人 0：无 1：是
     */
    private Byte isHavePatient;

    private PageResult pageResult;
}
