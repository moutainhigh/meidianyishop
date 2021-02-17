package com.meidianyi.shop.common.pojo.saas.api;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年07月17日
 */
@Data
public class ApiExternalGateResult {
    private Integer code = ApiExternalGateConstant.ERROR_CODE_SUCCESS;
    private String msg = ApiExternalGateConstant.ERROR_CODE_SUCCESS_MSG;
    private Object data = new Object[]{};

    public ApiExternalGateResult() {
    }

    public ApiExternalGateResult(Object data) {
        this.data = data;
    }

    public ApiExternalGateResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
