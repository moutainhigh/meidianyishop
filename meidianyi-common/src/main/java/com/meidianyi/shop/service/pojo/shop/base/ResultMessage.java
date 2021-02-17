package com.meidianyi.shop.service.pojo.shop.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;

/**
 * 用于传递可变的消息
 *
 * @author 孔德成
 * @date 2019/10/16 16:01
 */
@Getter
@Builder(builderMethodName ="builder")
public class ResultMessage {

    @Builder.Default
    private Boolean flag = false;
    private JsonResultCode jsonResultCode;
    @Singular
    private List<Object> messages;
}
