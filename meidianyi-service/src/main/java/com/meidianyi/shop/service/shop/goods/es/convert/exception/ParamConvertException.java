package com.meidianyi.shop.service.shop.goods.es.convert.exception;


/**
 * param convert exception
 * @author 卢光耀
 * @date 2019/11/15 10:14 上午
 *
 */
public class ParamConvertException extends RuntimeException {


    private static final long serialVersionUID = 739390807613638700L;

    public ParamConvertException(String msg){
        super(msg);
    }
}
