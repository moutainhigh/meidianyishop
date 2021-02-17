package com.meidianyi.shop.common.pojo.saas.api;

import lombok.Data;

/**
 * 小程序-对接Pos,Erp服务接口返回数据
 * @author 李晓冰
 * @date 2020年03月30日
 */
@Data
public class ApiJsonResult {
    private Integer code = ApiExternalGateConstant.ERROR_CODE_SUCCESS;
    private String msg = ApiExternalGateConstant.ERROR_CODE_SUCCESS_MSG;
    /**Object[]{} 为目前php文档定义的默认值，当没有数据的时候返php返回的是一个[](对于返回单个对象但是该对象查询失败时返回的也是[],如果成功则data就是该Json对象)*/
    private Object data = new Object[]{};

    public ApiJsonResult() {
    }

    public ApiJsonResult(Object data) {
        this.data = data;
    }
    public ApiJsonResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
