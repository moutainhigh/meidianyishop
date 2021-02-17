package com.meidianyi.shop.service.shop.goods.es.convert;

import com.meidianyi.shop.service.shop.goods.es.convert.goods.EsGoodsConvertInterface;
import com.meidianyi.shop.service.shop.goods.es.convert.param.EsParamConvertInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * 转换器工厂类
 * @author 卢光耀
 * @date 2019/11/1 6:05 下午
 *
*/
public class EsConvertFactory {

    private static final Map<Class<?>, EsParamConvertInterface> PARAM_MAP = new HashMap<>();


    public static EsParamConvertInterface getParamConvert(Class<?> clz){
        EsParamConvertInterface imp = PARAM_MAP.get(clz);
        if( imp == null ){
            try {
                imp = (EsParamConvertInterface)clz.newInstance();
                PARAM_MAP.put(clz,imp);
            }catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return imp;
    }
}
