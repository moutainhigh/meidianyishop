package com.meidianyi.shop.controller;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.ValidatorConfig;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;

import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Objects;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.LANGUAGE_TYPE_PARAM;
import static com.meidianyi.shop.config.ValidatorConfig.UNDEER_POINT;

/**
 * controller全局异常捕获处理
 * @author: 卢光耀
 * @date: 2019-07-10 11:22
 *
*/
@RestControllerAdvice(basePackages = "com.meidianyi.shop.controller")
public class ExceptionControllerHandler extends BaseController {

    Logger logger= LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public JsonResult request(MethodArgumentNotValidException e){
            BindingResult result = e.getBindingResult();
            logger.error("valid msg:"+result.getFieldError().getDefaultMessage());
            if( result.hasErrors() ){
                logger.error("error msg: "+result.getFieldError().getField() +result.getFieldError().getDefaultMessage());
                return this.fail(JsonResultCode.CODE_PARAM_ERROR,","+paramErrorMessage(result)+" "+result.getFieldError().getDefaultMessage());
            }
        return null;
    }

    public String paramErrorMessage( BindingResult result){
        FieldError fieldError = result.getFieldError();
        ConstraintViolationImpl source = FieldsUtil.getFieldValueByFieldName("source", fieldError, ConstraintViolationImpl.class);
        String messageTemplate = source != null ? source.getMessageTemplate() : "";
        if ( messageTemplate.startsWith(ValidatorConfig.VALID_MESSAGE_PREFIX)&&!messageTemplate.contains(ValidatorConfig.JAVAX_ORG_HIBERNATE_KEY)){
            return "";
        }
        String field = Objects.requireNonNull(result.getFieldError()).getField();
        String leftBracket = "[";
        if (field.contains(leftBracket)){
            field = field.replaceAll("[\\[\\d+\\]]", "");
        }
        String objectName = result.getObjectName();
        String errorCode = result.getFieldError().getCode();
        String errorMessage =objectName+ UNDEER_POINT+field+UNDEER_POINT+errorCode;
        String paramMessage= Util.translateMessage(getLang(), errorMessage,null, LANGUAGE_TYPE_PARAM);
        if (StringUtils.isEmpty(paramMessage)){
            String message =objectName+UNDEER_POINT+field;
            paramMessage= Util.translateMessage(getLang(), message,null, LANGUAGE_TYPE_PARAM);
            if (StringUtils.isEmpty(paramMessage)){
                paramMessage = Util.translateMessage(getLang(), field, null, LANGUAGE_TYPE_PARAM);
            }
        }
        if (StringUtils.isEmpty(paramMessage)){
            paramMessage=field;
        }
        return paramMessage;
    }

    @ExceptionHandler(BindException.class)
    public Object validExceptionHandler(BindException e){
        BindingResult result = e.getBindingResult();
        if( result.hasErrors() ){
            return this.fail(JsonResultCode.CODE_PARAM_ERROR,", "+result.getFieldError().getField() + result.getFieldError().getDefaultMessage());
        }
        return null;
    }
    /**
     * 对于WxErrorException的统一处理
     * @param e
     * @return
     */
    @ExceptionHandler({WxErrorException.class})
    public JsonResult procesErrorException(WxErrorException e){
    	return fail(JsonResultCode.WX_ERROR_EXCEPTION,e.getError().getErrorCode(),e.getError().getErrorMsg());
    }

    /**
     * json参数转换错误处理
     *
     * @param e
     * @return
     * @throws IOException
     */
     @ExceptionHandler(HttpMessageNotReadableException.class)
    public JsonResult request1(HttpMessageNotReadableException e) throws IOException {
         if (e.getCause()==null){
             logger.debug("valid msg:"+e.getMessage());
             return fail(JsonResultCode.CODE_PARAM_ERROR);
         }
         logger.error("valid msg:"+e.getCause().getMessage());
        return fail(JsonResultCode.CODE_PARAM_ERROR);
    }

    /**
     * 处理参数异常
     *
     * @author 郑保乐
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public JsonResult handleArgumentExceptions(IllegalArgumentException e) {
        return fail(e.getMessage());
    }

    /**
     * 常规业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public JsonResult businessException(BusinessException e){
        if (e.getCode() != null) {
            if (e.getArgs() != null && e.getArgs().length != 0) {
                return fail(e.getCode(), e.getArgs());
            }
            return fail(e.getCode());
        } else {
            return fail(e.getErrorMessage());
        }
    }

    @ExceptionHandler({MpException.class})
    public JsonResult procesMpException(MpException e){
        return result(e.getErrorCode(), e.getErrorResult(), e.getCodeParamWrapper());
    }
}
