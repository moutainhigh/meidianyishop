package com.meidianyi.shop.service.shop.order.action.base;

import java.util.HashMap;
import java.util.Map;

import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;

/**
 * 获取订单service
 *
 * @author 王帅
 *
 */
@Component
public class OrderOperateFactory implements ApplicationContextAware {

    private  Map<OrderServiceCode, IorderOperate<AbstractOrderOperateQueryParam,AbstractOrderOperateQueryParam>> orderOperateMap;

    @SuppressWarnings("unchecked")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取实现IorderOperate接口并加入ioc管理的实例
        Map<String, IorderOperate> map = applicationContext.getBeansOfType(IorderOperate.class);
        orderOperateMap = new HashMap<OrderServiceCode, IorderOperate<AbstractOrderOperateQueryParam, AbstractOrderOperateQueryParam>>();
        map.forEach((key, value) -> {
            boolean noExists = orderOperateMap.get(value.getServiceCode()) == null;
            //防止实现IorderOperate接口的类的ServiceCode重复
            Assert.isTrue(noExists,"防止实现IorderOperate接口的类的ServiceCode重复");
            orderOperateMap.put(value.getServiceCode(), value);
        });
    }

    /**
     * 通过传出param取其ServiceCode并调用execute
     *
     * @param info 执行参数
     * @return 执行结果
     * @throws MpException
     */
    public ExecuteResult orderOperate(AbstractOrderOperateQueryParam info) {
        return getService(info.getServiceCode()).execute(info);
    }

    /**
     * 通过传出param取其ServiceCode并调用query
     *
     * @param param 查询参数
     * @return 执行结果
     * @throws MpException
     */
    public Object orderQuery(AbstractOrderOperateQueryParam param) throws MpException {
        return getService(param.getServiceCode()).query(param);
    }

    /**
     * 根据传入的code获得处理该业务的service
     * @param code OrderServiceCode
     * @return IorderOperate的实现类
     */
    public IorderOperate<AbstractOrderOperateQueryParam,AbstractOrderOperateQueryParam> getService(OrderServiceCode code) {
        return orderOperateMap.get(code);
    }


}
