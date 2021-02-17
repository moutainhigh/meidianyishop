package com.meidianyi.shop.service.pojo.wxapp.market.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

/**
 * 表单提交id
 * @author 孔德成
 * @date 2020/3/20
 */
@Data
@NoArgsConstructor
public class FormSubmitDataVo {
    /**
     * 提交id
     */
    private Integer submitId;
    /**
     * 0成功 1 表单失效 2表单不能频繁提交 3提交次数达到上限 4表单校验失败
     */
    private Byte status= NumberUtils.BYTE_ZERO;
    private String message;
}
