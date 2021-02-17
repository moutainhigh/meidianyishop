package com.meidianyi.shop.service.shop.activity.factory;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年10月30日
 */
@Service
public class ProcessorFactoryBuilder {

    @Autowired(required = false)
    List<AbstractProcessorFactory> factories;

    /**
     * 不可以手动创建
     */
    private ProcessorFactoryBuilder(){}

    @PostConstruct
    public void init(){
        if (factories == null || factories.size() == 0) {
            LoggerFactory.getLogger(ProcessorFactoryBuilder.class).warn("{} collect no factory",ProcessorFactoryBuilder.class);
        } else {
            LoggerFactory.getLogger(ProcessorFactoryBuilder.class).debug("ProcessorFactory: {}",factories);
        }
    }

    /**
     * 获取处理器工厂
     * @param factoryClazz 要获取的工厂的Clazz类型
     * @param <T> 目标工厂的泛型信息
     * @return 目标工厂对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProcessorFactory(Class<T> factoryClazz) {
        for (AbstractProcessorFactory factory : factories) {
            if (factory.getClass().equals(factoryClazz)){
                return (T)factory;
            }
        }
        return null;
    }
}
