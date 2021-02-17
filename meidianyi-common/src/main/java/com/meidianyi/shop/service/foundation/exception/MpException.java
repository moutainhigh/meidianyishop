package com.meidianyi.shop.service.foundation.exception;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;

/**
 * MpException:处理
 *
 * @author 王帅
 *
 */
public class MpException extends Exception {

	private static final long serialVersionUID = 6088595731089992032L;
	private JsonResultCode errorCode;
	private String[] codeParam;

    public Object getErrorResult() {
        return errorResult;
    }

    public void setErrorResult(Object errorResult) {
        this.errorResult = errorResult;
    }

    private Object errorResult;
	
	public MpException(JsonResultCode errorCode, String message) {
		super(message);
		this.setErrorCode(errorCode);
	}

	public MpException(JsonResultCode errorCode) {
		this.setErrorCode(errorCode);
	}

	public MpException(JsonResultCode errorCode, String message, String... codeParam) {
		super(message);
		this.setErrorCode(errorCode);
		this.setCodeParam(codeParam);
	}
	
	public MpException(Exception e){
		super(e);
	}
	public JsonResultCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(JsonResultCode errorCode) {
		this.errorCode = errorCode;
	}

	public String[] getCodeParam() {
		return codeParam;
	}

    public Object[] getCodeParamWrapper () {
        return codeParam == null ? null : Lists.newArrayList(codeParam).toArray();
    }

	public void setCodeParam(String[] codeParam) {
		this.codeParam = codeParam;
	}

    public static MpException initErrorResult(JsonResultCode errorCode, Object errorResult, String... codeParam) {
        MpException mpException = new MpException(errorCode, null , codeParam);
        mpException.setErrorResult(errorResult);
        return mpException;
    }
}
