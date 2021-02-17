package com.meidianyi.shop.service.shop.goods.es.convert.exception;

/**
 * goods convert exception
 * @author 卢光耀
 * @date 2019/11/15 10:14 上午
 *
*/
public class GoodsConvertException extends RuntimeException {
    private static final long serialVersionUID = -6022853948697939973L;


    public GoodsConvertException(String msg){
        super(msg);
    }
}
